package com.seti.ms.franchises.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "microservice")
public class ExceptionConfigs {

    private Map<String, String> exception;

    public static final String PERZONALIZADA = "perzonalizada";
    public static final String SYSYEM = "system";

    public String getException(String key) {
        return exception.getOrDefault(key, "Error desconocido");
    }
}
