package com.naumen.anticafe.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("page")
@Setter
@Validated
public class PagePropertiesImpl implements PageProperties {
    @Max(value = 50, message = "количество элементов на странице от 1 до 50")
    @Min(value = 1, message = "количество элементов на странице от 1 до 50")
    private Integer page;

    @Override
    public Integer getPageSize() {
        return page;
    }
}
