package me.sallim.api.domain.crawler.parser;

import org.springframework.stereotype.Component;

@Component
public class FridgeModelParser implements ModelParser {
    @Override
    public String extractModelName(String deviceName) {
        String clean = deviceName.replaceAll("\\(.*\\)", "").trim();
        if (clean.contains("+")) {
            return clean.split("\\+")[0].trim().split(" ")[clean.split("\\+")[0].trim().split(" ").length - 1];
        }
        return clean.split(" ")[clean.split(" ").length - 1];
    }
}