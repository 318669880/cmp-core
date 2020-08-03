package com.fit2cloud.commons.utils;

//import net.sf.jmimemagic.Magic;
//import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liqiang on 2018/10/25.
 */
public class MimeTypeUtils {

    private static ConcurrentHashMap<String, String> extendMineType = new ConcurrentHashMap<>();


    private static final FileNameMap fileNameMap = URLConnection.getFileNameMap();

    static {
        extendMineType.put("css", "text/css");
        extendMineType.put("js", "application/javascript");
        extendMineType.put("ico", "image/x-icon");
        extendMineType.put("woff2", "application/font-woff2");
        extendMineType.put("ttf", "application/x-font-ttf");
    }

    public static String getMediaType(String fileName, byte[] fileContent) {
        String result = null;
        try {
            result = fileNameMap.getContentTypeFor(fileName);
            if (StringUtils.isBlank(result)) {
                String suffix = getExtensionName(fileName);
                if (StringUtils.isNotBlank(suffix)) {
                    result = extendMineType.get(suffix);
                }
            }
            if (StringUtils.isBlank(result) && fileContent != null) {
//                MagicMatch magicMatch = Magic.getMagicMatch(fileContent);
//                result = magicMatch.getMimeType();
            }
        } catch (Exception e) {
            // do nothing
        }
        if (StringUtils.isBlank(result)) {
            result = MediaType.ALL_VALUE;
        }
        return result;
    }

    private static String getExtensionName(String filename) {
        if ((StringUtils.isNotBlank(filename))) {
            return StringUtils.substringAfterLast(filename, ".");
        }
        return filename;
    }

}
