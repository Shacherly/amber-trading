package com.google.backend.trading.service.impl;

import com.google.backend.asset.common.model.trade.req.TradeCurrencyConversion;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.config.TradeProperties;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeMarginOrderMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.model.TradeMarginOrder;
import com.google.backend.trading.dao.model.TradeMarginOrderExample;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderExample;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.crex.CrexNodeEnum;
import com.google.backend.trading.model.crex.CrexTypeEnum;
import com.google.backend.trading.model.pdt.CreateTradeReq;
import com.google.backend.trading.model.pdt.CreateTradeRes;
import com.google.backend.trading.model.spot.api.SpotAvailableReq;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.FrozenType;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TriggerType;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.service.OrderRequest;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 定义订单相关的资源整合接口
 *
 * @author savion.chen
 * @date 2021/9/30 14:43
 */
@Slf4j
@Service
public class OrderRequestImpl implements OrderRequest {

    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;
    @Autowired
    private DefaultTradeSpotOrderMapper defaultTradeSpotOrderMapper;
    @Autowired
    private DefaultTradeMarginOrderMapper defaultTradeMarginOrderMapper;
    @Autowired
    private TradeProperties properties;


    private static final BigDecimal minBuffer = new BigDecimal("1.02");


    @Override
    public void validateLockQuantity(TradeSpotOrder order) {
        BigDecimal lockQuantity;
        if (!OrderType.isTriggerOrder(order.getType())) {
            lockQuantity = calcFreezeFunds(order, order.getQuantity(), order.getPrice());
        } else {
            lockQuantity = calcFreezeFunds(order, order.getQuantity(), order.getTriggerPrice());
        }
        if (lockQuantity.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_AMOUNT_TOO_LARGE);
        }
    }

    @Override
    public void validateQuantity(BigDecimal qty, String symbol, Boolean isFok, Boolean isQuote, BigDecimal price,
                                 boolean ignoreMinOrderNumCheck) {
        BigDecimal minOrder = CommonUtils.getMinOrder(symbol);
        BigDecimal maxOrder = isFok ? CommonUtils.getFokMaxOrder(symbol) : CommonUtils.getMaxOrder(symbol);
        BigDecimal checkQty;
        if (isQuote) {
            if (null == price) {
                price = CommonUtils.getMiddlePrice(symbol);
            }
            checkQty = CommonUtils.roundDivide(qty, price);
        } else {
            checkQty = qty;
        }
        if (!ignoreMinOrderNumCheck && checkQty.compareTo(minOrder) < 0) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_AMOUNT_TOO_SMALL);
        }
        if (checkQty.compareTo(maxOrder) > 0) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_AMOUNT_TOO_LARGE);
        }
    }

    @Override
    public Boolean validateMinQuantity(String symbol, BigDecimal quantity, Boolean isFok) {
        BigDecimal minOrder = CommonUtils.getMinOrder(symbol);
        return minOrder.compareTo(quantity) < 1;
    }

    @Override
    public void validatePricePrecision(OrderType orderType, String symbol, BigDecimal price, BigDecimal triggerPrice) {
        if (orderType.isLimitOrder()) {
            int pricePrecision = CommonUtils.getPrecision(symbol);
            if (price == null || price.stripTrailingZeros().scale() > pricePrecision) {
                throw new RuntimeException("price precision is illegal");
            }
            if (orderType.isTriggerOrder()) {
                if (triggerPrice == null || triggerPrice.stripTrailingZeros().scale() > pricePrecision) {
                    throw new RuntimeException("triggerPrice precision is illegal");
                }
            }
        }
    }

    @Override
    public void validateMaxPendingNum(String uid) {
        long spotCount = getPendingSpotOrderNum(uid);
        if (spotCount >= Constants.MAX_PEND_ORDER_NUM) {
            throw new BusinessException(BusinessExceptionEnum.OVER_ORDER_MAXIMUM_NUM);
        }
        long marginCount = getPendingMarginOrderNum(uid);
        if (spotCount + marginCount >= Constants.MAX_PEND_ORDER_NUM) {
            throw new BusinessException(BusinessExceptionEnum.OVER_ORDER_MAXIMUM_NUM);
        }
    }

    @Override
    public void validatePriceStatus(String symbol) {
        if (!SymbolDomain.nonNullGet(symbol).checkPlaceOrderPriceStatus()) {
            throw new BusinessException(BusinessExceptionEnum.TRADE_MARKET_UNDER_MAINTENANCE);
        }
    }

    private long getPendingSpotOrderNum(String uid) {
        TradeSpotOrderExample example = new TradeSpotOrderExample();
        TradeSpotOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        return defaultTradeSpotOrderMapper.countByExample(example);
    }

    private long getPendingMarginOrderNum(String uid) {
        TradeMarginOrderExample example = new TradeMarginOrderExample();
        TradeMarginOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusIn(OrderStatus.ACTIVE_STATUS);
        return defaultTradeMarginOrderMapper.countByExample(example);
    }

    @Override
    public boolean isSatisfyTrigger(BigDecimal triggerPrice, TriggerType compare, BigDecimal compPrice) {
        if (compare == TriggerType.GREATER) {
            return compPrice.compareTo(triggerPrice) >= 0;
        } else if (compare == TriggerType.LESS) {
            return compPrice.compareTo(triggerPrice) <= 0;
        }
        return false;
    }

    @Override
    public boolean isReachPrice(String symbol, Direction direct, BigDecimal price) {
        BigDecimal buyPrice = CommonUtils.getBuyPrice(symbol);
        BigDecimal sellPrice = CommonUtils.getSellPrice(symbol);
        log.info("check reach price, buy price = {}, sell price = {}, limit price = {}", buyPrice, sellPrice, price);
        if (direct == Direction.BUY) {
            return price.compareTo(buyPrice) >= 0;
        } else {
            return price.compareTo(sellPrice) <= 0;
        }
    }

    @Override
    public boolean checkOrderQuantity(BigDecimal qty, String symbol) {
        BigDecimal minOrder = CommonUtils.getMinOrder(symbol);
        BigDecimal maxOrder = CommonUtils.getMaxOrder(symbol);
        return CommonUtils.isWithinScope(qty, minOrder, maxOrder);
    }

    @Override
    public boolean isSmallQuantity(BigDecimal qty, String symbol) {
        BigDecimal minQty = CommonUtils.getMinOrder(symbol);
        return qty.compareTo(minQty) < 0;
    }

    @Override
    public BigDecimal getEntrustAmount(TradeSpotOrder order) {
        BigDecimal entrustAmount;
        if (Direction.isBuy(order.getDirection()) ^ order.getIsQuote()) {
            entrustAmount = order.getAmountFilled();
        } else {
            entrustAmount = order.getQuantityFilled();
        }
        return entrustAmount;
    }

    @Override
    public BigDecimal getTradeAmount(TradeSpotOrder order) {
        BigDecimal tradeAmount;
        if (Direction.isBuy(order.getDirection()) ^ order.getIsQuote()) {
            tradeAmount = order.getQuantityFilled();
        } else {
            tradeAmount = order.getAmountFilled();
        }
        return tradeAmount;
    }

    @Override
    public boolean isConversionCoin(String source) {
        SourceType type = SourceType.getByName(source);
        return (type == SourceType.AUTO_CONVERSION
                || type == SourceType.LIQUIDATION);
    }

    @Override
    public boolean notNeedFreezeAsset(String source) {
        SourceType type = SourceType.getByName(source);
        return (type == SourceType.LOAN_LIQUIDATION
                || type == SourceType.REPAY_WITH_COLLATERAL
                || type == SourceType.EARN_LIQUIDATION
                || type == SourceType.BUY_CRYPTOCURRENCY_CONVERSION);
    }

    @Override
    public boolean notNeedUnFreezeAsset(String source) {
        SourceType type = SourceType.getByName(source);
        return (type == SourceType.LOAN_LIQUIDATION
                || type == SourceType.REPAY_WITH_COLLATERAL
                || type == SourceType.BUY_CRYPTOCURRENCY_CONVERSION);
    }

    @Override
    public boolean isImmunityAsset(String source) {
        SourceType type = SourceType.getByName(source);
        return (type == SourceType.LOAN_LIQUIDATION
                || type == SourceType.REPAY_WITH_COLLATERAL
                || type == SourceType.EARN_LIQUIDATION);
    }


    @Override
    public CreateTradeReq getPlaceSpotOrder(TradeSpotOrder order, String tradeId) {
        BigDecimal placeQty = order.getQuantity();
        BigDecimal filledQty = order.getQuantityFilled();
        BigDecimal remainQty = CommonUtils.convertData(placeQty.subtract(filledQty));
        BigDecimal limitQty = order.getLockAmount();
        if (notNeedFreezeAsset(order.getSource()) || isConversionCoin(order.getSource())) {
            limitQty = calcFreezeFunds(order, order.getQuantity(), order.getPrice());
        }
        if (!CommonUtils.isPositive(remainQty) || limitQty.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("spot placeQty={} filledQty={} remainQty={}, order = {}", placeQty, filledQty, remainQty, order);
            return null;
        }

        CreateTradeReq placeReq = new CreateTradeReq();
        placeReq.setUserId(order.getUid());
        placeReq.setTradeId(tradeId);
        placeReq.setOrderId(order.getUuid());
        placeReq.setSymbol(order.getSymbol());
        String side = order.getDirection();
        placeReq.setDirection(side);

        if (order.getIsQuote()) {
            placeReq.setQuoteQuantity(remainQty);
        } else {
            placeReq.setQuantity(remainQty);
        }
        SpotAvailableReq sendReq = new SpotAvailableReq();
        sendReq.setSymbol(order.getSymbol());
        if (Direction.isBuy(order.getDirection())) {
            placeReq.setQuoteQuantityLimit(limitQty);
        } else {
            placeReq.setQuantityLimit(limitQty);
        }

        // 根据当前的订单类型，转化为去PDT执行的支持类型
        OrderType type = OrderType.getByName(order.getType());
        TradeStrategy lifeTime = TradeStrategy.getByName(order.getStrategy());
        if (type.isLimitOrder()) {
            placeReq.setPrice(order.getPrice());
            if (isSmallQuantity(remainQty, order.getSymbol())) {
                placeReq.setType(CrexTypeEnum.LIMIT_FILL_REST);
            } else {
                if (lifeTime == TradeStrategy.FOK) {
                    placeReq.setType(CrexTypeEnum.LIMIT_FOK);
                } else {
                    placeReq.setType(CrexTypeEnum.LIMIT_FAK);
                }
            }
        } else {
            placeReq.setType(CrexTypeEnum.MARKET);
        }

        // 附带的其他属性
        List<String> info = new ArrayList<String>();
        info.add(CrexNodeEnum.SPOT.getName());
        if (!isImmediately(type, lifeTime)) {
            info.add(CrexNodeEnum.GTC.getName());
        }
        SourceType source = SourceType.getByName(order.getSource());
        if (source == SourceType.LIQUIDATION) {
            info.add(CrexNodeEnum.LIQUIDATION.getName());
        } else if (source == SourceType.AUTO_CONVERSION) {
            info.add(CrexNodeEnum.PNL_SETTLE.getName());
        }
        placeReq.setNotes(info);
        return placeReq;
    }

    @Override
    public TradeCurrencyConversion getConversionReq(TradeSpotOrder order, String tradeId) {
        TradeCurrencyConversion sendReq = new TradeCurrencyConversion();
        sendReq.setReqId(tradeId);

        TradeCurrencyConversion.Params params = new TradeCurrencyConversion.Params();
        params.setUid(order.getUid());
        params.setFee(BigDecimal.ZERO);
        params.setDirection(order.getDirection().toLowerCase());
        SourceType type = SourceType.getByName(order.getSource());
        if (type == SourceType.AUTO_CONVERSION) {
            params.setTrigger(FrozenType.CONVERSION.getName());
        } else if (type == SourceType.LIQUIDATION) {
            params.setTrigger(FrozenType.DEDUCTION.getName());
        }

        String symbol = order.getSymbol();
        Pair<String, String> coinPair = CommonUtils.coinPair(symbol);
        String base = coinPair.getFirst();
        String quote = coinPair.getSecond();
        params.setBaseCoin(base);
        params.setQuoteCoin(quote);
        if (order.getIsQuote()) {
            params.setBaseAmount(order.getAmountFilled());
            params.setQuoteAmount(order.getQuantityFilled());
        } else {
            params.setBaseAmount(order.getQuantityFilled());
            params.setQuoteAmount(order.getAmountFilled());
        }

        //设置成交均价，用于计算持币均价，在新增的币种上覆盖成交均价即可
        if (Direction.isBuy(order.getDirection())) {
            BigDecimal avgPrice = order.getFilledPrice();
            if (!Constants.BASE_COIN.equals(quote)) {
                avgPrice = CommonUtils.getMiddlePrice(base + Constants.BASE_QUOTE).setScale(Constants.USD_PRICE_PRECISION,
                        RoundingMode.HALF_UP);
            }
            params.setBaseAvgPrice(avgPrice);
        } else {
            BigDecimal avgPrice = BigDecimal.ONE;
            if (!Constants.BASE_COIN.equals(quote)) {
                avgPrice = CommonUtils.getMiddlePrice(quote + Constants.BASE_QUOTE).setScale(Constants.USD_PRICE_PRECISION,
                        RoundingMode.HALF_UP);
            }
            params.setQuoteAvgPrice(avgPrice);
        }

        sendReq.setParams(params);
        return sendReq;
    }

    @Override
    public void calcAveragePrice(TradeSpotOrder order, CreateTradeRes info) {
        // 计算该笔订单的累计成交量
        BigDecimal newEntrustQty;
        BigDecimal newTradeQty;
        if (order.getIsQuote()) {
            newEntrustQty = order.getQuantityFilled().add(info.getQuoteFilled());
            newTradeQty = order.getAmountFilled().add(info.getFilled());
        } else {
            newEntrustQty = order.getQuantityFilled().add(info.getFilled());
            newTradeQty = order.getAmountFilled().add(info.getQuoteFilled());
        }
        order.setQuantityFilled(CommonUtils.convertData(newEntrustQty));
        order.setAmountFilled(CommonUtils.convertData(newTradeQty));

        // 计算累计成交的均价
        BigDecimal newPrice;
        if (order.getIsQuote()) {
            newPrice = CommonUtils.roundDivide(newEntrustQty, newTradeQty);
        } else {
            newPrice = CommonUtils.roundDivide(newTradeQty, newEntrustQty);
        }
        order.setFilledPrice(newPrice);

        // 计算该订单的总手续费
        BigDecimal saveFee = BigDecimal.ZERO;
        if (SourceType.isFromUser(order.getSource())) {
            BigDecimal tradeAmount = getTradeAmount(order);
            BigDecimal feeRate = getSpotFeeRate(order.getUid());
            saveFee = CommonUtils.convertData(tradeAmount.multiply(feeRate));
        }
        order.setFee(saveFee);
        log.info("calcAveragePrice isQuote={} quantity={} amount={} avg_price={}",
                order.getIsQuote(), newEntrustQty, newTradeQty, newPrice);
    }

    @Override
    public boolean isImmediately(OrderType type, TradeStrategy life) {
        return (type == OrderType.LIMIT && (life == TradeStrategy.FOK || life == TradeStrategy.IOC));
    }

    @Override
    public void calcOrderStatus(TradeSpotOrder order) {
        BigDecimal placeQty = order.getQuantity();
        BigDecimal filledQty = order.getQuantityFilled();
        if (filledQty.compareTo(placeQty) < 0) {
            OrderType orderType = OrderType.getByName(order.getType());
            TradeStrategy lifeTime = TradeStrategy.getByName(order.getStrategy());
            SourceType source = SourceType.getByName(order.getSource());
            BigDecimal limitQty = order.getLockAmount();
            if (notNeedFreezeAsset(order.getSource()) || isConversionCoin(order.getSource())) {
                limitQty = calcFreezeFunds(order, order.getQuantity(), order.getPrice());
            }
            // 一次性的订单给出终结态，否则为执行态
            if (CommonUtils.isSyncOrder(orderType, lifeTime, source) || limitQty.compareTo(BigDecimal.ZERO) <= 0) {
                setSpotFinishStatus(order);
            } else {
                order.setStatus(OrderStatus.EXECUTING.getName());
            }
        } else {
            order.setStatus(OrderStatus.COMPLETED.getName());
            if (filledQty.compareTo(placeQty) > 0) {
                log.error("trade quantity error={}", order);
            }
        }
    }

    @Override
    public void setSpotFinishStatus(TradeSpotOrder order) {
        BigDecimal placeQty = order.getQuantity();
        BigDecimal filledQty = order.getQuantityFilled();
        if (filledQty.compareTo(placeQty) < 0) {
            if (CommonUtils.isPositive(order.getQuantityFilled())) {
                order.setStatus(OrderStatus.PART_CANCELED.getName());
            } else {
                order.setStatus(OrderStatus.CANCELED.getName());
            }
        } else {
            // 防止资金更新失败，导致设置终结态不准确的问题
            order.setStatus(OrderStatus.COMPLETED.getName());
        }
    }

    @Override
    public void setMarginFinishStatus(TradeMarginOrder order) {
        BigDecimal placeQty = order.getQuantity();
        BigDecimal filledQty = order.getQuantityFilled();
        if (filledQty.compareTo(placeQty) < 0) {
            if (CommonUtils.isPositive(order.getQuantityFilled())) {
                order.setStatus(OrderStatus.PART_CANCELED.getName());
            } else {
                order.setStatus(OrderStatus.CANCELED.getName());
            }
        } else {
            order.setStatus(OrderStatus.COMPLETED.getName());
        }
    }

    private BigDecimal getSpotFeeRate(String userId) {
        UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(userId);
        return userFeeConfigRate.getSpotFeeRate();
    }


    @Override
    public BigDecimal calcMiddleFee(TradeSpotOrder order, CreateTradeRes resp) {
        // 根据逐笔成交的结果来计算部分手续费
        if (SourceType.isFromUser(order.getSource())) {
            BigDecimal getQty;
            if (Direction.isBuy(order.getDirection())) {
                getQty = resp.getFilled();
            } else {
                getQty = resp.getQuoteFilled();
            }
            BigDecimal feeRate = getSpotFeeRate(order.getUid());
            return getQty.multiply(feeRate);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTradedQuantity(String direct, CreateTradeRes resp) {
        BigDecimal quantity;
        if (Direction.isBuy(direct)) {
            quantity = resp.getQuoteFilled();
        } else {
            quantity = resp.getFilled();
        }
        return quantity;
    }

    @Override
    public BigDecimal calcFreezeFunds(TradeSpotOrder order, BigDecimal quantity, BigDecimal limitPrice) {
        boolean isBuy = Direction.isBuy(order.getDirection());
        SymbolDomain symbolDomain = SymbolDomain.nonNullGet(order.getSymbol());
        boolean isQuote = order.getIsQuote();
        BigDecimal needQty = quantity;
        if (isBuy ^ isQuote) {
            // 需要考虑限价单的准确冻结，和市价单的冗余冻结资金
            boolean isLimit = OrderType.isLimitOrder(order.getType());
            BigDecimal tradePrice = isLimit ? limitPrice : symbolDomain.price(order.getDirection());

            if (isBuy) {
                needQty = quantity.multiply(tradePrice);
            } else {
                needQty = CommonUtils.roundDivide(quantity, tradePrice);
            }
            if (!isLimit) {
                needQty = needQty.multiply(minBuffer);
            }
            log.info("place order limit price/ market price = {}", tradePrice);
        }
        log.info("calcFreezeFunds isBuy={} isQuote={} quantity={} needQty={}", isBuy, isQuote, quantity, needQty);
        return CommonUtils.convertData(needQty);
    }

    @Override
    public void checkPlaceIDKOrder(String uid, String symbol) {
        if (symbol.contains(Constants.IDK_COIN)) {
            List<String> uidArr = properties.getIdk().getUidArr();
            if (!uidArr.contains(uid)) {
                AlarmLogUtil.alarm("non-whitelist uid {} attempt place IDK coin order", uid);
                throw new BusinessException(BusinessExceptionEnum.UNEXPECTED_ERROR);
            }
        }
    }

}
