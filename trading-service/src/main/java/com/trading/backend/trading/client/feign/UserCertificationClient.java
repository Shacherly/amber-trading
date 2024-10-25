package com.google.backend.trading.client.feign;

import com.google.backend.common.model.kyc.res.ClientKycStatusRes;
import com.google.backend.common.model.web.Response;
import com.google.backend.trading.config.feign.FeignConfig;
import feign.Headers;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author david.chen
 * @date 2021/10/29 11:27
 */

@FeignClient(name = "UserCertificationClient", url = "${user-center.host}", configuration = FeignConfig.class)
@RequestMapping(value = "/internal/v1/certification",headers = {"origin_channel=BACKEND"})
public interface UserCertificationClient {
    /**
     * 查询用户状态，包括KYC国家
     * @param uid
     * @return
     */
    @GetMapping(value = "/certification-status")
    @ApiOperation(value = "用户认证状态详情", notes = "用户认证状态详情")
    Response<ClientKycStatusRes> getCertificationStatus(@RequestParam("uid") String uid);

}
