package com.google.backend.trading.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableMap;
import com.google.backend.asset.common.model.trade.req.TradeFundingCostReq;
import com.google.backend.trading.alarm.AlarmComponent;
import com.google.backend.trading.alarm.AlarmEnum;
import com.google.backend.trading.client.feign.AssetTradeClient;
import com.google.backend.trading.client.feign.CommonConfigClient;
import com.google.backend.trading.client.feign.KlineInfoClient;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.config.web.I18nConvertSerializer;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.constant.RedisKeyConstants;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.mapper.TradeTransactionAmountMapper;
import com.google.backend.trading.dao.model.TradeFeeDefaultConfig;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionAmount;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.migrate.AppMigrateHandle;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.kline.dto.PriceChange;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.swap.api.AipSwapOrderRes;
import com.google.backend.trading.model.swap.dto.AipSwapOrderPlaceReqDTO;
import com.google.backend.trading.model.trade.AlgoTradeAmountDTO;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.fee.RedisTradeDTO;
import com.google.backend.trading.model.trade.fee.TradeFeeConfigData;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.model.trade.fee.VIPLevelEnum;
import com.google.backend.trading.service.SensorsTraceService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.SwapService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.service.impl.AssetRequestImpl;
import com.google.backend.trading.task.AipSwapOrderExecuteLoopThread;
import com.google.backend.trading.task.AssetExceptionOrderCheckLoopThread;
import com.google.backend.trading.task.InitUserTradeAmount2DBTask;
import com.google.backend.trading.task.RedisUserAmountTask;
import com.google.backend.trading.task.fix.BugFixTask;
import com.google.backend.trading.util.CommonUtils;
import com.google.backend.trading.util.ThreadLocalUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * @author trading
 * @date 2021/10/9 16:59
 */
@Slf4j
@Api(value = "测试接口", tags = "测试接口")
@RestController
@Validated
@Profile({"!prod"})
@RequestMapping("/test")
public class TestController {


    @Autowired
    private StringRedisTemplate pdtRedisTemplate;

    @Autowired
    private AlarmComponent alarmComponent;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired(required = false)
    private AppMigrateHandle appMigrateHandle;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CommonConfigClient configClient;

    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;
    @Autowired
    private TradeAssetService tradeAssetService;
    @Resource
    private DefaultTradeTransactionMapper defaultTradeTransactionMapper;
    @Autowired
    private KlineInfoClient klineInfoClient;
    @Autowired
    private SensorsTraceService sensorsTraceService;
    @Resource
    private TradeTransactionAmountMapper tradeTransactionAmountMapper;
    @Autowired
    private InitUserTradeAmount2DBTask initUserTradeAmount2DBTask;
    @Autowired
    private RedisUserAmountTask redisUserAmountTask;
    @Autowired
    private SwapService swapService;
    @Autowired
    private AssetExceptionOrderCheckLoopThread assetExceptionOrderCheckLoopThread;

    @Autowired
    private AssetTradeClient assetTradeClient;

    @Autowired
    private BugFixTask bugFixTask;

    @GetMapping("/fix/fundingcost")
    @ApiOperation(value = "测试收取fundingcost task", notes = "测试收取fundingcost")
    public Response fundingcost() {
        bugFixTask.fixFundingCostTask();
        return Response.ok();
    }

    @GetMapping("/asset/fundingcost")
    @ApiOperation(value = "测试资产fundingcost接口", notes = "fundingcost接口")
    public Response assetFundingcost() {
        TradeFundingCostReq req = new TradeFundingCostReq();
        String reqId = UUID.randomUUID().toString();
        String uid = "61933c8d4a726bab3b7fa55b";
        BigDecimal totalFundingCost = new BigDecimal("0.01");
        req.setReqId(reqId);
        TradeFundingCostReq.Params params = new TradeFundingCostReq.Params();
        params.setUid(uid);
        params.setCoinAndAmount(ImmutableMap.of(Constants.BASE_COIN, totalFundingCost));
        req.setParams(params);
        assetTradeClient.doFundingCost(req, new LinkedMultiValueMap<>());
        return Response.ok();
    }

    @GetMapping("/asset-exception-loop")
    @ApiOperation(value = "资金timeout循环任务", notes = "资金timeout循环任务")
    public Response assetExceptionLoop() throws Throwable {
        assetExceptionOrderCheckLoopThread.handle();
        return Response.ok();
    }

    @Autowired
    private AipSwapOrderExecuteLoopThread aipSwapOrderExecuteLoopThread;
    @Autowired
    private AssetRequestImpl assetRequest;


    @GetMapping("/asset/rollback")
    @ApiOperation(value = "测试资金回滚接口", notes = "aip循环定时任务")
    public Response doRollback() {

        assetRequest.doRollback("");
        return Response.ok();
    }

