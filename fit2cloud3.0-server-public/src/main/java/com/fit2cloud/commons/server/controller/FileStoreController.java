package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.FileStore;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.service.FileStoreService;
import com.fit2cloud.commons.utils.MimeTypeUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author: chunxing
 * Date: 2018/8/16  下午2:27
 * Description:
 */
@RestController
public class FileStoreController {

    @Resource
    private FileStoreService fileStoreService;

    @GetMapping("/file/list/{businessKey}")
    public List<FileStore> listFiles(@PathVariable String businessKey) {
        return fileStoreService.listFiles(businessKey);
    }

    @PostMapping("/file/save/{businessKey}")
    public void save(@PathVariable String businessKey, @RequestBody List<MultipartFile> files) throws IOException {
        fileStoreService.save(businessKey, files);
    }

    @GetMapping("/file/delete/id/{id}")
    public void deleteById(@PathVariable String id) {
        fileStoreService.deleteById(id);
    }

    @GetMapping("/file/delete/key/{businessKey}")
    public void deleteByBusinessKey(@PathVariable String businessKey) {
        fileStoreService.deleteByBusinessKey(businessKey);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> downFile(@PathVariable String fileId) {
        FileStore fileStore = fileStoreService.getFileStore(fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileStore.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fileStore.getFile());
    }

    @GetMapping("/anonymous/login/img")
    public ResponseEntity<byte[]> loginImg() {
        FileStore fileStore = fileStoreService.loginImg();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MimeTypeUtils.getMediaType(fileStore.getName(), fileStore.getFile()));
        headers.setLastModified(WebConstants.timestamp);
        headers.setCacheControl(CacheControl.maxAge(1800, TimeUnit.DAYS));
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fileStore.getFile());
    }

    @GetMapping("/anonymous/favicon")
    public ResponseEntity<byte[]> favicon() {
        FileStore fileStore = fileStoreService.favicon();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MimeTypeUtils.getMediaType(fileStore.getName(), fileStore.getFile()));
        headers.setLastModified(WebConstants.timestamp);
        headers.setCacheControl(CacheControl.maxAge(1800, TimeUnit.HOURS));
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fileStore.getFile());
    }

    @GetMapping("/anonymous/logo")
    public ResponseEntity<byte[]> logo() {
        FileStore fileStore = fileStoreService.logo();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MimeTypeUtils.getMediaType(fileStore.getName(), fileStore.getFile()));
        headers.setLastModified(WebConstants.timestamp);
        headers.setCacheControl(CacheControl.maxAge(1800, TimeUnit.HOURS));
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fileStore.getFile());
    }

}
