package com.google.backend.trading.migrate;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "watch_list")
public class WatchList {

    String uid;

    List<String> watchCoinList;

    /**
     * create time(timestamp)
     */
    @Field(name = "created_time")
    private Long createdTime;
    /**
     * update time(timestamp)
     */
    @Field(name = "updated_time")
    private Long updatedTime;
    /**
     * create time(date)
     */
    private Date ctime;
    /**
     * update time(date)
     */
    private Date utime;

}