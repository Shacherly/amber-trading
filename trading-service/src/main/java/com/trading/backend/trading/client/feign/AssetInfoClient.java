package com.google.backend.trading.client.feign;


import com.google.backend.asset.client.feign.AssetInfoApi;
import com.google.backend.asset.common.model.asset.req.GetReqIdStatusReq;
import com.google.backend.common.web.Response;
import com.google.backend.trading.config.feign.FeignConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author trading
 * @date 2021/8/19 17:13
 */
@FeignClient(name = "assetInfoClient", url = "${asset.host}", configuration = FeignConfig.class)
public interface AssetInfoClient extends AssetInfoApi {
    @PostMapping("/get-req-id-status")
    @ApiOperation(value = "获取请求ID状态", notes = "1.reqId不存在即表示未执行,返回错误码：20100250,错误信息：no req_id record," +
            " 2.已经执行成功返回 data: 0, 3.已经回退返回 data: 1")
    Response<Integer> getReqIdStatus(@RequestBody @Validated GetReqIdStatusReq req);
}
