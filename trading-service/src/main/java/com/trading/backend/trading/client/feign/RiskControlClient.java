package com.google.backend.trading.client.feign;


import com.google.backend.riskcontrol.httpclient.client.feign.TradeApi;
import com.google.backend.trading.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author adam.wang
 * @date 2021/10/2 20:11
 */
@FeignClient(name = "riskControlClient", url = "${risk.host}", configuration = FeignConfig.class)
public interface RiskControlClient extends TradeApi {

}
