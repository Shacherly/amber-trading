package com.google.backend.trading.controller;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.funding.api.FundingRateHistoryReq;
import com.google.backend.trading.model.funding.api.FundingRateHistoryRes;
import com.google.backend.trading.model.funding.api.FundingRateRes;
import com.google.backend.trading.service.FundingCostService;
import com.google.backend.trading.util.CustomCellWriteHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资金费率
 *
 * @author trading
 * @date 2021/9/27 16:32
 */
@Slf4j
@Api(value = "资金费率相关接口", tags = "资金费率相关接口")
@RestController
@Validated
@RequestMapping("/v1/funding")
public class FundingCostController {
	private static final ThreadLocal<ObjectMapper> OM = ThreadLocal.withInitial(() -> {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	});


	@Resource
	FundingCostService fundingCostService;

	@ApiOperation(value = "实时资金费率")
	@GetMapping("/rate")
	public Response<List<FundingRateRes>> rate() {
		return Response.ok(fundingCostService.realTimeRate());
	}

	@ApiOperation(value = "历史资金费率")
	@GetMapping("/rateHistory")
	public Response<PageResult<FundingRateHistoryRes>> rateHistory(@Valid @RequestUnderlineToCamel FundingRateHistoryReq req) {
		return Response.ok(fundingCostService.listHistoryRate(req));
	}

	@GetMapping("/rate/history.xlsx")
	@ApiOperation("导出")
	public void export(HttpServletResponse response, @RequestParam String coin) throws IOException {
		List<FundingRateHistoryRes> list = fundingCostService.listRateByCoin(coin);
		if (Constants.BASE_COIN.equals(coin)) {
			list.forEach(item -> item.setCoin(Constants.BASE_COIN_AND_S));
		}
		try {
			// 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setCharacterEncoding("utf-8");
			String fileName = URLEncoder.encode("google-Funding-Rate", "UTF-8").replaceAll("\\+", "%20");
			response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
			EasyExcel.write(response.getOutputStream(), FundingRateHistoryRes.class).registerWriteHandler(new CustomCellWriteHandler()).autoCloseStream(Boolean.FALSE)
					.sheet("google Pro-Funding-Rate").doWrite(list);
		} catch (Exception e) {
			// 重置response
			response.reset();
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			Map<String, String> map = new HashMap<>();
			map.put("status", "failure");
			map.put("message", "下载文件失败" + e.getMessage());
			log.error("export error:", e);
			response.getWriter().println(OM.get().writeValueAsString(map));
		}
	}
}
