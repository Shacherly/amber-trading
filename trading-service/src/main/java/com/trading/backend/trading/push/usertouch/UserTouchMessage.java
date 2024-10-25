package com.google.backend.trading.push.usertouch;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.common.utils.UuidUtils;
import com.google.backend.trading.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 用户触达消息
 *
 * @author adam.wang
 * @date 2021/10/27 20:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserTouchMessage {
    @NotNull
    private String systemId;
    @NotNull
    private String uid;
    @NotNull
    private String requestId;
    //@NotNull
    //private String scenario;

    @NotNull
    private List<String> type;
    @NotNull
    private List<String> templateCodeList;
    private Map<String, String> params;
    private Map<String, String> userParams;


    public UserTouchMessage(String uid, ScenarioType scenario, List<ChannelType> channels,
                            List<TemplateCode> codes, Map<String, String> params) {
        this.uid = uid;
        //this.scenario = scenario.getCode();
        this.systemId = Constants.SERVICE_NAME;
        this.requestId = UuidUtils.generateUuidHex();
        this.type = ChannelType.listTypes(channels);
        this.templateCodeList = TemplateCode.listTypes(codes);
        this.params = params;
    }

}
