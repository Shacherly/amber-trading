package com.google.backend.trading.model.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author alan
 * @date 2020/4/1 17:51
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "分页结构")
public class PageResult<T> {
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
    @ApiModelProperty("当前页")
    private Integer currentPage = 0;
    @ApiModelProperty("总页数")
    private Integer totalPages = 0;

    /**
     * 生成一个空的page result,所有参数为 0 或 空集
     *
     * @param <T> 泛型类型
     * @return
     */
    public static <T> PageResult<T> generateEmptyResult() {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setCount(0L);
        pageResult.setPageSize(0);
        pageResult.setCurrentPage(0);
        pageResult.setTotalPages(0);
        pageResult.setItems(new ArrayList<>());
        return pageResult;
    }

    /**
     * 生成空数据，但是分页相关其他参数是正常返回
     *
     * @param totalSize
     * @param page
     * @param pageSize
     * @param <T>
     * @return
     */
    public static <T> PageResult<T> generateEmptyResult(long totalSize, int page, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setCount(totalSize);
        result.setPageSize(pageSize);
        result.setCurrentPage(page);
        result.setTotalPages(0);
        result.setItems(Collections.emptyList());
        return result;
    }

    public static <T> PageResult<T> generate(long totalSize, int page, int pageSize, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setCount(totalSize);
        result.setPageSize(pageSize);
        result.setCurrentPage(page);
        result.setItems(list);

        int totalPages = 0;
        if (totalSize != 0L) {
            totalPages = (int)totalSize / pageSize;
            totalPages += totalSize % (long)pageSize != 0L ? 1 : 0;
        }

        result.setTotalPages(totalPages);
        return result;
    }

    public static <T> PageResult<T> generate(PageInfo<T> pageInfo) {
        PageResult<T> result = new PageResult<>();
        result.setCount(pageInfo.getTotal());
        result.setPageSize(pageInfo.getPageSize());
        result.setCurrentPage(pageInfo.getPageNum());
        result.setTotalPages(pageInfo.getPages());
        result.setItems(pageInfo.getList());
        return result;
    }

    public static <T, R> PageResult<R> generate(PageInfo<T> pageInfo, Function<? super T, ? extends R> mapper) {
        PageResult<R> result = new PageResult<>();
        result.setCount(pageInfo.getTotal());
        result.setPageSize(pageInfo.getPageSize());
        result.setCurrentPage(pageInfo.getPageNum());
        result.setTotalPages(pageInfo.getPages());
        result.setItems(pageInfo.getList().stream().map(mapper).collect(Collectors.toList()));
        return result;
    }
}
