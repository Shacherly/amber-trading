package com.google.backend.trading.model.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author alan
 * @date 2020/4/1 17:51
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "分页结构")
public class PageResultWithPage<T> {
    /**
     * 总数据量
     */
    @ApiModelProperty(value = "总数据量")
    private long count = 0L;
    /**
     * 页大小
     */
    @ApiModelProperty(name="page_size",value = "页大小")
    private Integer pageSize = 0;
    /**
     * 列表
     */
    @ApiModelProperty(value = "数据")
    private List<T> items;

    @ApiModelProperty(value = "总页数")
    private Integer totalPages;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "当前页（OPENAPI V1版本）")
    private Integer currentPage;

    public static <T> PageResultWithPage<T> generate(long totalSize, int page, int pageSize, List<T> list) {
        PageResultWithPage<T> result = new PageResultWithPage<>();
        result.setCount(totalSize);
        result.setPage(page);
        result.setCurrentPage(page);
        result.setPageSize(pageSize);
        result.setItems(list);
        int totalPages = 0;
        if (totalSize != 0) {
            totalPages = (int) totalSize / pageSize;
            totalPages += (totalSize % pageSize != 0 ? 1 : 0);
        }
        result.setTotalPages(totalPages);
        return result;
    }
}
