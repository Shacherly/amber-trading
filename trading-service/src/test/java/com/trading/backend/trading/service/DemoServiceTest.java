package com.google.backend.trading.service;


import com.google.backend.common.model.kyc.res.ClientKycStatusRes;
import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.client.feign.UserCertificationClient;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.commonconfig.dto.AllConfig;
import com.google.backend.trading.client.feign.CommonConfigClient;
import com.google.backend.trading.model.spot.dto.SpotOrderPlace;
import com.google.backend.trading.push.PushComponent;
import com.google.backend.trading.push.PushEventEnum;
import com.google.backend.trading.push.WsPushMessage;
import com.google.backend.trading.task.CheckTimeZoneSettleTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author trading
 * @date 2021/9/27 13:35
 */
@Slf4j
public class DemoServiceTest extends TradingServiceApplicationTest {

	@Autowired
	private DemoService demoService;

	@Autowired
	private PushComponent pushComponent;

	@Autowired
	private SpotService spotService;

	@Autowired
	private CheckTimeZoneSettleTask settleTask;

	@Test
	public void testSettleTask() {
		settleTask.triggerSettle();
	}

	@Test
	public void testWsPush() throws InterruptedException {
		WsPushMessage<String> message = WsPushMessage.buildAllConsumersMessage("616289a2d4b1a6d195d6f288",
				PushEventEnum.SWAP_ORDER_UPDATE, "trading test message");
		pushComponent.pushWsMessage(message);
		Thread.sleep(5000);
	}

	@Test
	public void testSpotServiceValidate() {
		spotService.placeOrder(new SpotOrderPlace());
	}


	@Test
	public void testDemoOk() {
		String actualValue = demoService.demo();
		Assert.assertEquals("ok", actualValue);
	}

	@Test
	public void testDemoFail() {
		String actualValue = demoService.demo();
		Assert.assertEquals("fail", actualValue);
	}

	@Test
	public void feignDemoTest() {
		demoService.feignDemo();
		//demoService.testFundRecord();
		//demoService.testPlaceOrder();
	}

	@Test
	public void mybatisDemo() {
		demoService.mybatisDemo();
	}

	@Test
	public void testDatabase() {
		demoService.testAddOrder();
//		demoService.testUpdateOrder();
	}

	@Autowired
	private CommonConfigClient configClient;

	@Test
	public void testCommonConfig() {
		//Response<AllConfigRes> res = configClient.allConfigInfo();
		//log.info("commonConfig data = {}", res.getData());
		Response<AllConfig> res = configClient.configInfoByType(1);
		log.info("commonConfig data = {}", res.getData());
	}

	@Autowired
	private UserCertificationClient userCertificationClient;

	@Test
	public void testCertificationStatus() {
		String uid = "0000000001";
		com.google.backend.common.model.web.Response<ClientKycStatusRes> certificationStatus = userCertificationClient.getCertificationStatus(uid);
		log.info("userid:{},certificationStatus data = {}",uid,certificationStatus);
	}
	@Autowired
	private MessageSource messageSource;

	@Test
	public void testI18n() {
		Locale locale = LocaleContextHolder.getLocale();
		String user_name = messageSource.getMessage("user_name", null, locale);
		log.info(user_name);
	}
}
