package com.google.backend.trading.client.feign;

import com.google.backend.trading.config.feign.FeignConfig;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.user.club.BriefUserClubInfo;
import com.google.backend.trading.model.user.club.ClubInfoUidReq;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author david.chen
 * @date 2022/1/5 19:06
 */
@FeignClient(name = "clubClient", url = "${club.host}", configuration = FeignConfig.class)
@RequestMapping(value = "/internal/v1",headers = {"origin_channel=BACKEND"})
public interface ClubClient {
    @PostMapping("/brief/me")
    @ApiOperation(value = "查询用户club等级信息")
    Response<BriefUserClubInfo> getPersonalClub(ClubInfoUidReq clubInfoUidReq);
}