    @GetMapping("/aip-loop")
    @ApiOperation(value = "aip循环定时任务", notes = "aip循环定时任务")
    public Response aipLoop() throws Throwable {
//        aipSwapOrderExecuteLoopThread.handle();
        return Response.ok();
    }


    @GetMapping("/json/algo-trade-amount")
    @ApiOperation(value = "JSON算法交易量", notes = "JSON算法交易量")
    public Response jsonToAlgoTradeAmountDTO() throws JsonProcessingException {
        String msg = "{\"trade_id\":\"f9EU3lJI5YHYaAQ6-4afa\",\"uid\":\"60ffae30bc6422f961e3040e\",\"coin\":\"BTC\",\"amount\":\"19.99567\"}";
        AlgoTradeAmountDTO res = objectMapper.readValue(msg, AlgoTradeAmountDTO.class);
        return Response.ok(res);
    }

    @GetMapping("/init/transaction-amount")
    @ApiOperation(value = "初始化用户20W条交易数据")
    public Response initTransactionAmount(@RequestParam(name = "uid", required = true) String uid) {
        long l = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        for (int i = 0; i < 100; i++) {
            List<TradeTransactionAmount> tradeTransactionAmountList = IntStream.range(0, 2_000).mapToObj(e -> {
                TradeTransactionAmount tradeTransactionAmount = new TradeTransactionAmount();
                tradeTransactionAmount.setUid(uid);
                BigDecimal bigDecimal = RandomUtil.randomBigDecimal(BigDecimal.ONE, BigDecimal.TEN);
                tradeTransactionAmount.setAmount(bigDecimal);
                tradeTransactionAmount.setTransId("");
                tradeTransactionAmount.setCtime(new java.util.Date(l));
                return tradeTransactionAmount;
            }).collect(Collectors.toList());
            tradeTransactionAmountMapper.batchInsert(tradeTransactionAmountList);
        }
        return Response.ok();
    }

    @PostMapping("aip/swap/place")
    @ApiOperation(value = "AIP下单")
    public Response<AipSwapOrderRes> aipSwapOrderPlace(@RequestBody AipSwapOrderPlaceReqDTO aipSwapOrderPlaceReqDTO) {
        swapService.saveForAip(aipSwapOrderPlaceReqDTO);
        TradeSwapOrder tradeSwapOrder = swapService.queryByOrderId(aipSwapOrderPlaceReqDTO.getAipSwapId());
        return Response.ok(swapService.aipPerformOrder(tradeSwapOrder));
    }

    @GetMapping("/set/redis-user-amount")
    @ApiOperation(value = "用户设置30天交易量")
    public Response setRedisUserAmount(@RequestParam(name = "uid", required = true) String uid) {
        redisUserAmountTask.setRedisUserAmount();
        return Response.ok();
    }

    @GetMapping("/init/usertradeamount-2dbtask")
    @ApiOperation(value = "初始化前30用户交易数据到DB中")
    public Response initUserTradeAmount2DBTask(@RequestParam(name = "uid", required = true) String uid) {
        initUserTradeAmount2DBTask.initUserTradeAmount2db();
        return Response.ok();
    }

    @GetMapping("alarmprice/sensors-test")
    @ApiOperation(value = "测试神策alarmprice")
    public Response sensorsAlarmPriceTest() {

        TradeUserAlarmPrice tradeUserAlarmPrice = new TradeUserAlarmPrice();
        java.util.Date date = new java.util.Date();
        tradeUserAlarmPrice.setMtime(date);
        tradeUserAlarmPrice.setCtime(date);
        tradeUserAlarmPrice.setAlarmPrice(BigDecimal.ONE);
        tradeUserAlarmPrice.setAlarmCompare(">");
        tradeUserAlarmPrice.setUid("test1");
        tradeUserAlarmPrice.setSymbol("BTC_USD");

        sensorsTraceService.setAlert(tradeUserAlarmPrice, BigDecimal.TEN);

        return Response.ok();
    }

    @GetMapping("threadlocal")
    @ApiOperation(value = "测试threadlocal header缓存")
    public Response threadlocalTest() {
        String originChannel = ThreadLocalUtils.ORIGIN_CHANNEL.get();
        String lorp = ThreadLocalUtils.L_OR_P.get();
        log.info("HEADER ORIGIN_CHANNEL :{}", originChannel);
        log.info("HEADER L_OR_P :{}", lorp);
        return Response.ok();
    }

