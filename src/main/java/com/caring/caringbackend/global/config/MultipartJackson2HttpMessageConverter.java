package com.caring.caringbackend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import java.lang.reflect.Type;

/**
 * Multipart/form-data 요청에서 @RequestPart로 JSON 객체를 받을 수 있도록 하는 커스텀 HttpMessageConverter
 *
 * Swagger UI에서 @RequestPart로 DTO를 전송할 때 발생하는
 * "Content-Type 'application/octet-stream' is not supported" 에러를 해결합니다.
 */
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}

