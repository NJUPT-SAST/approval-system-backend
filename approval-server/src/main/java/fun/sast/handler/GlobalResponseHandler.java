package fun.sast.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.sast.annotation.ResponseResult;
import fun.sast.response.GlobalResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/** 全局结果处理 */
@RestControllerAdvice(basePackages = "fun.sast.controller")
@Slf4j
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Resource private ObjectMapper objectMapper;

    /**
     * 只处理有@ResponseResult注解的接口
     *
     * @param returnType 返回数据类型
     * @param converterType 转换器类型
     * @return boolean true表示处理，false表示不处理
     */
    @Override
    public boolean supports(
            MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(ResponseResult.class)
                || returnType.getContainingClass().isAnnotationPresent(ResponseResult.class);
    }

    /**
     * 处理接口返回数据
     *
     * @param body 接口返回数据
     * @param returnType 返回数据类型
     * @param selectedContentType 响应数据类型
     * @param selectedConverterType 响应数据转换器
     * @param request 请求
     * @param response 响应
     * @return object 处理后的数据
     */
    @Override
    public Object beforeBodyWrite(
            Object body,
            @NotNull MethodParameter returnType,
            @NotNull MediaType selectedContentType,
            @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response) {
        if (body == null) {
            return GlobalResponse.success();
        }
        else if (body instanceof GlobalResponse) {
            return body;
        }
        return GlobalResponse.success(body);
    }
}
