package me.sallim.api.domain.crawler.service;

import me.sallim.api.domain.crawler.dto.CrawlingItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrawlingService {
    private final DeviceCrawler deviceCrawler;

    public CrawlingService(DeviceCrawler deviceCrawler) {
        this.deviceCrawler = deviceCrawler;
    }

    public void crawlAll(List<CrawlingItem> items) throws InterruptedException {
        for (CrawlingItem item : items) {
            deviceCrawler.crawl(item);
        }
    }
}