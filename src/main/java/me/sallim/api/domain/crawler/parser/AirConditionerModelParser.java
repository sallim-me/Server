package me.sallim.api.domain.crawler.parser;

import org.springframework.stereotype.Component;

@Component
public class AirConditionerModelParser implements ModelParser {
    @Override
    public String extractModelName(String deviceName) {
        String[] words = deviceName.split(" ");
        String last = words[words.length - 1];
        if (last.contains("거치형") || last.contains("매립형")) {
            return words[words.length - 2];
        }
        return last;
    }
}