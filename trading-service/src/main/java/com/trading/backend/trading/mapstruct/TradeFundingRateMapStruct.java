package com.google.backend.trading.mapstruct;


import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.dao.model.TradeFundingRate;
import com.google.backend.trading.model.funding.api.FundingRateHistoryRes;
import com.google.backend.trading.model.funding.api.FundingRateRes;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * TradeFundingRate 转换工具
 * @author adam.wang
 * @date 2021/9/30 14:06
 */
@Mapper(componentModel="spring")
public interface TradeFundingRateMapStruct {

    /**
     * tradeFundingRate转换为fundingRateRes
     * @param tradeFundingRate
     * @return
     */
    FundingRateRes tradeFundingRate2fundingRateRes(TradeFundingRate tradeFundingRate);


    /**
     * tradeFundingRate转换为FundingRateHistoryRes
     * @param tradeFundingRate
     * @return
     */
    FundingRateHistoryRes tradeFundingRate2fundingRateHistoryRes(TradeFundingRate tradeFundingRate);

    /**
     * List tradeFundingRate转换为fundingRateRes
     * @param tradeFundingRates
     * @return
     */
    List<FundingRateRes> tradeFundingRates2fundingRateRes(List<TradeFundingRate> tradeFundingRates);



    /**
     * List tradeFundingRate转换为FundingRateHistoryRes
     * @param tradeFundingRate
     * @return
     */
    List<FundingRateHistoryRes> tradeFundingRate2fundingRateHistoryRes(List<TradeFundingRate> tradeFundingRate);

}
