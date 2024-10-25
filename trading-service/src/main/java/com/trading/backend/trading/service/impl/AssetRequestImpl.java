package com.google.backend.trading.service.impl;

import com.google.common.collect.ImmutableSet;
import com.google.backend.asset.common.model.asset.req.BatchUserPoolReq;
import com.google.backend.asset.common.model.asset.req.PoolReq;
import com.google.backend.asset.common.model.asset.req.UserAvailableNumReq;
import com.google.backend.asset.common.model.base.AvailableNumEntity;
import com.google.backend.asset.common.model.base.BaseReq;
import com.google.backend.asset.common.model.base.BatchPoolEntityForRisk;
import com.google.backend.asset.common.model.base.PoolEntity;
import com.google.backend.asset.common.model.base.PoolEntityForRisk;
import com.google.backend.asset.common.model.trade.req.TradeClosePositionReq;
import com.google.backend.asset.common.model.trade.req.TradeCurrencyConversion;
import com.google.backend.asset.common.model.trade.req.TradeOpenPositionReq;
import com.google.backend.asset.common.model.trade.req.TradeSettlePositionReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotOrderReq;
import com.google.backend.asset.common.model.trade.req.TradeSpotReq;
import com.google.backend.common.dto.constant.DtoConstants;
import com.google.backend.common.mq.HeaderUtils;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetInfoClient;
import com.google.backend.trading.client.feign.AssetOpsClient;
import com.google.backend.trading.client.feign.AssetRiskInfoClient;
import com.google.backend.trading.client.feign.AssetTradeClient;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.asset.Balance;
import com.google.backend.trading.model.spot.api.SpotAvailableReq;
import com.google.backend.trading.model.spot.api.SpotAvailableRes;
import com.google.backend.trading.model.swap.api.CoinBalanceRes;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.TransactionType;
import com.google.backend.trading.service.AssetRequest;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.DefaultedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 封装和资金模块的交互接口
 *
 * @author savion.chen
 * @date 2021/9/30 14:43
 */
@Slf4j
@Service
public class AssetRequestImpl implements AssetRequest {

    public static final PoolEntity EMPTY;

    /**
     * 资金code
     */
    private static final Set<Integer> BALANCE_NOT_ENOUGH_SET = ImmutableSet.of(20100070, 20100040, 20100050);

    static {
        PoolEntity pool = new PoolEntity();
        pool.setBalance(BigDecimal.ZERO);
        pool.setCollateral(BigDecimal.ZERO);
        pool.setDualCurrencyLocked(BigDecimal.ZERO);
        pool.setEarnLocked(BigDecimal.ZERO);
        pool.setExecutionLocked(BigDecimal.ZERO);
        pool.setLoan(BigDecimal.ZERO);
        pool.setSpotLocked(BigDecimal.ZERO);
        pool.setWithdrawLocked(BigDecimal.ZERO);
        pool.setCredit(BigDecimal.ZERO);
        EMPTY = pool;
    }

    @Autowired
    private AssetInfoClient assetInfoClient;
    @Autowired
    private AssetTradeClient assetTradeClient;
    @Autowired
    private AssetRiskInfoClient assetRiskInfoClient;
    @Autowired
    private AssetOpsClient assetOpsClient;

    @Override
    public void doRollback(String reqId) {
        BaseReq req = new BaseReq();
        req.setReqId(reqId);
        com.google.backend.common.web.Response<?> resp = assetOpsClient.doRollback(req);
        if (resp.getCode() != BusinessExceptionEnum.SUCCESS.getCode()) {
            AlarmLogUtil.alarm("asset doRollback failed. reqId: {}, resp: {}", reqId, resp);
        }
    }

