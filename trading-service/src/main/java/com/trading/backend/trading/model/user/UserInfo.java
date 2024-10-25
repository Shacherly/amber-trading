package com.google.backend.trading.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * 用户信息，由网关header内容转换而来
 * @author adam.wang
 * @date 2021/10/1 15:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public final class UserInfo implements Serializable {

    public static final String CURRENCY_USER_TAG = "currency_user";

    public static final UserInfo MOCK = new UserInfo("616289a2d4b1a6d195d6f286", 0, null, null, null);

    /**
     * 用户id
     */
    @JsonProperty(value = "user_id")
    @ApiModelProperty(hidden = true)
    private String uid;

    /**
     * 用户kyc状态  0：未认证 1:个人认证 2:机构认证
     */
    @JsonProperty(value = "kyc_status")
    @ApiModelProperty(hidden = true)
    private Integer kycStatus;

    @JsonProperty(value = "ip")
    @ApiModelProperty(hidden = true)
    private String ip;

    @JsonProperty(value = "ip_country_code")
    @ApiModelProperty(hidden = true)
    private String ipCountryCode;

    @JsonProperty(value = "ip_region_code")
    @ApiModelProperty(hidden = true)
    private String ipRegionCode;
}
