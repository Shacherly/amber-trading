package com.google.backend.trading.service;

import com.google.backend.asset.common.model.asset.req.FundRecordReq;
import com.google.backend.asset.common.model.asset.req.PoolReq;
import com.google.backend.asset.common.model.asset.res.FundRecordRes;
import com.google.backend.asset.common.model.base.PoolEntity;
import com.google.backend.asset.common.model.trade.req.TradeSpotOrderReq;
import com.google.backend.common.web.PageResult;
import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AssetInfoClient;
import com.google.backend.trading.client.feign.AssetTradeClient;
import com.google.backend.trading.dao.mapper.DefaultTradeSpotOrderMapper;
import com.google.backend.trading.dao.model.TradeSpotOrder;
import com.google.backend.trading.dao.model.TradeSpotOrderExample;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceReq;
import com.google.backend.trading.model.spot.api.SpotOrderPlaceRes;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderStatus;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import com.google.backend.trading.model.trade.TriggerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * @author trading
 * @date 2021/9/27 13:34
 */
@Slf4j
@Service
public class DemoService {

	@Autowired
	private AssetInfoClient assetInfoClient;
	@Autowired
	private AssetTradeClient assetTradeClient;
	@Autowired
	private SpotService spotService;
	@Autowired
	private DefaultTradeSpotOrderMapper spotOrderMapper;

	private static String testUser = "60b08e48c64cb15040ffdb69";
	private static String testSymbol = "ETH_USD";
	private static BigDecimal testAmount = new BigDecimal("3456");

	public String demo() {
		return "ok";
	}

	public SpotOrderPlace createSpotRequest() {
		SpotOrderPlace req = new SpotOrderPlace();
		req.setSymbol(testSymbol);
		OrderType orderType = OrderType.getByCode(RandomTestData.getStrInt(1, 5));
		req.setType(orderType);
		if (orderType == OrderType.LIMIT) {
			req.setPrice(RandomTestData.getDouble(3000, 4000));
		} else if (orderType == OrderType.STOP_LIMIT || orderType == OrderType.STOP_MARKET) {
			TriggerType comp = RandomTestData.getBool() ? TriggerType.GREATER : TriggerType.LESS;
			req.setTriggerCompare(comp);
			req.setTriggerPrice(RandomTestData.getDouble(3000, 4000));
		}
		TradeStrategy strategy = TradeStrategy.getByCode(RandomTestData.getStrInt(1, 3));
		req.setStrategy(strategy);
		Direction direct = Direction.getByCode(RandomTestData.getStrInt(1, 2));
		req.setDirection(direct);
		req.setIsQuote(RandomTestData.getBool());
		req.setQuantity(RandomTestData.getDouble(1, 10));
		req.setNotes(RandomTestData.getString(20));
		SourceType source = SourceType.getByCode(RandomTestData.getStrInt(1, 4));
		req.setSource(source);
		return req;
	}

	public OrderRequest.ChangeOrderData createChangeData() {
		OrderRequest.ChangeOrderData change = new OrderRequest.ChangeOrderData();
		change.setFilledPrice(RandomTestData.getDouble(1000, 10000));
		change.setQtyFilled(RandomTestData.getDouble(100, 1000));
		change.setAmountFilled(RandomTestData.getDouble(100, 1000));
		change.setFee(RandomTestData.getDouble(1, 10));
		change.setPnl(RandomTestData.getDouble(10, 100));
		OrderStatus status = OrderStatus.getByName(RandomTestData.getStrInt(1, 9));
		change.setStatus(status);
		if (status == OrderStatus.EXCEPTION) {
			change.setError(RandomTestData.getString(10));
		}
		return change;
	}

	//接收客户端请求同步处理订单
	public String placeOrder(SpotOrderPlaceReq spotReq) {
		BigDecimal quantity = spotReq.getQuantity();
		//检查quantity等其他参数
		boolean checkPass = true;
		//生成order写入db
		TradeSpotOrder order = null;
		if (checkPass) {
			Object o = checkPriceAndFillOrder(order);
			return "ok";
		} else {
			return "fail";
		}
	}

	//接收请求处理（可能是后台检查线程调用，可能是客户端请求调用）
	//检查价格触发发单和pdt交互
	public Object checkPriceAndFillOrder(TradeSpotOrder order) {
		TradeSpotOrderExample example = new TradeSpotOrderExample();
		//where id = order.getId()
		example.createCriteria().andIdEqualTo(order.getId()).andStatusEqualTo("EXECUTING");

		TradeSpotOrder update = new TradeSpotOrder();
		update.setStatus("CANCELED");
		int i = spotOrderMapper.updateByExampleSelective(update, example);
		//逻辑处理
		return null;
	}

	public void mybatisDemo() {
		TradeSpotOrder order = spotOrderMapper.selectByPrimaryKey(1L);
		log.info("order = {}", order);
	}

	public void feignDemo() {
		PoolReq req = new PoolReq();
		req.setUid(testUser);
		//List<String> coins = new ArrayList<String>();
		//coins.add("BTC");
		req.setCoin(Collections.emptyList());
		Response<Map<String, PoolEntity>> resp = assetInfoClient.getPool(req);
		log.info("getPool code={} data={}", resp.getCode(), resp.getData());
	}

	public void testFundRecord() {
		FundRecordReq req = new FundRecordReq();
		req.setUid(testUser);
		req.setCoins(Collections.emptyList());
		Response<PageResult<FundRecordRes>> pool = assetInfoClient.getFundRecord(req);
		log.info("getFundRecord pool = {}", pool.getData());
	}

	public void testPlaceOrder() {
		TradeSpotOrderReq req = new TradeSpotOrderReq();
        req.setReqId(RandomTestData.getStrInt(100, 100000));

		TradeSpotOrderReq.Params param = new TradeSpotOrderReq.Params();
		param.setUid(testUser);
		param.setCoin(testSymbol);
		param.setAmount(testAmount);
		req.setParams(param);

		Response<Void> resp = assetTradeClient.doSpotOrder(req);
		log.info("doSpotOrder code={} data={}", resp.getCode(), resp.getData());
	}


	public void testAddOrder() {
		for (int i = 0; i < 3; i++) {
			SpotOrderPlace placeReq = createSpotRequest();
			SpotOrderPlaceRes resp = spotService.placeOrder(placeReq);
			log.info("placeOrder order={}", resp.toString());
		}
	}

}
