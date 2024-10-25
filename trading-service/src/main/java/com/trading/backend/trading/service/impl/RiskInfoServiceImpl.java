package com.google.backend.trading.service.impl;

import com.google.backend.common.web.Response;
import com.google.backend.riskcontrol.common.model.trade.res.GetRiskStatusRes;
import com.google.backend.trading.client.feign.RiskControlClient;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.service.RiskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author trading
 * @date 2021/12/6 17:15
 */
@Slf4j
@Service
public class RiskInfoServiceImpl implements RiskInfoService {

	@Autowired
	private RiskControlClient riskControlClient;

	@Override
	public void validateRiskStatus(String uid) {
		Response<GetRiskStatusRes> res = riskControlClient.getRiskStatusV2(uid);
		if (res.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
			GetRiskStatusRes riskStatus = res.getData();
			if (riskStatus.getBoom() || riskStatus.getLiquid()) {
				throw new BusinessException(BusinessExceptionEnum.REFUSE_OPERATE_IN_RISK_LIQUID);
			}
		} else {
			log.error("search risk status fail, pessimistic handle");
			throw new BusinessException(BusinessExceptionEnum.REFUSE_OPERATE_IN_RISK_LIQUID);
		}
	}
}
