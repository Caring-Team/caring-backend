package com.caring.caringbackend.domain.auth.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2ProviderProperties {

    private Map<String, ProviderProperties> providers;

    @Getter
    @Setter
    public static class ProviderProperties {
        private String clientId;
        private String clientSecret;
        private String tokenUri;
        private String userInfoUri;
    }
}
