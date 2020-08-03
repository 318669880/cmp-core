package com.fit2cloud.mc.dto;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author: chunxing
 * Date: 2018/8/15  下午3:41
 * Description:
 */
public class SystemParameterDTO extends SystemParameter {

    @ApiModelProperty("文件")
    private MultipartFile file;
    @ApiModelProperty("文件名称")
    private String fileName;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
