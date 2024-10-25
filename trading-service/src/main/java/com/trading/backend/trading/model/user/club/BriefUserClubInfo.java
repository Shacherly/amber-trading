package com.google.backend.trading.model.user.club;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author david.chen
 * @date 2022/1/5 19:43
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BriefUserClubInfo {
    @ApiModelProperty("uid")
    private String uid;
    @ApiModelProperty("club 等级（-1级为从未入金新用户，0级为普通0级用户）")
    private Integer clubLevel;
    @ApiModelProperty("club值")
    private Long clubPoint;
    @ApiModelProperty("当前等级最小google值")
    private Long clubPointMin;
    @ApiModelProperty("当前等级最大google值")
    private Long clubPointMax;
    @ApiModelProperty("有效期")
    private Long validUntil;
    @ApiModelProperty("是否保级")
    private Boolean preserved;
    @ApiModelProperty("当前是否有等级提升")
    private Boolean upgrade;
    @ApiModelProperty("bwc是否首次访问")
    private Boolean whaleFirstAccess;
    @ApiModelProperty("bwc等级(1、2、3级，-1级为非bwc用户)")
    private Integer whaleLevel;
    @ApiModelProperty("bwc等级是否有提升")
    private Boolean whaleUpgrade;
}
