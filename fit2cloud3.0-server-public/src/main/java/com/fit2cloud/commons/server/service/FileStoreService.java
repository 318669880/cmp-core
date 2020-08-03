package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.FileStore;
import com.fit2cloud.commons.server.base.domain.FileStoreExample;
import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.mapper.FileStoreMapper;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtFileStoreCommonMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/8/16  下午2:20
 * Description:
 */
@Service
public class FileStoreService {

    @Resource
    private FileStoreMapper fileStoreMapper;
    @Resource
    private ExtFileStoreCommonMapper extFileStoreCommonMapper;
    @Resource
    private SystemParameterMapper systemParameterMapper;


    public FileStore getFileStore(String id) {
        return fileStoreMapper.selectByPrimaryKey(id);
    }

    public FileStore favicon() {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(ParamConstants.UI.FAVICON.getValue());
        return fileStoreMapper.selectByPrimaryKey(systemParameter.getParamValue());
    }

    public FileStore logo() {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(ParamConstants.UI.LOGO.getValue());
        return fileStoreMapper.selectByPrimaryKey(systemParameter.getParamValue());
    }

    public FileStore loginImg() {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(ParamConstants.UI.LOGIN_IMG.getValue());
        return fileStoreMapper.selectByPrimaryKey(systemParameter.getParamValue());
    }

    public boolean isFileExists(String id) {
        FileStoreExample fileStoreExample = new FileStoreExample();
        fileStoreExample.createCriteria().andIdEqualTo(id);
        return fileStoreMapper.countByExample(fileStoreExample) > 0;
    }

    public void save(String businessKey, List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            FileStore fileStore = new FileStore();
            fileStore.setId(UUIDUtil.newUUID());
            fileStore.setBusinessKey(businessKey);
            fileStore.setName(file.getOriginalFilename());
            fileStore.setFile(file.getBytes());
            fileStore.setSize(file.getSize());
            fileStore.setCreateTime(Instant.now().toEpochMilli());
            fileStoreMapper.insert(fileStore);
        }
    }

    public void deleteById(String id) {
        fileStoreMapper.deleteByPrimaryKey(id);
    }

    public void deleteByBusinessKey(String businessKey) {
        FileStoreExample fileStoreExample = new FileStoreExample();
        fileStoreExample.createCriteria().andBusinessKeyEqualTo(businessKey);
        fileStoreMapper.deleteByExample(fileStoreExample);
    }

    public List<FileStore> listFiles(String businessKey) {
        return extFileStoreCommonMapper.listFiles(businessKey);
    }
}
