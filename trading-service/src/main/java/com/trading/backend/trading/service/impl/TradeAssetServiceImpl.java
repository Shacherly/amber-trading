package com.google.backend.trading.service.impl;

import com.google.backend.asset.common.model.base.PoolEntity;
import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.asset.common.model.base.SpotDetailEntity;
import com.google.backend.common.trade.TradeCommon;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.constant.RedisKeyConstants;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionAmountMapper;
import com.google.backend.trading.dao.model.TradePosition;
import com.google.backend.trading.dao.model.TradeTransactionAmount;
import com.google.backend.trading.mapstruct.margin.TradePositionMapStruct;
import com.google.backend.trading.model.margin.api.ActivePositionInfoVo;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import com.google.backend.trading.model.margin.dto.SettleAsset;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.MarginRiskEnum;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.service.KlineCoingeckoService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CoinUtil;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 交易资产数据获取
 *
 * @author trading
 * @date 2021/10/18 16:28
 */
@Slf4j
@Service
public class TradeAssetServiceImpl implements TradeAssetService {

    @Lazy
    @Autowired
    private AssetRequest assetRequest;
    @Autowired
    private TradePositionMapStruct tradePositionMapStruct;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private DefaultTradeTransactionAmountMapper defaultTradeTransactionAmountMapper;
    @Autowired
    private KlineCoingeckoService klineCoingeckoService;


