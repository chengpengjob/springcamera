package com.cp.springcamera.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author ChengPeng
 * @create 2020-03-26 15:39
 */
public class DataUtil {
    /**
     * String类型的时间(yyyy-MM-dd HH:mm:ss)转换成IOS-8601格式
     * @param data
     * @return
     */
    public String Stringios(String data) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dt1 = LocalDateTime.parse(data, dateTimeFormatter);
        ZoneOffset offset = ZoneOffset.of("+08:00");
        OffsetDateTime date = OffsetDateTime.of(dt1, offset);

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String datas = date.format(formatter2);

        return datas;

    }
}
