package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeFeeConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author trading
 * @date 2021/9/28 12:25
 */
public interface TradeFeeConfigMapper {

	/**
	 * 获取用户的手续费配置
	 * @param uid 可为空
	 * @return
	 */
	@NonNull
	TradeFeeConfig selectFeeConfigByUid(@Nullable @Param("uid") String uid);
}