    @Override
    public BigDecimal unpnl(List<TradePosition> tradePositions) {
        BigDecimal unpnl = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(tradePositions)) {
            for (TradePosition tradePosition : tradePositions) {
                BigDecimal positionUnpnlUsd = calculatePositionUnpnlUsd(tradePosition).getFirst();
                unpnl = unpnl.add(positionUnpnlUsd);
            }
        }
        return unpnl;
    }

    @Override
    public MarginInfo marginInfo(String uid, Map<String, PoolEntityForRisk> poolEntityForRiskMap, List<TradePosition> tradePositions,
                                 BigDecimal leverage, boolean earnPledge, boolean pnlConversion) {

        BigDecimal position = BigDecimal.ZERO;
        BigDecimal unpnl = BigDecimal.ZERO;
        BigDecimal openUnpnl = BigDecimal.ZERO;
        BigDecimal liquidUnpnl = BigDecimal.ZERO;
        List<ActivePositionInfoVo> activePositionInfoVos = new ArrayList<>();
        if (ListUtil.isNotEmpty(tradePositions)) {
            for (TradePosition tradePosition : tradePositions) {
                Pair<BigDecimal, BigDecimal> unpnlPair = calculatePositionUnpnlUsd(tradePosition);
                BigDecimal positionUnpnlUsd = unpnlPair.getFirst();
                BigDecimal positionOriginalUnpnl = unpnlPair.getSecond();
                if (positionUnpnlUsd.compareTo(BigDecimal.ZERO) > 0) {
                    openUnpnl = openUnpnl.add(positionUnpnlUsd.multiply(Constants.PROFIT_OPEN_HAIR_CUT_AVAILABLE));
                    liquidUnpnl = liquidUnpnl.add(positionUnpnlUsd.multiply(Constants.PROFIT_OPEN_HAIR_CUT_LIQUIDATION));
                } else {
                    openUnpnl = openUnpnl.add(positionUnpnlUsd.multiply(Constants.LOSS_OPEN_HAIR_CUT_AVAILABLE));
                    liquidUnpnl = liquidUnpnl.add(positionUnpnlUsd.multiply(Constants.LOSS_OPEN_HAIR_CUT_LIQUIDATION));
                }

                BigDecimal positionValue = calculatePositionValue(tradePosition);
                ActivePositionInfoVo dto = tradePositionMapStruct.tradePosition2ActivePositionInfoVo(tradePosition);
                dto.setUnpnl(positionUnpnlUsd);
                dto.setOriginalUnpnl(positionOriginalUnpnl);
                dto.setPosition(positionValue);
                dto.setUsedMargin(positionValue.divide(leverage, Constants.USD_PRECISION, RoundingMode.DOWN));
                activePositionInfoVos.add(dto);
                unpnl = unpnl.add(positionUnpnlUsd);
                position = position.add(positionValue);
            }
        }
        Pair<BigDecimal, BigDecimal> marginPair = calculateTotalMargin(poolEntityForRiskMap, earnPledge, openUnpnl, liquidUnpnl);
        BigDecimal totalOpenMargin = marginPair.getFirst();
        BigDecimal totalLiquidMargin = marginPair.getSecond();

        for (ActivePositionInfoVo dto : activePositionInfoVos) {
            this.calculateExtPositionInfo(totalOpenMargin, dto, pnlConversion);
        }

        if (ListUtil.isNotEmpty(activePositionInfoVos)) {
            activePositionInfoVos = activePositionInfoVos.stream().sorted(
                    Comparator.comparing(ActivePositionInfoVo::getUsedMargin).reversed()).collect(Collectors.toList());
        }

        PoolEntityForRisk poolEntityForRisk = poolEntityForRiskMap.get(Constants.BASE_COIN);
        BigDecimal credit = BigDecimal.ZERO;
        if (null != poolEntityForRisk) {
            credit = poolEntityForRisk.getCredit();
        }
        if (totalLiquidMargin.compareTo(BigDecimal.ZERO) == 0 || totalOpenMargin.compareTo(BigDecimal.ZERO) == 0) {
            return new MarginInfo(totalOpenMargin, totalLiquidMargin, BigDecimal.ZERO, BigDecimal.ZERO, totalOpenMargin,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    leverage, tradePositions, activePositionInfoVos, poolEntityForRiskMap);
        }
        BigDecimal usedMargin = totalOpenMargin.min(position.divide(leverage, Constants.DEFAULT_PRECISION, RoundingMode.DOWN));
        BigDecimal availableMargin = totalOpenMargin.subtract(usedMargin);

        BigDecimal currentLeverage = position.divide(totalLiquidMargin, Constants.DEFAULT_PRECISION, RoundingMode.DOWN);
        //维持保证金率
        BigDecimal keepRate = MarginRiskEnum.getByPositionValue(position).getRate();
        //风险率= 1 - (清算总保证金 - 持仓头寸 * 维持保证金率)/ max{清算总保证金 - 浮动盈亏， 清算总保证金}
        BigDecimal marginOccupy = totalLiquidMargin.subtract(position.multiply(keepRate));
        BigDecimal riskRate = BigDecimal.ONE;
        if (marginOccupy.compareTo(BigDecimal.ZERO) > 0 && totalLiquidMargin.compareTo(BigDecimal.ZERO) > 0) {
            riskRate = BigDecimal.ONE.subtract(marginOccupy.divide(totalLiquidMargin.subtract(liquidUnpnl).max(totalLiquidMargin),
                    Constants.DEFAULT_PRECISION, RoundingMode.DOWN));
        } else {
            currentLeverage = BigDecimal.ONE.divide(keepRate, Constants.DEFAULT_PRECISION, RoundingMode.DOWN);
        }
        BigDecimal canOpenUsd = availableMargin.multiply(leverage).max(BigDecimal.ZERO);
        BigDecimal usedCredit = usedMargin.min(credit);
        BigDecimal usedMarginWithoutCredit = usedMargin.subtract(credit).max(BigDecimal.ZERO);
        BigDecimal fundUtilization = usedMargin.divide(totalOpenMargin, Constants.RISK_RATE_PRECISION, RoundingMode.DOWN);
        return new MarginInfo(
                totalOpenMargin.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                totalLiquidMargin.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                position.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                usedMargin.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                availableMargin.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                unpnl.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                currentLeverage.setScale(Constants.LEVERAGE_PRECISION, RoundingMode.DOWN),
                riskRate.setScale(Constants.RISK_RATE_PRECISION, RoundingMode.DOWN),
                canOpenUsd.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                usedCredit.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                usedMarginWithoutCredit.setScale(Constants.USD_PRECISION, RoundingMode.DOWN),
                fundUtilization,
                leverage,
                tradePositions,
                activePositionInfoVos,
                poolEntityForRiskMap
        );
    }


    @Override
    public void calculateExtPositionInfo(BigDecimal totalMargin, ActivePositionInfoVo dto, boolean pnlConversion) {
        BigDecimal usedMargin = totalMargin.min(dto.getUsedMargin());
        dto.setUsedMargin(usedMargin);
        //浮动盈亏收益率unpnlRate=unpnl/仓位价格相对quote
        BigDecimal unpnlRate = BigDecimal.ZERO;
        if (dto.getUsedMargin().compareTo(BigDecimal.ZERO) != 0) {
            unpnlRate = dto.getUnpnl().divide(usedMargin, Constants.SHOW_PRECISION, RoundingMode.DOWN);
        }
        dto.setUnpnlRate(unpnlRate);
        if (pnlConversion) {
            dto.setUnpnlCoin(Constants.BASE_COIN);
        } else {
            Pair<String, String> coinPair = CommonUtils.coinPair(dto.getSymbol());
            String quote = coinPair.getSecond();
            dto.setUnpnl(dto.getOriginalUnpnl());
            dto.setUnpnlCoin(quote);
        }
        //预计资金费用expectedFundingCost
        BigDecimal expectedFundingCost = CoinDomain.positionFundingCost(dto.getSymbol(),
                dto.getDirection(), dto.getQuantity());
        dto.setExpectedFundingCost(expectedFundingCost);
        BigDecimal fundingCostRate = CoinDomain.fundingCostRate(dto.getSymbol(), dto.getDirection());
        //反向
        dto.setFundingCostRate(fundingCostRate.negate());
    }

    @Override
    public void setUserAmountUsd2DB(String uid, String coin, BigDecimal amount, String transId) {
        if (!Objects.equals(Constants.BASE_COIN, coin)) {
            String usdSymbol = coin + Constants.BASE_QUOTE;
            SymbolDomain symbolDomain = SymbolDomain.nullableGet(usdSymbol);
            if (symbolDomain == null) {
                BigDecimal price = klineCoingeckoService.getPrice(coin);
                if (price != null) {
                    amount = price.multiply(amount);
                } else {
                    AlarmLogUtil.alarm("setUserAmountUsd2DB get symbol:{} symbolDomain null", usdSymbol);
                    return;
                }
            } else {
                BigDecimal midPrice = symbolDomain.midPrice();
                amount = midPrice.multiply(amount);
            }
        }
        TradeTransactionAmount tradeTransactionAmount = new TradeTransactionAmount();
        tradeTransactionAmount.setAmount(amount);
        tradeTransactionAmount.setTransId(transId);
        tradeTransactionAmount.setUid(uid);
        tradeTransactionAmount.setCtime(CommonUtils.getNowTime());
        defaultTradeTransactionAmountMapper.insertSelective(tradeTransactionAmount);
    }

    @Override
    public BigDecimal get30TradeAmount(String uid) {
        RBucket<BigDecimal> bucket = redissonClient.getBucket(RedisKeyConstants.get30DUserTradeAmountKey(uid));
        BigDecimal userTrade30DAmount = bucket.get();
        if (userTrade30DAmount == null) {
            userTrade30DAmount = BigDecimal.ZERO;
        }
        return userTrade30DAmount;
    }

    public static void main(String[] args) {
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));
        LocalDateTime start = end.minusDays(30);
        System.out.println("start:" + start + ",end:" + end);
    }

    /**
     * 获取总保证金
     *
     * @param poolEntityMap
     * @param earnPledge
     * @param openUnpnl
     * @param liquidUnpnl
     * @return first 是建仓总保证金 second 是清算总保证金
     */
    @Override
    public Pair<BigDecimal, BigDecimal> calculateTotalMargin(Map<String, PoolEntityForRisk> poolEntityMap,
                                                             boolean earnPledge,
                                                             BigDecimal openUnpnl, BigDecimal liquidUnpnl) {
        BigDecimal openTotalMargin = BigDecimal.ZERO;
        BigDecimal liquidTotalMargin = BigDecimal.ZERO;
        for (Map.Entry<String, PoolEntityForRisk> entry : poolEntityMap.entrySet()) {
            String coin = entry.getKey();
            PoolEntityForRisk pool = entry.getValue();

            BigDecimal openTotal;
            BigDecimal liquidTotal;
            BigDecimal total = BigDecimal.ZERO;
            total = total.add(pool.getBalance());
            if (earnPledge) {
                total = total.add(pool.getEarnLocked());
            }
            total = total.add(pool.getCredit());

            openTotal = total;
            liquidTotal = total;

            CoinDomain coinDomain = CoinDomain.nullableGet(coin);
            if (null == coinDomain) {
                //非交易币种
                continue;
            }

            BigDecimal coinOpenHairCut = coinDomain.getCommonConfig().getHairCutAvailable();
            BigDecimal coinLiquidHairCut = coinDomain.getCommonConfig().getHairCutLiquidation();
            if (coinOpenHairCut.compareTo(BigDecimal.ZERO) == 0 || coinLiquidHairCut.compareTo(BigDecimal.ZERO) == 0) {
                log.warn("haircut exception, coinOpenHairCut or coinLiquidHairCut is zero, coinDomain = {}", coinDomain);
                //跳过haircut为0的币种，不支持进行交易
                continue;
            }

            // 使用对手方第一档价格
            BigDecimal buyPrice = CoinUtil.getBaseCoinSymbolPrice(coin, Direction.BUY);
            BigDecimal sellPrice = CoinUtil.getBaseCoinSymbolPrice(coin, Direction.SELL);

            //负资产时 抵扣率为1
            if (openTotal.compareTo(BigDecimal.ZERO) > 0) {
                openTotal = openTotal.multiply(coinOpenHairCut);
            }

            //负资产时 抵扣率为1
            if (liquidTotal.compareTo(BigDecimal.ZERO) > 0) {
                liquidTotal = liquidTotal.multiply(coinLiquidHairCut);
            }

            //对手价格来计算保证金
            BigDecimal openMargin;
            BigDecimal liquidMargin;
            if (openTotal.compareTo(BigDecimal.ZERO) >= 0) {
                openMargin = openTotal.multiply(sellPrice);
            } else {
                openMargin = openTotal.multiply(buyPrice);
            }
            openTotalMargin = openTotalMargin.add(openMargin);

            if (liquidTotal.compareTo(BigDecimal.ZERO) >= 0) {
                liquidMargin = liquidTotal.multiply(sellPrice);
            } else {
                liquidMargin = liquidTotal.multiply(buyPrice);
            }
            liquidTotalMargin = liquidTotalMargin.add(liquidMargin);


            // 现货锁定计算
            List<SpotDetailEntity> spotLockEntityList = pool.getSpotDetail();
            if (CollectionUtils.isNotEmpty(spotLockEntityList)) {
                BigDecimal openSpotMargin = BigDecimal.ZERO;
                BigDecimal liquidSpotMargin = BigDecimal.ZERO;
                for (SpotDetailEntity entity : spotLockEntityList) {
                    String receiveCoin = entity.getCoin();
                    BigDecimal originalMargin = entity.getAmount();
                    BigDecimal receiveCoinOpenHairCut =
                            CoinDomain.nonNullGet(receiveCoin).getCommonConfig().getHairCutAvailable();
                    BigDecimal receiveCoinLiquidHairCut =
                            CoinDomain.nonNullGet(receiveCoin).getCommonConfig().getHairCutLiquidation();
                    //取现货相关的两个币种各自的抵扣率的最小值
                    BigDecimal openMinHairCut = coinOpenHairCut.min(receiveCoinOpenHairCut);
                    BigDecimal liquidMinHairCut = coinLiquidHairCut.min(receiveCoinLiquidHairCut);
                    openSpotMargin = openSpotMargin.add(originalMargin.multiply(sellPrice).multiply(openMinHairCut));
                    liquidSpotMargin = liquidSpotMargin.add(originalMargin.multiply(sellPrice).multiply(liquidMinHairCut));
                }
                openTotalMargin = openTotalMargin.add(openSpotMargin);
                liquidTotalMargin = liquidTotalMargin.add(liquidSpotMargin);
            }

        }
        log.info("margin without unpnl, open = {}, liquid = {}", openTotalMargin, liquidTotalMargin);
        //加上 unpnl以及抵扣率
        openTotalMargin = openTotalMargin.add(openUnpnl);
        liquidTotalMargin = liquidTotalMargin.add(liquidUnpnl);
        log.info("margin add unpnl, open = {}, liquid = {}, openUnpnl = {}, liquidUnpnl = {}", openTotalMargin, liquidTotalMargin,
                openUnpnl, liquidUnpnl);
        return Pair.of(openTotalMargin, liquidTotalMargin);
    }

    /**
     * pnl转换成usd后的数额
     *
     * @param position
     * @return first = usd unpnl, second = original unpnl
     */
    @Override
    public Pair<BigDecimal, BigDecimal> calculatePositionUnpnlUsd(TradePosition position) {
        String symbol = position.getSymbol();
        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String quoteCoin = coinPair.getSecond();
        BigDecimal originalPnl = getOriginalPnl(position);
        // 计算完原始unPnl，用对手价计算usd计价
        BigDecimal price;
        if (originalPnl.compareTo(BigDecimal.ZERO) > 0) {
            price = CoinUtil.getBaseCoinSymbolPrice(quoteCoin, Direction.SELL);
        } else {
            price = CoinUtil.getBaseCoinSymbolPrice(quoteCoin, Direction.BUY);
        }
        return Pair.of(price.multiply(originalPnl).setScale(Constants.PNL_PRECISION, RoundingMode.HALF_UP), originalPnl);
    }

    /**
     * 原始仓位pnl
     *
     * @param position 仓位
     * @return pnl base计价
     */
    @Override
    public BigDecimal getOriginalPnl(TradePosition position) {
        String direction = position.getDirection();
        String symbol = position.getSymbol();
        BigDecimal currentPrice;
        // 用对手价
        if (direction.equals(TradeCommon.BUY)) {
            currentPrice = SymbolDomain.nonNullGet(symbol).price(Direction.SELL);
        } else {
            currentPrice = SymbolDomain.nonNullGet(symbol).price(Direction.BUY);
        }
        BigDecimal pnl;
        if (direction.equals(Direction.BUY.getName())) {
            pnl = currentPrice.subtract(position.getPrice()).multiply(position.getQuantity());
        } else {
            pnl = position.getPrice().subtract(currentPrice).multiply(position.getQuantity());
        }
        return pnl;
    }

    /**
     * 计算仓位头寸，使用中间价
     *
     * @param position 仓位
     * @return hairCut
     */
    @Override
    public BigDecimal calculatePositionValue(TradePosition position) {
        String symbol = position.getSymbol();
        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String baseCoin = coinPair.getFirst();
        if (Constants.BASE_COIN.equals(baseCoin)) {
            return position.getQuantity();
        }
        String newSymbol = baseCoin + Constants.BASE_QUOTE;
        BigDecimal price = SymbolDomain.nonNullGet(newSymbol).midPrice();
        return position.getQuantity().multiply(price);
    }

    @Override
    public BigDecimal spotAvailable(String uid, String symbol, Direction direction) {
        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String baseCoin = coinPair.getFirst();
        String quoteCoin = coinPair.getSecond();
        String availableCoin;
        String comCoin;
        if (Direction.BUY == direction) {
            availableCoin = quoteCoin;
            comCoin = baseCoin;
        } else {
            availableCoin = baseCoin;
            comCoin = quoteCoin;
        }
        return assetRequest.assetSpotAvailable(uid, availableCoin, comCoin);
    }

    @Override
    public SettleAsset settleAvailable(TradePosition position) {
        String symbol = position.getSymbol();
        String uid = position.getUid();
        String direction = position.getDirection();
        BigDecimal price = position.getPrice();
        BigDecimal quantity = position.getQuantity();
        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String baseCoin = coinPair.getFirst();
        String quoteCoin = coinPair.getSecond();
        Map<String, PoolEntity> poolEntityMap = assetRequest.assetPool(uid);
        BigDecimal available;
        BigDecimal settleAvailable;
        if (Direction.BUY.getName().equals(direction)) {
            available = poolEntityMap.get(quoteCoin).getBalance();
            settleAvailable = available.divide(price, Constants.DEFAULT_PRECISION, RoundingMode.DOWN).min(quantity);
        } else {
            available = poolEntityMap.get(baseCoin).getBalance();
            settleAvailable = available.min(quantity);
        }
        settleAvailable = settleAvailable.max(BigDecimal.ZERO);
        return new SettleAsset(available, settleAvailable);
    }
}