    @Override
    public BigDecimal queryAvailableByCoin(String uid, String coin) {
        PoolReq sendReq = new PoolReq();
        sendReq.setUid(uid);
        List<String> coinList = new ArrayList<>();
        coinList.add(coin);
        sendReq.setCoin(coinList);

        com.google.backend.common.web.Response<Map<String, PoolEntity>> resp = assetInfoClient.getPool(sendReq);
        if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
            PoolEntity entity = resp.getData().get(coin);
            if (entity != null) {
                return entity.getBalance();
            }
        }
        return BigDecimal.ZERO;
    }


    @Override
    public SpotAvailableRes queryAvailable(SpotAvailableReq req, String uid) {
        PoolReq sendReq = new PoolReq();
        sendReq.setUid(uid);
        List<String> coinList = new ArrayList<String>();
        String[] coins = req.getSymbol().split(CommonUtils.SEPARATOR);
        String baseCoin = coins[0];
        String quoteCoin = coins[1];
        coinList.add(baseCoin);
        coinList.add(quoteCoin);
        sendReq.setCoin(coinList);

        com.google.backend.common.web.Response<Map<String, PoolEntity>> resp = assetInfoClient.getPool(sendReq);
        if (resp.getCode() == 0) {
            SpotAvailableRes res = new SpotAvailableRes();
            PoolEntity baseEntity = resp.getData().get(baseCoin);
            if (baseEntity != null) {
                res.setBaseAvailable(baseEntity.getBalance());
            } else {
                res.setBaseAvailable(CommonUtils.ZERO_NUM);
            }
            PoolEntity quoteEntity = resp.getData().get(quoteCoin);
            if (quoteEntity != null) {
                res.setQuoteAvailable(quoteEntity.getBalance());
            } else {
                res.setQuoteAvailable(CommonUtils.ZERO_NUM);
            }
            return res;
        }
        return null;
    }


    @Override
    public List<CoinBalanceRes> querySwapCoinBalance(String userId, String specifyCoin, boolean onlyLite) {
        PoolReq req = new PoolReq();
        req.setUid(userId);
        if (null != specifyCoin) {
            CoinDomain coinDomain = CoinDomain.nonNullGet(specifyCoin);
            if (!coinDomain.getSwapConfig().isSupport()) {
                return Collections.emptyList();
            }
            if (onlyLite && !CoinDomain.getLiteCoin().contains(coinDomain)) {
                return Collections.emptyList();
            }
            req.setCoin(Collections.singletonList(specifyCoin));
        } else {
            req.setCoin(Collections.emptyList());
        }

        // 获取用户下所有币对的资金余额
        com.google.backend.common.web.Response<Map<String, PoolEntity>> resp = assetInfoClient.getPool(req);
        if (resp.getCode() != BusinessExceptionEnum.SUCCESS.getCode()) {
            return Collections.emptyList();
        }

        if (null != specifyCoin) {
            CoinBalanceRes item = getCoinBalanceRes(CoinDomain.nonNullGet(specifyCoin), resp.getData().get(specifyCoin));
            return Collections.singletonList(item);
        }

        return CoinDomain.CACHE.values().stream().filter(v -> v.getSwapConfig().isSupport())
                .filter(v -> !onlyLite || CoinDomain.getLiteCoin().contains(v)).map(coinDomain -> {
                    String coin = coinDomain.getName();
                    return getCoinBalanceRes(coinDomain, resp.getData().get(coin));
                }).sorted((o1, o2) -> {
                    int compare = o2.getMarketValue().compareTo(o1.getMarketValue());
                    if (compare == 0) {
                        compare = o1.getPriority() - o2.getPriority();
                    }
                    return compare;
                }).collect(Collectors.toList());
    }

    private CoinBalanceRes getCoinBalanceRes(CoinDomain coinDomain, PoolEntity coinPoolEntity) {
        String coinName = coinDomain.getName();
        BigDecimal balance =
                Optional.ofNullable(coinPoolEntity).map(PoolEntity::getBalance).orElse(BigDecimal.ZERO);
        BigDecimal avgPrice =
                Optional.ofNullable(coinPoolEntity).map(PoolEntity::getAvgPrice).orElse(BigDecimal.ZERO);
        CoinBalanceRes item = new CoinBalanceRes();
        item.setCoin(coinName);
        item.setBalance(balance);
        item.setMarketValue(CommonUtils.usdValue(coinName, balance));
        item.setPriority(coinDomain.getSwapConfig().getPriority());
        if (coinName.equals(Constants.BASE_COIN)) {
            item.setProfit(BigDecimal.ZERO);
        } else {
            BigDecimal midPrice =
                    Optional.ofNullable(SymbolDomain.nullableGet(coinName + Constants.BASE_QUOTE)).map(SymbolDomain::midPrice).orElse(BigDecimal.ZERO);
            item.setProfit(balance.multiply(midPrice.subtract(avgPrice)));
        }
        return item;
    }

    @Override
    public List<CoinBalanceRes> queryCoinBalance(String userId) {
        List<CoinBalanceRes> resList = new ArrayList<>();
        PoolReq req = new PoolReq();
        req.setUid(userId);
        req.setCoin(Collections.emptyList());

        // 获取用户下所有币对的资金余额
        com.google.backend.common.web.Response<Map<String, PoolEntity>> resp = assetInfoClient.getPool(req);
        if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
            for (Map.Entry<String, PoolEntity> entry : resp.getData().entrySet()) {
                BigDecimal balance = entry.getValue().getBalance();
                // 过滤无效的币种
                if (!CommonUtils.isPositive(balance)) {
                    continue;
                }
                BigDecimal value = balance;
                String coin = entry.getKey().toUpperCase();
                if (!coin.equals(Constants.BASE_COIN)) {
                    String symbol = coin + Constants.BASE_QUOTE;
                    try {
                        // 根据当前价计算该币种以USD计价的市值
                        BigDecimal lastPrice = CommonUtils.getMiddlePrice(symbol);
                        value = balance.multiply(lastPrice);
                    } catch (Exception e) {
                        log.error("Can't find symbol={}", symbol);
                    }
                }
                CoinBalanceRes item = new CoinBalanceRes();
                item.setCoin(coin);
                item.setBalance(balance);
                item.setMarketValue(value);
                resList.add(item);
            }
        } else {
            return null;
        }

        if (resList.size() > 1) {
            // 根据币种的当前市值排序输出
            resList.sort((o1, o2) -> {
                if (o2.getMarketValue().compareTo(o1.getMarketValue()) > 0) {
                    return 1;
                } else if (o2.getMarketValue().compareTo(o1.getMarketValue()) < 0) {
                    return -1;
                }
                return 0;
            });
        }
        return resList;
    }

    @Override
    public AssetStatus doOpenPosition(TradeTransaction transaction) {
        TransactionType type = TransactionType.valueOf(transaction.getType());
        assert TransactionType.isOpenPos(type) : "type must be ADD_POSITION or OPEN_POSITION";
        String[] coins = transaction.getSymbol().split(CommonUtils.SEPARATOR);
        String baseCoin = coins[0];
        String quoteCoin = coins[1];
        TradeOpenPositionReq req = new TradeOpenPositionReq();
        req.setReqId(transaction.getUuid());
        TradeOpenPositionReq.Params params = new TradeOpenPositionReq.Params();
        params.setUid(transaction.getUid());
        params.setDirection(transaction.getDirection().toLowerCase());
        params.setBaseCoin(baseCoin);
        params.setQuoteCoin(quoteCoin);
        params.setBaseAmount(transaction.getBaseQuantity());
        params.setQuoteAmount(transaction.getQuoteQuantity());
        params.setFee(transaction.getFee());
        params.setFeeCoin(Constants.BASE_COIN);
        req.setParams(params);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(DtoConstants.AGGREGATION_GLOBAL_ID, transaction.getUuid());
        headers.add(DtoConstants.AGGREGATION_TXH_ID, HeaderUtils.toTxnId(Constants.SERVICE_NAME, transaction.getId()));
        headers.add(DtoConstants.AGGREGATION_SERVICE, Constants.SERVICE_NAME);

        com.google.backend.common.web.Response<Void> resp = assetTradeClient.doOpenPosition(req, headers);
        if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
            return AssetStatus.COMPLETED;
        }
        AlarmLogUtil.alarm("asset doOpenPosition failed. transaction: {}, resp: {}", transaction, resp);
        if (resp.getCode() == BusinessExceptionEnum.ASSET_FALLBACK.getCode()) {
            return AssetStatus.PENDING;
        }
        return AssetStatus.EXCEPTION;
    }

    @Override
    public AssetStatus doClosePosition(TradeTransaction transaction) {
        TransactionType type = TransactionType.valueOf(transaction.getType());
        assert TransactionType.isClosePos(type) : "type must be REDUCE_POSITION or CLOSE_POSITION";
        String[] coins = transaction.getSymbol().split(CommonUtils.SEPARATOR);
        String baseCoin = coins[0];
        String quoteCoin = coins[1];
        TradeClosePositionReq req = new TradeClosePositionReq();
        req.setReqId(transaction.getUuid());
        TradeClosePositionReq.Params params = new TradeClosePositionReq.Params();
        params.setUid(transaction.getUid());
        params.setDirection(transaction.getDirection().toLowerCase());
        params.setBaseCoin(baseCoin);
        params.setQuoteCoin(quoteCoin);
        params.setBaseAmount(transaction.getBaseQuantity());
        params.setQuoteAmount(transaction.getQuoteQuantity());
        params.setPnl(transaction.getPnl());
        params.setPnlConversion(transaction.getPnlConversion());
        params.setFee(transaction.getFee());
        params.setFeeCoin(Constants.BASE_COIN);
        req.setParams(params);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(DtoConstants.AGGREGATION_GLOBAL_ID, transaction.getUuid());
        headers.add(DtoConstants.AGGREGATION_TXH_ID, HeaderUtils.toTxnId(Constants.SERVICE_NAME, transaction.getId()));
        headers.add(DtoConstants.AGGREGATION_SERVICE, Constants.SERVICE_NAME);

        com.google.backend.common.web.Response<Void> resp = assetTradeClient.doClosePosition(req, headers);
        if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
            log.info("compensate asset success, transaction = {}", transaction);
            return AssetStatus.COMPLETED;
        }
        AlarmLogUtil.alarm("asset doClosePosition failed. transaction: {}, resp: {}", transaction, resp);
        if (resp.getCode() == BusinessExceptionEnum.ASSET_FALLBACK.getCode()) {
            return AssetStatus.PENDING;
        }
        return AssetStatus.EXCEPTION;
    }

    @Override
    public void doSettlePosition(TradeTransaction transaction) {
        TransactionType type = TransactionType.valueOf(transaction.getType());
        assert type == TransactionType.SETTLE_POSITION : "type must be SETTLE_POSITION";
        String[] coins = transaction.getSymbol().split(CommonUtils.SEPARATOR);
        String baseCoin = coins[0];
        String quoteCoin = coins[1];
        TradeSettlePositionReq req = new TradeSettlePositionReq();
        req.setReqId(transaction.getUuid());
        TradeSettlePositionReq.Params params = new TradeSettlePositionReq.Params();
        params.setUid(transaction.getUid());
        params.setDirection(transaction.getDirection().toLowerCase());
        params.setBaseCoin(baseCoin);
        params.setQuoteCoin(quoteCoin);
        params.setBaseAmount(transaction.getBaseQuantity());
        params.setQuoteAmount(transaction.getQuoteQuantity());
        params.setFee(transaction.getFee());
        params.setFeeCoin(Objects.equals(transaction.getDirection(), Direction.BUY.getName()) ? baseCoin : quoteCoin);

        if (Direction.isBuy(transaction.getDirection())) {
            BigDecimal avgPrice = transaction.getPrice();
            if (!Constants.BASE_COIN.equals(quoteCoin)) {
                avgPrice = CommonUtils.getMiddlePrice(baseCoin + Constants.BASE_QUOTE).setScale(Constants.USD_PRICE_PRECISION,
                        RoundingMode.HALF_UP);
            }
            params.setBaseAvgPrice(avgPrice);
        } else {
            BigDecimal avgPrice = BigDecimal.ONE;
            if (!Constants.BASE_COIN.equals(quoteCoin)) {
                avgPrice = CommonUtils.getMiddlePrice(quoteCoin + Constants.BASE_QUOTE).setScale(Constants.USD_PRICE_PRECISION,
                        RoundingMode.HALF_UP);
            }
            params.setQuoteAvgPrice(avgPrice);
        }

        req.setParams(params);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(DtoConstants.AGGREGATION_GLOBAL_ID, transaction.getUuid());
        headers.add(DtoConstants.AGGREGATION_TXH_ID, HeaderUtils.toTxnId(Constants.SERVICE_NAME, transaction.getId()));
        headers.add(DtoConstants.AGGREGATION_SERVICE, Constants.SERVICE_NAME);

        com.google.backend.common.web.Response<Void> resp = assetTradeClient.doSettlePosition(req, headers);
        // TODO: 如果是超时，才需要rollback
        if (resp.getCode() == BusinessExceptionEnum.ASSET_FALLBACK.getCode()) {
            this.doRollback(transaction.getUuid());
        }
        if (resp.getCode() != BusinessExceptionEnum.SUCCESS.getCode()) {
            throw new BusinessException(BusinessExceptionEnum.INSUFFICIENT_FUNDS);
        }
        ;
    }

    @Override
    public TradeSpotOrderReq getSpotFreezeReq(TradeSpotOrder order, BigDecimal lockQty, String reqId) {
        TradeSpotOrderReq sendReq = new TradeSpotOrderReq();
        sendReq.setReqId(reqId);

        // 根据计算的冻结资金量， 构造去资金模块的冻结请求
        TradeSpotOrderReq.Params param = new TradeSpotOrderReq.Params();
        param.setUid(order.getUid());
        String[] coins = order.getSymbol().split(CommonUtils.SEPARATOR);
        if (Direction.isBuy(order.getDirection())) {
            param.setCoin(coins[1]);
            param.setCompetitorCoin(coins[0]);
        } else {
            param.setCoin(coins[0]);
            param.setCompetitorCoin(coins[1]);
        }
        param.setAmount(lockQty);
        sendReq.setParams(param);
        return sendReq;
    }

    @Override
    public BusinessExceptionEnum freezeFunds(TradeSpotOrderReq req) {
        // 请求资金模块进行资金的冻结操作，如果资金不足则返回该信息到前端
        com.google.backend.common.web.Response<Void> resp = assetTradeClient.doSpotOrder(req);
        if (BALANCE_NOT_ENOUGH_SET.contains(resp.getCode())) {
            return BusinessExceptionEnum.INSUFFICIENT_FUNDS;
        }
        return resp.getCode() == 0 ? BusinessExceptionEnum.SUCCESS : BusinessExceptionEnum.UNEXPECTED_ERROR;
    }

    @Override
    public int cancelFreeze(TradeSpotOrderReq req) {
        com.google.backend.common.web.Response<Void> resp = assetTradeClient.doCancelSpotOrder(req);
        return resp.getCode();
    }

    @Override
    public int conversionCoin(TradeCurrencyConversion req) {
        // 执行币种转换的处理，和isConversionCoin的类型配合使用
        com.google.backend.common.web.Response<Void> resp = assetTradeClient.doCurrencyConversion(req);
        return resp.getCode();
    }

    @Override
    public TradeSpotReq getUpdateTradeReq(TradeSpotOrder order, TradeTransaction trans) {
        TradeSpotReq tradeReq = new TradeSpotReq();
        tradeReq.setReqId(trans.getUuid());

        Pair<String, String> coinPair = CommonUtils.coinPair(order.getSymbol());
        String base = coinPair.getFirst();
        String quote = coinPair.getSecond();

        TradeSpotReq.Params params = new TradeSpotReq.Params();
        params.setUid(order.getUid());
        params.setTradeType("spot");
        params.setDirection(order.getDirection().toLowerCase());
        params.setBaseCoin(base);
        params.setQuoteCoin(quote);
        params.setBaseAmount(trans.getBaseQuantity());
        params.setQuoteAmount(trans.getQuoteQuantity());

        //设置成交均价，用于计算持币均价，在新增的币种上覆盖成交均价即可
        if (Direction.isBuy(order.getDirection())) {
            BigDecimal avgPrice = trans.getPrice();
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

        // 在订单结束的时候，需要将多冻结的一点buff进行释放
        if (OrderStatus.isFinish(order.getStatus())) {
            BigDecimal oldLock = order.getLockAmount();
            BigDecimal tradeQty;
            if (Direction.isBuy(trans.getDirection())) {
                tradeQty = trans.getQuoteQuantity();
            } else {
                tradeQty = trans.getBaseQuantity();
            }
            BigDecimal remainQty = oldLock.subtract(tradeQty);
            if (CommonUtils.isPositive(remainQty)) {
                params.setUnlockAmount(remainQty);
            } else {
                params.setUnlockAmount(CommonUtils.ZERO_NUM);
            }
        } else {
            params.setUnlockAmount(CommonUtils.ZERO_NUM);
        }

        params.setFee(trans.getFee());
        params.setFeeCoin(order.getFeeCoin());
        tradeReq.setParams(params);
        return tradeReq;
    }

    @Override
    public int updateTradedResult(TradeSpotReq req) {
        com.google.backend.common.web.Response<Void> resp = assetTradeClient.doSpot(req);
        if (resp.getCode() != BusinessExceptionEnum.SUCCESS.getCode()) {
            AlarmLogUtil.alarm("asset doSpot failed. req: {}, resp: {}", req, resp);
        }
        return resp.getCode();
    }

    @Override
    public Map<String, PoolEntity> getBalanceMapByUid(String uid) {
        PoolReq poolReq = new PoolReq();
        poolReq.setUid(uid);
        Response<Map<String, PoolEntity>> pool = assetInfoClient.getPool(poolReq);
        if (BusinessExceptionEnum.SUCCESS.getCode() != pool.getCode()) {
            throw new RuntimeException("query asset get poll error!");
        }
        return pool.getData();
    }

    @Override
    public List<Balance> getBalanceByUid(String uid) {
        List<Balance> balances = new ArrayList<>();
        Map<String, PoolEntity> map = getBalanceMapByUid(uid);
        if(null != map){
            map.forEach((key, value) -> {
                    Balance b = new Balance(key.toUpperCase(), value.getBalance(),value.getSpotLocked());
                    balances.add(b);
                }
            );
        }
        return balances;
    }

    @Override
    public List<Balance> negativeBalanceExcludingUSD(String uid) {
        List<Balance> balances = getBalanceByUid(uid);
        if (ListUtil.isEmpty(balances)){
            return new ArrayList<>();
        }
        return balances.stream().filter(balance -> BigDecimal.ZERO.compareTo(balance.getBalance()) > 0
                && !Constants.BASE_COIN.equals(balance.getCoin())).sorted().collect(Collectors.toList());
    }

    @Override
    public List<Balance> positiveBalanceExcludingUSD(String uid) {
        List<Balance> list = getBalanceByUid(uid);
        if (ListUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        return list.stream().filter(balance -> BigDecimal.ZERO.compareTo(balance.getBalance()) < 0
                && !Constants.BASE_COIN.equals(balance.getCoin())).sorted().collect(Collectors.toList());
    }

    @Override
    public List<Balance> getLockedAssert(String uid) {
        List<Balance> list = getBalanceByUid(uid);
        if (ListUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        return list.stream().filter(balance ->
                BigDecimal.ZERO.compareTo(balance.getSpotLocked()) < 0).sorted().collect(Collectors.toList());
    }

    @Override
    public Map<String, PoolEntityForRisk> assetPoolForRisk(String uid) {
        BatchUserPoolReq req = new BatchUserPoolReq();
        req.setUids(Collections.singletonList(uid));
        req.setCoins(null);
        Response<List<BatchPoolEntityForRisk>> res = assetRiskInfoClient.getPoolBatch(req);
        if (BusinessExceptionEnum.SUCCESS.getCode() == res.getCode()) {
            List<BatchPoolEntityForRisk> data = res.getData();
            if (data.size() == 1) {
                BatchPoolEntityForRisk poolEntityForRisk = data.get(0);
                return poolEntityForRisk.getPools();
            }
        }
        AlarmLogUtil.alarm("risk asset pool err, margin set up to 0, res = {}", res);
        return Collections.emptyMap();
    }

    @Override
    public BigDecimal assetSpotAvailable(String uid, String availableCoin, String comCoin) {
        UserAvailableNumReq req = new UserAvailableNumReq();
        req.setUid(uid);
        req.setCoin(availableCoin);
        req.setComCoin(comCoin);
        req.setType("spot");
        Response<AvailableNumEntity> res = assetInfoClient.getUserAvailableNum(req);
        if (BusinessExceptionEnum.SUCCESS.getCode() == res.getCode()) {
            return res.getData().getAvailableNum();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Map<String, PoolEntity> assetPool(String uid) {
        PoolReq req = new PoolReq();
        req.setUid(uid);
        req.setCoin(null);
        Response<Map<String, PoolEntity>> res = assetInfoClient.getPool(req);
        Map<String, PoolEntity> data = new HashMap<>();
        if (BusinessExceptionEnum.SUCCESS.getCode() == res.getCode()) {
            data = res.getData();
        }
        return DefaultedMap.defaultedMap(data, EMPTY);
    }

}
