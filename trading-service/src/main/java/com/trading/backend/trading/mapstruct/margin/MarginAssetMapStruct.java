package com.google.backend.trading.mapstruct.margin;

import com.google.backend.trading.model.margin.api.MarginAssetInfoRes;
import com.google.backend.trading.model.margin.dto.MarginInfo;
import org.mapstruct.Mapper;

/**
 * @author adam.wang
 * @date 2021/10/19 13:52
 */
@Mapper(componentModel="spring")
public interface MarginAssetMapStruct {

    /**
     * marginInfo2MarginAssetInfoRes
     * @param marginInfo
     * @return
     */
    MarginAssetInfoRes marginInfo2MarginAssetInfoRes(MarginInfo marginInfo);
}
