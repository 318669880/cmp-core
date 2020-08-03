package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.model.ExcelExportRequest;
import com.fit2cloud.commons.utils.ExcelExportUtils;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChartTableExportController {

    @PostMapping("chart/table/export")
    public ResponseEntity<byte[]> export(@RequestBody ExcelExportRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "导出");
        byte[] bytes;
        List<ExcelExportRequest.Row> rows = request.getData();
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        List<ExcelExportRequest.Column> columns = request.getColumns();
        if (CollectionUtils.isEmpty(columns)) {
            return null;
        }

        List<List<Object>> data = rows.stream().map(row -> new ArrayList<Object>() {{
            columns.forEach(column -> {
                try {
                    Object value = FieldUtils.getDeclaredField(row.getClass(), column.getKey(), true).get(row);
                    add(value);
                } catch (IllegalAccessException e) {
                    LogUtil.error("Export chart table excel error: ", ExceptionUtils.getStackTrace(e));
                }
            });
        }}).collect(Collectors.toList());

        bytes = ExcelExportUtils.exportExcelData("资源详情", request.getColumns().stream().map(ExcelExportRequest.Column::getValue).collect(Collectors.toList()), data);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(bytes);
    }

}
