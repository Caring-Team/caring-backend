package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.annotation.OAuth2Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2ServiceFactory {

    private final Map<String, OAuth2Service> oAuth2ServiceMap = new HashMap<>();

    public OAuth2ServiceFactory(List<OAuth2Service> services) {
        for (OAuth2Service service : services) {
            OAuth2Provider annotation = service.getClass().getAnnotation(OAuth2Provider.class);
            if (annotation != null) {
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
