package me.sallim.api.domain.crawler.parser;

import org.springframework.stereotype.Component;

@Component
public class WasherModelParser implements ModelParser {
    @Override
    public String extractModelName(String deviceName) {
        return deviceName.split(" ")[deviceName.split(" ").length - 1];
    }
}
