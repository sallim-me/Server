package me.sallim.api.domain.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrawlingItem {
    private String brand;
    private String category;
    private String url;
    private int numPages;
}