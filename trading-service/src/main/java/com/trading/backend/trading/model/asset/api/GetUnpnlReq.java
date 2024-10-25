package com.google.backend.trading.model.asset.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author trading
 * @date 2021/11/3 20:05
 */
@Data
@ApiModel(value = "资金unpnl请求数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetUnpnlReq {

	@NotEmpty
	private List<String> uidList;

}