    @GetMapping("page/select")
    @ApiOperation(value = "测试分页")
    public Response pageTest() {

        int page = 1;
        int pageSize = 20;

        PageHelper.startPage(page, pageSize, false);
        TradeTransactionExample example = new TradeTransactionExample();
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        criteria.andAssetStatusEqualTo(AssetStatus.COMPLETED.name());
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(31);
        criteria.andCtimeBetween(Date.valueOf(start.toLocalDate()), Date.valueOf(end.toLocalDate()));

        while (true) {
            List<TradeTransaction> tradeTransactionList = defaultTradeTransactionMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(tradeTransactionList)) {
                break;
            }
            log.info("查询分页 page : {} list : {} , size : {}", page, tradeTransactionList, tradeTransactionList.size());
            page++;
            PageHelper.startPage(page, pageSize, false);
        }
        return Response.ok();
    }

    @GetMapping("kline/before30d")
    @ApiOperation(value = "过去30天Kline")
    public Response<List<PriceChange>> klineTest() {
        String coin = "BTC";
        String dayListStr = StringUtils.arrayToDelimitedString(IntStream.range(0, 31).boxed().toArray(), ",");
        Response<List<PriceChange>> priceChangeRes = klineInfoClient.priceChange(coin + Constants.BASE_QUOTE, dayListStr);
        System.out.println(priceChangeRes);
        return priceChangeRes;
    }

    @GetMapping("user-trade2redis/init")
    @ApiOperation(value = "初始化30天成交量")
    public Response initUserTrade2Redis() {
        log.info("初始化user-trade amount 2 redis ");
        TradeTransactionExample example = new TradeTransactionExample();
        TradeTransactionExample.Criteria criteria = example.createCriteria();

        criteria.andAssetStatusEqualTo(AssetStatus.COMPLETED.name());
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(31);
        criteria.andCtimeBetween(Date.valueOf(start.toLocalDate()), Date.valueOf(end.toLocalDate()));
        List<TradeTransaction> tradeTransactionList = defaultTradeTransactionMapper.selectByExample(example);
        List<RedisTradeDTO> redisTradeDTOList = new ArrayList<>(tradeTransactionList.size());
        log.info("查询 start: {} end: {} tradeTransactionList：{}", start, end, tradeTransactionList);

        List<TradeTransaction> usdCoinIsQuoteList = tradeTransactionList.stream().filter(
                        e -> CommonUtils.getQuoteCoin(e.getSymbol()).equals(Constants.BASE_COIN) ||
                                "USDT".equals(CommonUtils.getQuoteCoin(e.getSymbol())))
                .collect(Collectors.toList());
        log.info("查询USD or USDT 为Quote start: {} end: {} 【usdCoinIsQuoteList】：{}", start, end, usdCoinIsQuoteList);
        Map<String, List<TradeTransaction>> usdCoinIsQuoteGroupTran = usdCoinIsQuoteList.stream().collect(Collectors.groupingBy(TradeTransaction::getSymbol));
        for (String symbol : usdCoinIsQuoteGroupTran.keySet()) {
            List<TradeTransaction> tradeTransactions = usdCoinIsQuoteGroupTran.get(symbol);
            for (TradeTransaction tradeTransaction : tradeTransactions) {
                java.util.Date ctime = tradeTransaction.getCtime();
                String uid = tradeTransaction.getUid();
                BigDecimal quoteQuantity = tradeTransaction.getQuoteQuantity();
                redisTradeDTOList.add(new RedisTradeDTO(quoteQuantity, ctime.getTime(), uid, tradeTransaction.getUuid()));
            }
        }

        String dayListStr = StringUtils.arrayToDelimitedString(IntStream.range(0, 31).boxed().toArray(), ",");
        List<TradeTransaction> usdCoinNOTQuoteList = tradeTransactionList.stream().filter(e -> !CommonUtils.getQuoteCoin(e.getSymbol()).equals(Constants.BASE_COIN)).collect(Collectors.toList());
        log.info("查询usd不是Quote的 start: {} end: {} 【usdCoinNOTQuoteList】：{}", start, end, usdCoinNOTQuoteList);

        Map<String, List<TradeTransaction>> baseCoinGroupList = usdCoinNOTQuoteList.stream().collect(Collectors.groupingBy(e -> CommonUtils.getBaseCoin(e.getSymbol())));
        for (String coin : baseCoinGroupList.keySet()) {
            if (coin.equals(Constants.BASE_COIN)) {
                List<TradeTransaction> tradeTransactions = baseCoinGroupList.get(coin);
                tradeTransactions.stream().forEach(tradeTransaction -> {
                    BigDecimal amount = tradeTransaction.getBaseQuantity();
                    String uid = tradeTransaction.getUid();
                    java.util.Date ctime = tradeTransaction.getCtime();
                    redisTradeDTOList.add(new RedisTradeDTO(amount, ctime.getTime(), uid, tradeTransaction.getUuid()));
                });
            } else {
                Response<List<PriceChange>> priceChangeRes = klineInfoClient.priceChange(coin + Constants.BASE_QUOTE, dayListStr);
                List<PriceChange> priceChangeList = priceChangeRes.getData();
                if (priceChangeList != null && !priceChangeList.isEmpty()) {
                    List<TradeTransaction> tradeTransactions = baseCoinGroupList.get(coin);
                    for (TradeTransaction tradeTransaction : tradeTransactions) {
                        String uid = tradeTransaction.getUid();
                        java.util.Date ctime = tradeTransaction.getCtime();
                        long day = DateUtil.betweenDay(ctime, CommonUtils.getNowTime(), false);
                        PriceChange priceChange = null;
                        try {
                            priceChange = priceChangeList.stream().filter(e -> (int) day == e.getDays()).findFirst().orElse(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (priceChange != null) {
                            BigDecimal price = priceChange.getPrice();
                            BigDecimal baseQuantity = tradeTransaction.getBaseQuantity();
                            BigDecimal amount = price.subtract(baseQuantity);
                            redisTradeDTOList.add(new RedisTradeDTO(amount, ctime.getTime(), uid, tradeTransaction.getUuid()));
                        }
                    }
                }
            }
        }
        Map<String, List<RedisTradeDTO>> listMapGroupByUid = redisTradeDTOList.stream().collect(Collectors.groupingBy(RedisTradeDTO::getUid));
        for (String uid : listMapGroupByUid.keySet()) {
            List<RedisTradeDTO> redisTradeDTOS = listMapGroupByUid.get(uid);
            List<TradeTransactionAmount> tradeTransactionAmounts = redisTradeDTOS.stream().map(e -> {
                TradeTransactionAmount tradeTransactionAmount = new TradeTransactionAmount();
                tradeTransactionAmount.setAmount(e.getAmount());
                tradeTransactionAmount.setUid(e.getUid());
                tradeTransactionAmount.setTransId(e.getTransId());
                tradeTransactionAmount.setCtime(CommonUtils.getNowTime(e.getTime()));
                return tradeTransactionAmount;
            }).collect(Collectors.toList());
            if (!tradeTransactionAmounts.isEmpty()) {
                tradeTransactionAmountMapper.batchInsert(tradeTransactionAmounts);
                log.info("tradeTransactionAmountMapper batchInsert list size :{}", tradeTransactionAmounts.size());
            }
        }
        return Response.ok();
    }


    @GetMapping("user-fee-config/")
    @ApiOperation(value = "用户手续费测试")
    public Response<UserFeeConfigRate> testUserFeeConfig(String uid) {
        UserFeeConfigRate userFeeConfigRate = tradeFeeConfigService.selectUserFeeConfig(uid);

        TradeFeeDefaultConfig defaultConfig = new TradeFeeDefaultConfig();
        defaultConfig.setTag(VIPLevelEnum.DEFAULT.getTag());
        defaultConfig.setAlgorithmicFeeRate(new BigDecimal("0.001"));
        defaultConfig.setSpotFeeRate(new BigDecimal("0.001"));
        defaultConfig.setSwapFeeRate(new BigDecimal("0.001"));
        defaultConfig.setMarginFeeRate(new BigDecimal("0.001"));
        defaultConfig.setMarginSettleFeeRate(new BigDecimal("0.001"));
        defaultConfig.setFundingCostEnable(true);
        tradeFeeConfigService.insertOrUpdateDefault(defaultConfig);

        UserFeeConfigRate userFeeConfigRate1 = tradeFeeConfigService.selectUserFeeConfig(null);
        BigDecimal spotFeeRate = userFeeConfigRate1.getSpotFeeRate();
        Assert.isTrue(
                defaultConfig.getSpotFeeRate().equals(userFeeConfigRate1.getSpotFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getSwapFeeRate().equals(userFeeConfigRate1.getSwapFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getMarginFeeRate().equals(userFeeConfigRate1.getMarginFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getMarginSettleFeeRate().equals(userFeeConfigRate1.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getFundingCostEnable().equals(userFeeConfigRate1.getFundingCostEnable()));

        TradeFeeConfigData tradeFeeConfigData = new TradeFeeConfigData();
        tradeFeeConfigData.setUid("test");
        tradeFeeConfigData.setAlgorithmicFeeRate(new BigDecimal("0.001"));
        tradeFeeConfigData.setSpotFeeRate(new BigDecimal("0.001"));
        tradeFeeConfigData.setSwapFeeRate(new BigDecimal("0.001"));
        tradeFeeConfigData.setMarginFeeRate(new BigDecimal("0.001"));
        tradeFeeConfigData.setMarginSettleFeeRate(new BigDecimal("0.001"));
        tradeFeeConfigService.insertOrUpdateUser(tradeFeeConfigData);

        UserFeeConfigRate userFeeConfigRate2 = tradeFeeConfigService.selectUserFeeConfig("test");
        Assert.isTrue(
                tradeFeeConfigData.getSpotFeeRate().equals(userFeeConfigRate2.getSpotFeeRate().stripTrailingZeros()) &&
                        tradeFeeConfigData.getSwapFeeRate().equals(userFeeConfigRate2.getSwapFeeRate().stripTrailingZeros()) &&
                        tradeFeeConfigData.getMarginFeeRate().equals(userFeeConfigRate2.getMarginFeeRate().stripTrailingZeros()) &&
                        tradeFeeConfigData.getMarginSettleFeeRate().equals(userFeeConfigRate2.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        userFeeConfigRate2.getFundingCostEnable() == true);

        TradeFeeConfigData updateUserFee = new TradeFeeConfigData();
        updateUserFee.setUid("test");
        updateUserFee.setAlgorithmicFeeRate(new BigDecimal("0.0002"));
        updateUserFee.setSpotFeeRate(new BigDecimal("0.0002"));
        updateUserFee.setSwapFeeRate(new BigDecimal("0.0002"));
        updateUserFee.setMarginFeeRate(new BigDecimal("0.0002"));
        updateUserFee.setMarginSettleFeeRate(new BigDecimal("0.0002"));
        tradeFeeConfigService.insertOrUpdateUser(updateUserFee);


        UserFeeConfigRate userFeeConfigRate3 = tradeFeeConfigService.selectUserFeeConfig("test");
        Assert.isTrue(
                updateUserFee.getSpotFeeRate().equals(userFeeConfigRate3.getSpotFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getSwapFeeRate().equals(userFeeConfigRate3.getSwapFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getMarginFeeRate().equals(userFeeConfigRate3.getMarginFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getMarginSettleFeeRate().equals(userFeeConfigRate3.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        userFeeConfigRate3.getFundingCostEnable() == true);

        TradeFeeConfigData tradeFeeConfigData2 = new TradeFeeConfigData();
        tradeFeeConfigData2.setUid("test2");
        tradeFeeConfigData2.setAlgorithmicFeeRate(new BigDecimal("0.0002"));
        tradeFeeConfigData2.setSpotFeeRate(new BigDecimal("0.0002"));
        tradeFeeConfigData2.setSwapFeeRate(new BigDecimal("0.0002"));
        tradeFeeConfigData2.setMarginFeeRate(new BigDecimal("0.0002"));
        tradeFeeConfigData2.setMarginSettleFeeRate(new BigDecimal("0.0002"));
        tradeFeeConfigData2.setFundingCostEnable(false);
        tradeFeeConfigService.insertOrUpdateUser(tradeFeeConfigData2);

        UserFeeConfigRate userFeeConfigRate4 = tradeFeeConfigService.selectUserFeeConfig("test2");
        Assert.isTrue(
                tradeFeeConfigData2.getSpotFeeRate().equals(userFeeConfigRate4.getSpotFeeRate().stripTrailingZeros()) &&
                        tradeFeeConfigData2.getSwapFeeRate().equals(userFeeConfigRate4.getSwapFeeRate().stripTrailingZeros()) &&
                        tradeFeeConfigData2.getMarginFeeRate().equals(userFeeConfigRate4.getMarginFeeRate().stripTrailingZeros()) &&
                        tradeFeeConfigData2.getMarginSettleFeeRate().equals(userFeeConfigRate4.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        tradeFeeConfigData2.getFundingCostEnable().equals(userFeeConfigRate4.getFundingCostEnable()));

        TradeFeeConfigData updateUserFee2 = new TradeFeeConfigData();
        updateUserFee2.setUid("test2");
        updateUserFee2.setAlgorithmicFeeRate(new BigDecimal("0.0001"));
        updateUserFee2.setSpotFeeRate(new BigDecimal("0.0001"));
        updateUserFee2.setSwapFeeRate(new BigDecimal("0.0001"));
        updateUserFee2.setMarginFeeRate(new BigDecimal("0.0001"));
        updateUserFee2.setMarginSettleFeeRate(new BigDecimal("0.0001"));
        updateUserFee2.setFundingCostEnable(true);
        tradeFeeConfigService.insertOrUpdateUser(updateUserFee2);

        UserFeeConfigRate userFeeConfigRate5 = tradeFeeConfigService.selectUserFeeConfig("test2");
        Assert.isTrue(
                updateUserFee2.getSpotFeeRate().equals(userFeeConfigRate5.getSpotFeeRate().stripTrailingZeros()) &&
                        updateUserFee2.getSwapFeeRate().equals(userFeeConfigRate5.getSwapFeeRate().stripTrailingZeros()) &&
                        updateUserFee2.getMarginFeeRate().equals(userFeeConfigRate5.getMarginFeeRate().stripTrailingZeros()) &&
                        updateUserFee2.getMarginSettleFeeRate().equals(userFeeConfigRate5.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        updateUserFee2.getFundingCostEnable().equals(userFeeConfigRate5.getFundingCostEnable()));

        tradeFeeConfigService.updateFundingCostConfig("test", false);
        UserFeeConfigRate userFeeConfigRate6 = tradeFeeConfigService.selectUserFeeConfig("test");
        Assert.isTrue(
                updateUserFee.getSpotFeeRate().equals(userFeeConfigRate6.getSpotFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getSwapFeeRate().equals(userFeeConfigRate6.getSwapFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getMarginFeeRate().equals(userFeeConfigRate6.getMarginFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getMarginSettleFeeRate().equals(userFeeConfigRate6.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        userFeeConfigRate6.getFundingCostEnable() == false);

        tradeFeeConfigService.updateFundingCostConfig("test", true);
        UserFeeConfigRate userFeeConfigRate7 = tradeFeeConfigService.selectUserFeeConfig("test");
        Assert.isTrue(
                updateUserFee.getSpotFeeRate().equals(userFeeConfigRate7.getSpotFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getSwapFeeRate().equals(userFeeConfigRate7.getSwapFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getMarginFeeRate().equals(userFeeConfigRate7.getMarginFeeRate().stripTrailingZeros()) &&
                        updateUserFee.getMarginSettleFeeRate().equals(userFeeConfigRate7.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        userFeeConfigRate7.getFundingCostEnable() == true);

        tradeFeeConfigService.delUserTradeFeeConfig("test");
        UserFeeConfigRate userFeeConfigRate8 = tradeFeeConfigService.selectUserFeeConfig("test");
        Assert.isTrue(
                defaultConfig.getSpotFeeRate().equals(userFeeConfigRate8.getSpotFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getSwapFeeRate().equals(userFeeConfigRate8.getSwapFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getMarginFeeRate().equals(userFeeConfigRate8.getMarginFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getMarginSettleFeeRate().equals(userFeeConfigRate8.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        defaultConfig.getFundingCostEnable().equals(userFeeConfigRate8.getFundingCostEnable()));


        TradeFeeConfigData updateUserFee3 = new TradeFeeConfigData();
        updateUserFee3.setUid("test2");
        updateUserFee3.setAlgorithmicFeeRate(new BigDecimal("0.0001"));
        updateUserFee3.setSpotFeeRate(new BigDecimal("-1"));
        updateUserFee3.setSwapFeeRate(new BigDecimal("0.0001"));
        updateUserFee3.setMarginFeeRate(new BigDecimal("0.0001"));
        updateUserFee3.setMarginSettleFeeRate(new BigDecimal("0.0001"));
        updateUserFee3.setFundingCostEnable(true);
        tradeFeeConfigService.insertOrUpdateUser(updateUserFee3);

        UserFeeConfigRate userFeeConfigRate9 = tradeFeeConfigService.selectUserFeeConfig("test2");
        Assert.isTrue(
                defaultConfig.getSpotFeeRate().equals(userFeeConfigRate9.getSpotFeeRate().stripTrailingZeros()) &&
                        updateUserFee3.getSwapFeeRate().equals(userFeeConfigRate9.getSwapFeeRate().stripTrailingZeros()) &&
                        updateUserFee3.getMarginFeeRate().equals(userFeeConfigRate9.getMarginFeeRate().stripTrailingZeros()) &&
                        updateUserFee3.getMarginSettleFeeRate().equals(userFeeConfigRate9.getMarginSettleFeeRate().stripTrailingZeros()) &&
                        updateUserFee3.getFundingCostEnable().equals(userFeeConfigRate9.getFundingCostEnable()));

        return Response.ok(userFeeConfigRate);
    }

    public static void main(String[] args) {
        java.util.Date ctime = new java.util.Date();
        System.out.println("ctime:" + ctime);
        Calendar instance = Calendar.getInstance();
        instance.setTime(ctime);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);


        java.util.Date now1 = new java.util.Date();
        java.util.Date now2 = new java.util.Date();

        System.out.println(now1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(now2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

        System.out.println(StringUtils.arrayToDelimitedString(IntStream.range(0, 31).boxed().toArray(), ","));

        HashMap<String, String> map = new HashMap<>();
        String s = map.computeIfAbsent("1", k -> k);
        System.out.println(s);
        String s2 = map.computeIfAbsent("2", k -> k);
        System.out.println(s2);
        String s3 = map.computeIfAbsent("2", k -> k);
        System.out.println(s3);
    }

    @GetMapping("redis/zset")
    @ApiOperation(value = "reids zset测试")
    public Response redisZset() {
        String test1 = "test1";
        String test2 = "test2";
        RScoredSortedSet<RedisTradeDTO> scoredSortedSet1 = redissonClient.getScoredSortedSet(RedisKeyConstants.getTradeUserTransKey(test1));
        scoredSortedSet1.clear();
        HashMap<RedisTradeDTO, Double> param = new HashMap<>();
        param.put(new RedisTradeDTO(BigDecimal.ONE, System.currentTimeMillis() - 1 * 5), Double.valueOf(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN)).getTime()));
        param.put(new RedisTradeDTO(BigDecimal.ONE, System.currentTimeMillis() - 1 * 10), Double.valueOf(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.MIN)).getTime()));
        param.put(new RedisTradeDTO(BigDecimal.ONE, System.currentTimeMillis() - 1 * 16), Double.valueOf(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.MIN)).getTime()));
        param.put(new RedisTradeDTO(BigDecimal.ONE, System.currentTimeMillis() - 1 * 20), Double.valueOf(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusDays(4), LocalTime.MIN)).getTime()));
        scoredSortedSet1.addAll(param);
        LocalDate now = LocalDate.now();
        LocalDate outer30d = now.minusDays(31);
        scoredSortedSet1.add(Timestamp.valueOf(LocalDateTime.of(outer30d, LocalTime.MIN)).getTime(), new RedisTradeDTO(BigDecimal.ONE, System.currentTimeMillis()));
        LocalDate outer60d = now.minusDays(61);
        scoredSortedSet1.add(Timestamp.valueOf(LocalDateTime.of(outer60d, LocalTime.MIN)).getTime(), new RedisTradeDTO(BigDecimal.ONE, System.currentTimeMillis()));
        BigDecimal tradeAmount = tradeAssetService.get30TradeAmount("1");
        Assert.isTrue(tradeAmount.compareTo(new BigDecimal("4")) == 0);

        Double firstScore = scoredSortedSet1.firstScore();
        Assert.isTrue(firstScore.longValue() == Timestamp.valueOf(LocalDateTime.of(outer60d, LocalTime.MIN)).getTime());

        long start = firstScore.longValue();
        LocalDate endLocalDate = LocalDate.now().minusDays(60);
        long end = Timestamp.valueOf(LocalDateTime.of(endLocalDate, LocalTime.MIN)).getTime();
        scoredSortedSet1.removeRangeByScore(start, true,
                end, false);
        tradeAmount = tradeAssetService.get30TradeAmount("1");
        Assert.isTrue(tradeAmount.compareTo(new BigDecimal("4")) == 0);

        BigDecimal big = scoredSortedSet1.valueRange(start, true, end, true).stream().map(RedisTradeDTO::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        Assert.isTrue(big.compareTo(BigDecimal.ZERO) == 0);

        RScoredSortedSet<RedisTradeDTO> scoredSortedSet2 = redissonClient.getScoredSortedSet(RedisKeyConstants.getTradeUserTransKey(test2));
        scoredSortedSet2.clear();
        scoredSortedSet2.add(System.currentTimeMillis(), new RedisTradeDTO(BigDecimal.ONE, System.currentTimeMillis()));
        RKeys keys = redissonClient.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(RedisKeyConstants.getTradeUserTransKey("*"));
        for (String key : keysByPattern) {
            Assert.isTrue(key.equals(RedisKeyConstants.getTradeUserTransKey(test1)) || key.equals(RedisKeyConstants.getTradeUserTransKey(test2)));
        }
        return Response.ok();
    }

    @GetMapping("common/config")
    @ApiOperation(value = "config测试")
    public Response testConfig() {
        return configClient.allConfigInfo();
    }


    @GetMapping("json")
    @ApiOperation(value = "json序列化测试")
    public Response testJson() throws JsonProcessingException {
        String json = "{\n" +
                "    \"type\" : \"DEFAULT\",\n" +
                "    \"data\": {\n" +
                "            \"tag\": \"DEFAULT\",    \n" +
                "            \"spot_fee_rate\": \"0.0005\", \n" +
                "            \"swap_fee_rate\": \"0.0005\", \n" +
                "            \"margin_fee_rate\": \"0.0005\", \n" +
                "            \"algorithmic_fee_rate\": \"0.0005\", \n" +
                "            \"margin_settle_fee_rate\": \"0.0005\", \n" +
                "            \"funding_cost_enable\": true\n" +
                "    }\n" +
                "}";
        TradeFeeConfigData tradeFeeConfigData = objectMapper.readValue(json, TradeFeeConfigData.class);
        return Response.ok(tradeFeeConfigData);
    }

    @GetMapping("/i18n")
    @ApiOperation(value = "测试i18n")
    public Response testI18n() {
        TestUser testUser = new TestUser();
        testUser.setUsername("user_name");
        testUser.setPasswrod("password");
        return Response.ok(testUser);
    }

    @Autowired
    private SpotService spotService;

    @Data
    class TestUser {
        @JsonSerialize(using = I18nConvertSerializer.class)
        private String username;
        @JsonSerialize(using = I18nConvertSerializer.class)
        private String passwrod;
    }

    @PostMapping("/redis")
    @ApiOperation(value = "测试pdt redis连通性")
    public Response<String> testPdtRedis() {
        String result = pdtRedisTemplate.execute(RedisConnectionCommands::ping);
        if (null != result) {
            return Response.ok(result);
        }
        return Response.fail("fail");
    }

    @PostMapping("/spot/placeOrder/byid")
    @ApiOperation(value = "测试根据OrderId 现货下单")
    public Response exsOrder(String orderId, String userId) {
        TradeSpotOrder order = spotService.querySpotOrderById(orderId, userId);
        spotService.checkAndFillOrder(order, true);
        return Response.ok();
    }

    private final static String testSymbol = "BTC_USD";
    private final static String testUser = "61628983d4b1a6d195d6f285";

    @PostMapping("/spot/placeOrder")
    @ApiOperation(value = "测试现货下单接口")
    public Response addOrder() {
        SpotOrderPlace req = new SpotOrderPlace();
        req.setSymbol(testSymbol);
        req.setUid(testUser);

        req.setType(OrderType.LIMIT);
        req.setStrategy(TradeStrategy.FOK);
        req.setDirection(Direction.SELL);
        req.setIsQuote(true);
        req.setQuantity(new BigDecimal("100"));
        req.setPrice(new BigDecimal("3826"));
        req.setNotes("modify");
        req.setSource(SourceType.PLACED_BY_CLIENT);

        SpotOrderPlaceRes res = spotService.placeOrder(req);
        if (res != null) {
            System.out.println("result=" + res.toString());
        }
        return Response.ok();
    }


    @PostMapping("/redission")
    @ApiOperation(value = "redission")
    public void testLoop() throws InterruptedException {
        RLock lock = redissonClient.getLock("test-redission");
        lock.lock();
    }

    @GetMapping("/cache")
    @ApiOperation(value = "测试cache数据")
    public Response<Map<String, Object>> testCache() {
        Map<String, Object> map = new HashMap<>();
        map.put("coin", CoinDomain.CACHE);
        map.put("symbol", SymbolDomain.CACHE);
        return Response.ok(map);
    }

    @GetMapping("/kafka")
    @ApiOperation(value = "测试kafka")
    public Response<String> testKafka() {
        kafkaTemplate.send("google-google-referral-funding-trading-dev", "trading test msg");
        return Response.ok("ok");
    }

    @PostMapping("/migrate/first")
    @ApiOperation(value = "测试migrate第一轮")
    public Response<String> testMigrateFirst() {
        //第一轮
        appMigrateHandle.migrateContractTransaction(AppMigrateHandle.START, AppMigrateHandle.END);
        appMigrateHandle.migrateSwapTransaction(AppMigrateHandle.START, AppMigrateHandle.END);
        appMigrateHandle.migrateFundingCost(AppMigrateHandle.START, AppMigrateHandle.END);
        appMigrateHandle.migrateSwapOrder(AppMigrateHandle.START, AppMigrateHandle.END);
        appMigrateHandle.migrateSpotOrder();
        appMigrateHandle.migrateContractOrder();
        appMigrateHandle.migratePosition();
        return Response.ok("ok");
    }

    @PostMapping("/migrate/second")
    @ApiOperation(value = "测试migrate第二轮")
    public Response<String> testMigrateSecond() {
        //第二轮
        appMigrateHandle.migrateWatchList();
        appMigrateHandle.migrateActiveSpotOrder();
        appMigrateHandle.migrateActiveMarginOrder();
        appMigrateHandle.migrateFundingCost(AppMigrateHandle.END, AppMigrateHandle.FINAL);
        appMigrateHandle.migrateContractTransaction(AppMigrateHandle.END, AppMigrateHandle.FINAL);
        appMigrateHandle.migrateSwapTransaction(AppMigrateHandle.END, AppMigrateHandle.FINAL);
        appMigrateHandle.migrateSwapOrder(AppMigrateHandle.END, AppMigrateHandle.FINAL);
        appMigrateHandle.migrateActivePosition();
        return Response.ok("ok");
    }

    @PostMapping("/migrate/rewrite")
    @ApiOperation(value = "测试migrate重算pro的pnl")
    public Response<String> testMigrateRewritePnl() {
        appMigrateHandle.reCalProPositionPnl();
        return Response.ok("ok");
    }

    @PostMapping("/alarm")
    @ApiOperation(value = "测试告警")
    public Response<String> alarm() {
        alarmComponent.asyncAlarm(AlarmEnum.SPOT_PLACE_ORDER_ERROR, "测试告警");
        return Response.ok();
    }
}
