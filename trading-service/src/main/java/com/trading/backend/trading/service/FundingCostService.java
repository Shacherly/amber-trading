package com.google.backend.trading.service;

import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.funding.api.FundingRateHistoryReq;
import com.google.backend.trading.model.funding.api.FundingRateHistoryRes;
import com.google.backend.trading.model.funding.api.FundingRateRes;
import com.google.backend.trading.model.internal.amp.AmpPositionFundReq;
import com.google.backend.trading.model.internal.amp.AmpPositionFundRes;

import java.util.Date;
import java.util.List;

/**
 * 资金费率相关
 * @author adam.wang
 * @date 2021/9/30 11:16
 */
public interface FundingCostService {

    /**
     * 实时资金费率，可以有coin入参
     * @return
     */
    List<FundingRateRes> realTimeRate();

    /**
     * 查询历史资金费率，必须有coin入参，查询结果按时间倒序
     * @param fundingRateHistoryReq
     * @return
     */
    PageResult<FundingRateHistoryRes> listHistoryRate(FundingRateHistoryReq fundingRateHistoryReq);


    /**
     * 查询365天资金费率
     * @param coin
     * @return
     */
    List<FundingRateHistoryRes> listRateByCoin(String coin);

    /**
     * 每半小时 批量保存历史费率
     */
    void batchSaveHalfHourHistory();

    /**
     * 结算仓位的资金费用
	 * @param uidList
	 * @param settleTime
	 */
    void settlePositionFundingCost(List<String> uidList, Date settleTime);

    /**
     * 结算负余额的资金费用
	 * @param uidList
	 * @param settleTime
	 */
    void settleNegativeBalanceFundingCost(List<String> uidList, Date settleTime);

    PageResult<AmpPositionFundRes> getHistoryFundForAmp(AmpPositionFundReq ampPositionFundReq);
}
