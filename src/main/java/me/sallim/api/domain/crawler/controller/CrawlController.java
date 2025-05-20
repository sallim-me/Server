package me.sallim.api.domain.crawler.controller;

import me.sallim.api.domain.crawler.dto.CrawlingItem;
import me.sallim.api.domain.crawler.service.CrawlingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crawler")
public class CrawlController {

    private final CrawlingService crawlingService;

    public CrawlController(CrawlingService crawlingService) {
        this.crawlingService = crawlingService;
    }

    @PostMapping("/run")
    public String runCrawling() throws InterruptedException {
        List<CrawlingItem> items = List.of(
                new CrawlingItem("삼성", "세탁기", "https://prod.danawa.com/list/?cate=10251634", 3),
                new CrawlingItem("LG", "세탁기", "https://prod.danawa.com/list/?cate=10251634", 4),
                new CrawlingItem("삼성", "냉장고", "https://prod.danawa.com/list/?cate=102110", 49),
                new CrawlingItem("LG", "냉장고", "https://prod.danawa.com/list/?cate=102110", 41),
                new CrawlingItem("삼성", "에어컨", "https://prod.danawa.com/list/?cate=1022644", 46),
                new CrawlingItem("LG", "에어컨", "https://prod.danawa.com/list/?cate=1022644", 35)
        );

        crawlingService.crawlAll(items);
        return "크롤링 완료";
    }
}
