package com.google.backend.trading.util;

/**
 * @author adam.wang
 * @date 2021/11/15 20:00
 */

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

/**
 * @author adam.wang
 * @date 2021/11/15 11:28
 * 设置列宽
 */
public class CustomCellWriteHandler extends AbstractColumnWidthStyleStrategy {

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> cellDataList, Cell cell, Head head, Integer integer, Boolean isHead) {
        boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
        if (needSetWidth) {
            writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 22 * 256);
        }
    }


}
