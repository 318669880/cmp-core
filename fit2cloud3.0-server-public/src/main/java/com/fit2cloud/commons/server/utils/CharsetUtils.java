package com.fit2cloud.commons.server.utils;


import com.fit2cloud.commons.utils.LogUtil;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.Optional;


/**
 * 获取字节或者流的编码方式
 */
@Component
public class CharsetUtils {

    /**
     *
     * @param bytes
     * @return the name of charset
     */
    public String getCharsetName(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)) return null;
        CharsetDetector charsetDetector = new CharsetDetector();
        charsetDetector.setText(bytes);
        CharsetMatch charsetMatch = charsetDetector.detect();
        if (Optional.ofNullable(charsetMatch).isPresent()){
            return charsetMatch.getName();
        }
        return null;
    }

    public String getCharsetName(InputStream inputStream){
        if(ObjectUtils.isEmpty(inputStream)) return null;
        CharsetDetector charsetDetector = new CharsetDetector();
        try{
            charsetDetector.setText(inputStream);
        }catch (Exception e){
            //e.printStackTrace();
            LogUtil.error("get the charsetName of inputstream error",e);
        }

        CharsetMatch charsetMatch = charsetDetector.detect();
        if (Optional.ofNullable(charsetMatch).isPresent()){
            return charsetMatch.getName();
        }
        return null;
    }



}
