package com.google.backend.trading.model.trade.fee;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * ace up trade fee业务kafka传入的对象
 *
 * @author david.chen
 * @date 2021/12/30 16:57
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class AceTradeFeeConfigDTO {

    private String type;
    private TradeFeeConfigData data;

    public boolean isDefault() {
        return StringUtils.isNotEmpty(type) && KafkaTradeFeeTypeEnum.DEFAULT.name().equals(type) &&
                isZeroOrPositive(data.getSpotFeeRate()) &&
                isZeroOrPositive(data.getSwapFeeRate()) &&
                isZeroOrPositive(data.getMarginFeeRate()) &&
                isZeroOrPositive(data.getAlgorithmicFeeRate()) &&
                isZeroOrPositive(data.getMarginSettleFeeRate());
    }

    public boolean isDelUser() {
        return StringUtils.isNotEmpty(type) && KafkaTradeFeeTypeEnum.CUSTOMIZED.name().equals(type) &&
                StringUtils.isNotEmpty(data.getUid()) &&
                data.getSpotFeeRate() == null &&
                data.getSwapFeeRate() == null &&
                data.getMarginFeeRate() == null &&
                data.getAlgorithmicFeeRate() == null &&
                data.getMarginSettleFeeRate() == null;
    }

    public boolean isUpdateFundingCost() {
        return StringUtils.isNotEmpty(type) && KafkaTradeFeeTypeEnum.CUSTOMIZED.name().equals(type) &&
                StringUtils.isNotEmpty(data.getUid()) &&
                !isZeroOrPositive(data.getSpotFeeRate()) &&
                !isZeroOrPositive(data.getSwapFeeRate()) &&
                !isZeroOrPositive(data.getMarginFeeRate()) &&
                !isZeroOrPositive(data.getAlgorithmicFeeRate()) &&
                !isZeroOrPositive(data.getMarginSettleFeeRate());
    }

    private boolean isZeroOrPositive(BigDecimal num) {
        return (num != null) && (num.compareTo(BigDecimal.ZERO) >= 0);
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = "{\n" +
                "    \"data\":  {\n" +
                "        \"uid\": \"xxxx\",\n" +
                "        \"spot_fee_rate\": \"-1\", \n" +
                "        \"swap_fee_rate\": \"-1\", \n" +
                "        \"margin_fee_rate\": \"-1\",\n" +
                "        \"algorithmic_fee_rate\": \"-1\",\n" +
                "        \"margin_settle_fee_rate\": \"-1\", \n" +
                "        \"funding_cost_enable\": false\n" +
                "    },\n" +
                "    \"type\": \"CUSTOMIZED\"\n" +
                "}";
        AceTradeFeeConfigDTO aceTradeFeeConfigDTO = objectMapper.readValue(s, AceTradeFeeConfigDTO.class);
        Assert.isTrue(aceTradeFeeConfigDTO.isUpdateFundingCost());
        s = "{\n" +
                "    \"data\":  {\n" +
                "        \"uid\": \"xxxx\",\n" +
                "        \"spot_fee_rate\": \"-1\", \n" +
                "        \"swap_fee_rate\": \"-1\", \n" +
                "        \"margin_fee_rate\": \"-1\",\n" +
                "        \"algorithmic_fee_rate\": \"-1\",\n" +
                "        \"margin_settle_fee_rate\": \"0\", \n" +
                "        \"funding_cost_enable\": false\n" +
                "    },\n" +
                "    \"type\": \"CUSTOMIZED\"\n" +
                "}";
        aceTradeFeeConfigDTO = objectMapper.readValue(s, AceTradeFeeConfigDTO.class);
        Assert.isFalse(aceTradeFeeConfigDTO.isDefault() && aceTradeFeeConfigDTO.isDelUser() && aceTradeFeeConfigDTO.isUpdateFundingCost());

        s = "{\n" +
                "    \"data\":  {\n" +
                "        \"uid\": \"xxxx\"\n" +
                "    },\n" +
                "    \"type\": \"CUSTOMIZED\"\n" +
                "}";
        aceTradeFeeConfigDTO = objectMapper.readValue(s, AceTradeFeeConfigDTO.class);
        Assert.isTrue(aceTradeFeeConfigDTO.isDelUser());
        s = "{\n" +
                "    \"data\":  {\n" +
                "        \"tag\": \"DEFAULT\",\n" +
                "        \"spot_fee_rate\": \"-1\", \n" +
                "        \"swap_fee_rate\": \"0.01\", \n" +
                "        \"margin_fee_rate\": \"0.1\",\n" +
                "        \"algorithmic_fee_rate\": \"0\",\n" +
                "        \"margin_settle_fee_rate\": \"0.01\", \n" +
                "        \"funding_cost_enable\": false\n" +
                "    },\n" +
                "    \"type\": \"DEFAULT\"\n" +
                "}";
        aceTradeFeeConfigDTO = objectMapper.readValue(s, AceTradeFeeConfigDTO.class);
        Assert.isFalse(aceTradeFeeConfigDTO.isDefault());

        s = "{\n" +
                "    \"data\":  {\n" +
                "        \"tag\": \"DEFAULT\",\n" +
                "        \"spot_fee_rate\": \"0.01\", \n" +
                "        \"swap_fee_rate\": \"0.01\", \n" +
                "        \"margin_fee_rate\": \"0.01\",\n" +
                "        \"algorithmic_fee_rate\": \"0.01\",\n" +
                "        \"margin_settle_fee_rate\": \"0\", \n" +
                "        \"funding_cost_enable\": false\n" +
                "    },\n" +
                "    \"type\": \"DEFAULT\"\n" +
                "}";
        aceTradeFeeConfigDTO = objectMapper.readValue(s, AceTradeFeeConfigDTO.class);
        Assert.isTrue(aceTradeFeeConfigDTO.isDefault());
    }
}
