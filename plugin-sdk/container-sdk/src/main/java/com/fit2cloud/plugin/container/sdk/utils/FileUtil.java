package com.fit2cloud.plugin.container.sdk.utils;

import com.fit2cloud.plugin.container.sdk.AbstractContainerProvider;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Created by liqiang on 2018/9/11.
 */
public class FileUtil {

    public static String readFileFromClassPath(String path) {
        InputStream inputStream = null;
        try {
            inputStream = AbstractContainerProvider.class.getClassLoader().getResourceAsStream(path);
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("failed to read " + path, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }
}
