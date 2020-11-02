package org.efan.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/***
 *　　　　　　__
 *  　___  / _|  __ _  _ __
 * 　/ _ \| |_  / _` || '_ \
 *　|  __/|  _|| (_| || | | |
 * 　\___||_|   \__,_||_| |_|
 *
 * @author fan
 * @date 2020-3-23 11:42
 * @description: TODO : 描述一下吧~~
 */
public class EfanHttpMessageConverter  extends AbstractHttpMessageConverter {
    public EfanHttpMessageConverter() {
        super(MediaType.ALL);
    }

    @Override
    protected boolean supports(Class clazz) {
        return true;
    }

    /**
     * 定义请求类型必须为 application/eway;charset=utf-8
     */
    private static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("application", "eway", StandardCharsets.UTF_8);
    private MediaType requestMediaType;

    private static final int DELAY = 1;
    private String key;
    private String iv;
    @Override
    public boolean canRead(Class clazz, MediaType mediaType) {
        System.out.println("canRead");
        requestMediaType=mediaType;
//        return DEFAULT_MEDIA_TYPE.equals(requestMediaType);
        return true;
    }

    /**
     * 请求加密
     * @param clazz
     * @param inputMessage
     * @return
     * @throws IOException
     * @throws HttpMessageNotReadableException
     */
    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
return null;
    }

    /**
     * 返回加密
     * @param o
     * @param outputMessage
     * @throws IOException
     * @throws HttpMessageNotWritableException
     */
    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
//        String result = JSON.toJSONString(o);
//        if (!DEFAULT_MEDIA_TYPE.equals(requestMediaType)) {
//            //请求ContentType 不匹配
//            log.error("ContentType 不匹配 request ContentType:{}", null!=requestMediaType?requestMediaType.toString():"");
//            o = WrapperResponse.returnVaule(TYPE_ERROR);
//            result = JSON.toJSONString(o);
//        }
//        ServletServerHttpResponse s=(ServletServerHttpResponse)outputMessage;
//        if (OK_200 == s.getServletResponse().getStatus()) {
//            //加密
//            try {
//                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(iv)) {
//                    result = AESUtils.encryptAES(result, key, iv);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        outputMessage.getHeaders().setContentType(DEFAULT_MEDIA_TYPE);
//        outputMessage.getBody().write(result.getBytes());
    }
}
