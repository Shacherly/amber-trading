package com.google.backend.trading.task;

import com.github.pagehelper.PageHelper;
import com.google.backend.trading.component.TimeZone;
import com.google.backend.trading.config.TradeProperties;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.TradeUserTradeSettingMapper;
import com.google.backend.trading.service.FundingCostService;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.trace.annotation.TraceId;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检查时区结算
 * @author trading
 * @date 2021/10/22 8:52
 */
@Slf4j
@Component
public class CheckTimeZoneSettleTask {

	@Autowired
	private TradeUserTradeSettingMapper userTradeSettingMapper;

	@Autowired
	private FundingCostService fundingCostService;

	@Autowired
	private MarginService marginService;

	@Autowired
	private TradeProperties properties;

	/**
	 * 检查用户结算时区设置，执行结算
	 */
	@TraceId
	//@Scheduled(cron = "0 0/30 * * * ?")
	@XxlJob("triggerSettle")
	public void triggerSettle() {
		if (!properties.getTask().isEnabled()) {
			log.info("task disable");
			return;
		}

		try {
			Calendar time = Calendar.getInstance();
			int hour = time.get(Calendar.HOUR_OF_DAY);
			int minute = time.get(Calendar.MINUTE);
			if (minute < 30) {
				minute = 0;
			} else {
				minute = 30;
			}
			time.set(Calendar.MINUTE, minute);
			time.set(Calendar.SECOND, 0);
			time.set(Calendar.MILLISECOND, 0);

			long diffSeconds = Duration.ofHours(hour - Constants.SETTLE_HOUR_OF_DAY).plus(Duration.ofMinutes(minute)).getSeconds();
			long newZoneOffsetSeconds = TimeZone.UTC_0800_Beijing.getZoneOffsetSeconds() - diffSeconds;
			List<TimeZone> timeZones = TimeZone.getByOffsetSeconds((int) newZoneOffsetSeconds);
			if (CollectionUtils.isEmpty(timeZones)) {
				return;
			}
			//自定义sql查询符合条件（时区 in timeZones）的uid
			List<String> timeZoneIdList = timeZones.stream().map(TimeZone::getId).collect(Collectors.toList());
			log.info("need settle timeZoneId list = {}, settle time = {}", timeZoneIdList, time.getTime());
			int pageNum = 1;
			int pageSize = 5000;
			List<String> uidList;
			do {
				PageHelper.startPage(pageNum, pageSize);
				uidList = userTradeSettingMapper.selectNeedSettleUidList(timeZoneIdList, time.getTime());
				log.info("need settle uid list = {}", uidList);
				if (uidList.isEmpty()) {
					break;
				}
				for (String uid: uidList) {
					marginService.autoSettlePosition(uid);
				}
				//处理仓位资金费率
				fundingCostService.settlePositionFundingCost(uidList, time.getTime());
				//处理负余额资金费率
				fundingCostService.settleNegativeBalanceFundingCost(uidList, time.getTime());
				pageNum++;
			} while (true);
		} catch (Exception e) {
			log.error("triggerSettle handle err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}
	}

	public static void main(String[] args) {
		Calendar time = Calendar.getInstance();
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);

		System.out.println(time.getTime());

		for (int i = 1; i < 7; i++) {
			System.out.println("前"+i+"个小时");
			System.out.println(time.getTime()+"~");
			time.add(Calendar.HOUR_OF_DAY,-1);
			System.out.println(time.getTime());
			System.out.println("---------------");
		}
	}
}
