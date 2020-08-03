package com.fit2cloud.commons.server.config;

import com.fit2cloud.commons.utils.EncryptConfig;

import java.util.ArrayList;
import java.util.List;

public interface DBEncryptConfig {
    default List<EncryptConfig> encryptConfig() {
        return new ArrayList<>();
    }
}
