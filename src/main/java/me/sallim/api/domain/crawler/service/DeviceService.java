package me.sallim.api.domain.crawler.service;

import me.sallim.api.domain.crawler.dto.CrawlingItem;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("crawler")
@Service
public class DeviceService {
    private final DeviceCrawler deviceCrawler;

    public DeviceService(DeviceCrawler deviceCrawler) {
        this.deviceCrawler = deviceCrawler;
    }

    public void crawlAll(List<CrawlingItem> items) throws InterruptedException {
        for (CrawlingItem item : items) {
            deviceCrawler.crawl(item);
        }
    }
}