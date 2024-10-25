package com.google.backend.trading.dao.mapper;

import com.google.backend.trading.dao.model.TradeUserMarketFavorite;
import org.apache.ibatis.annotations.Param;

/**
 * @author trading
 * @date 2021/10/14 21:15
 */
public interface TradeUserMarketFavoriteMapper {

	int insertIgnoreConflict(TradeUserMarketFavorite userMarketFavorite);

	int addSymbolToFavorite(@Param("uid") String uid, @Param("symbol") String symbol);

	int removeSymbolFromFavorite(@Param("uid") String uid, @Param("symbol") String symbol);
}
