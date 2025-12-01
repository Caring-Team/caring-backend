package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.annotation.OAuth2Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.core.util.AnnotationsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2ServiceFactory {

    private final Map<String, OAuth2Service> oAuth2ServiceMap = new HashMap<>();

    public OAuth2ServiceFactory(List<OAuth2Service> services) {
        for (OAuth2Service service : services) {
            Class<?> targetClass = AopUtils.getTargetClass(service);
            OAuth2Provider annotation = targetClass.getAnnotation(OAuth2Provider.class);
            if (annotation != null) {
                String key = annotation.value().toLowerCase();
                log.info("OAuth2Service 등록 Provider : {} -> {}", key, targetClass.getSimpleName());
                oAuth2ServiceMap.put(annotation.value().toLowerCase(), service);
            }
        }
    }

    public OAuth2Service getService(String provider) {
        OAuth2Service service = oAuth2ServiceMap.get(provider.toLowerCase());
        if (service == null) {
            log.error("unknown provider {}", provider);
            throw new IllegalArgumentException("Unknown OAuth2 provider: " + provider);
        }
        return service;
    }

}
