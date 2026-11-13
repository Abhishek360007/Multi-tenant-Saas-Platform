package com.abhishek.tenanthub.common.util;

import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


/**
 * <p>
 * Excel Utility class
 * </p>
 *
 * @author WangChenChen
 * @version 1.2
 * @date 2023/11/23 14:50
 */

@Slf4j
public class WrapExcelUtils {

    /**
     * Default page size
     */
    private static final int PAGE_SIZE = 10000;

    /**
     * Maximum page size
     */
    private static final int MAX_PAGE_SIZE = 60000;

    /**
     * Generic paged export to Excel, 10k rows per page by default
     *
     * @param fileName File name
     * @param dataList Data list
     * @return ByteArrayOutputStream
     */
    public static <T> ByteArrayOutputStream genPageWrite(List<T> dataList) {
        return genPageWrite(PAGE_SIZE, dataList);
    }


    /**
     * Generic paged export to Excel, 10k rows per page by default
     *
     * @param fileName File name
     * @param dataList Data list
     * @return ResponseEntity<ByteArrayResource>
     */
    public static <T> ResponseEntity<ByteArrayResource> genPageWrite(String fileName, List<T> dataList) {
        return genPageWrite(fileName, PAGE_SIZE, dataList);
    }


    /**
     * Generic paged export to Excel
     *
     * @param fileName File name
     * @param pageSize Rows per page; each Excel sheet holds at most 60k rows
     * @param dataList Data list
     * @return ResponseEntity<ByteArrayResource>
     */
    public static <T> ResponseEntity<ByteArrayResource> genPageWrite(String fileName, Integer pageSize, List<T> dataList) {
        if (StrUtil.isEmpty(fileName)) {
            fileName = RandomUtil.randomString(12);
        } else {
            if (fileName.endsWith(".xlsx")) {
                fileName = fileName.replaceFirst(".xlsx", "");
            }
        }
        var os = genPageWrite(pageSize, dataList);
        var headerValue = String.format("attachment;filename=%s.xlsx", fileNameEncode(fileName));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(new ByteArrayResource(os.toByteArray()));
    }


    /**
     * Generic paged export to Excel
     *
     * @param fileName File name
     * @param pageSize Rows per page; each Excel sheet holds at most 60k rows
     * @param dataList Data list
     * @return ByteArrayOutputStream
     */
    public static <T> ByteArrayOutputStream genPageWrite(Integer pageSize, List<T> dataList) {
        if (pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        var bigWriter = ExcelUtil.getBigWriter();
        if (!dataList.isEmpty()) {
            // Get the field alias
            Map<String, String> fieldSchemaDescription = getFieldSchemaDescription(dataList.getFirst());
            bigWriter.setOnlyAlias(true).setHeaderAlias(fieldSchemaDescription);
            // Get the total number of devices
            int total = dataList.size();
            if (total > pageSize) {
                // Remove the first Sheet
                bigWriter.getWorkbook().removeSheetAt(0);
                var pageNum = new AtomicInteger(1);
                // Paged write
                toPageIndexRange(total, pageSize, (fromIndex, toIndex) -> {
                    var rangeData = dataList.subList(fromIndex, toIndex);
                    var sheetName = String.format("Page %d", pageNum.get());
                    bigWriter.setSheet(sheetName).write(rangeData);
                    pageNum.getAndIncrement();
                });
            } else {
                bigWriter.write(dataList);
            }
        }
        var result = new ByteArrayOutputStream();
        bigWriter.flush(result, true);
        return result;
    }


    /**
     * Compute page ranges
     * Compute the start and end index of each page from the total and the page size
     * Example: 300 rows in total, 100 per page.
     * Page 1: 0 to 99, page 2: 100 to 199, page 3: 200 to 299, and so on
     */
    private static void toPageIndexRange(int total, int pageSize, BiConsumer<Integer, Integer> consumer) {
        // Total pages
        int totalPage = PageUtil.totalPage(total, pageSize);
        // Start index
        var startIndex = new AtomicInteger(0);
        for (int i = 0; i < totalPage; i++) {
            int fromIndex = startIndex.get();
            var toIndex = (fromIndex + pageSize);
            startIndex.set(toIndex);
            if (toIndex >= total) {
                toIndex = total;
            }
            consumer.accept(fromIndex, toIndex);
        }
    }


    /**
     * Read by reflection from the @Schema annotation on the field description
     * <p>
     * key : Property name
     * value :  cn.hutool.core.annotation.Alias.value
     * or io.swagger.v3.oas.annotations.media.Schema.description
     *
     * @return Map<String, String>
     */
    private static Map<String, String> getFieldSchemaDescription(Object obj) {
        return Arrays.stream(ReflectUtil.getFields(obj.getClass()))
                .collect(Collectors.toMap(Field::getName, field -> {
                    var ann1 = field.getAnnotation(cn.hutool.core.annotation.Alias.class);
                    if (ann1 != null) {
                        return ann1.value();
                    }
                    var ann2 = field.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
                    if (ann2 != null) {
                        return ann2.description();
                    }
                    return field.getName();
                }, (k1, k2) -> k1, LinkedHashMap::new));
    }


    /**
     * File name encoding
     */
    private static String fileNameEncode(String fileName) {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

}
