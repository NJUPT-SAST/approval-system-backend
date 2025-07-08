package com.sast.approval.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sast.approval.annotation.ResponseResult;
import com.sast.approval.enums.ErrorEnum;
import com.sast.approval.response.GlobalResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局结果处理
 */

@RestControllerAdvice(basePackages = "com.sast.approval.controller")
@Slf4j
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper;
    /**
     * 只处理有@ResponseResult注解的接口
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(ResponseResult.class) ||
                returnType.getContainingClass().isAnnotationPresent(ResponseResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null){
            return GlobalResponse.success();
        }
        if(body instanceof GlobalResponse){
            return body;
        }
        if(body instanceof ErrorEnum){
            return GlobalResponse.failure((ErrorEnum) body);
        }
        try {
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return objectMapper.writeValueAsString(GlobalResponse.success(body));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return GlobalResponse.success(body);
    }
}
