package com.fit2cloud.commons.server.handle;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.ResultHolder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class RestTemplateExceptionHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        ResultHolder resultHolder = getResultHolder(response);
        if (resultHolder != null && !resultHolder.isSuccess()) {
            F2CException.throwException(resultHolder.getMessage());
        }
        super.handleError(response);
    }


    private ResultHolder getResultHolder(ClientHttpResponse response) {
        try {
            return JSON.parseObject(IOUtils.toString(response.getBody()), ResultHolder.class);
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }
}
