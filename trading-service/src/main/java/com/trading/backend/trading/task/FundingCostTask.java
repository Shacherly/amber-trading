package com.google.backend.trading.task;

import com.google.backend.trading.service.FundingCostService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author trading
 * @date 2021/10/12 19:54
 */
@Slf4j
@Component
public class FundingCostTask {


	@Autowired
	private FundingCostService fundingCostService;
	/**
	 * 保存历史费率每半小时
	 */
	@XxlJob("historyToDb")
	public void halfHourHistoryToDb() {
		fundingCostService.batchSaveHalfHourHistory();
	}

}
