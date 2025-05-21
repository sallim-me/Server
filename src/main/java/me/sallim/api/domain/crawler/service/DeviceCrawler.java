package me.sallim.api.domain.crawler.service;

import me.sallim.api.domain.crawler.domain.Device;
import me.sallim.api.domain.crawler.dto.CrawlingItem;
import me.sallim.api.domain.crawler.parser.AirConditionerModelParser;
import me.sallim.api.domain.crawler.parser.FridgeModelParser;
import me.sallim.api.domain.crawler.parser.ModelParser;
import me.sallim.api.domain.crawler.parser.WasherModelParser;
import me.sallim.api.domain.crawler.repository.DeviceRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Profile("crawler")
@Component
public class DeviceCrawler {

    private final ChromeDriver driver;
    private final DeviceRepository deviceRepository;
    private final WasherModelParser washerModelParser;
    private final FridgeModelParser fridgeModelParser;
    private final AirConditionerModelParser airConditionerModelParser;

    public DeviceCrawler(
            ChromeDriver driver,
            DeviceRepository deviceRepository,
            WasherModelParser washerModelParser,
            FridgeModelParser fridgeModelParser,
            AirConditionerModelParser airConditionerModelParser
    ) {
        this.driver = driver;
        this.deviceRepository = deviceRepository;
        this.washerModelParser = washerModelParser;
        this.fridgeModelParser = fridgeModelParser;
        this.airConditionerModelParser = airConditionerModelParser;
    }

    public void crawl(CrawlingItem item) throws InterruptedException {
        driver.get(item.getUrl());
        Thread.sleep(5000);

        String checkboxId = item.getBrand().equals("삼성") ? "searchMakerRep702" : "searchMakerRep2137";
        try {
            WebElement brandCheckbox = driver.findElement(By.id(checkboxId));
            brandCheckbox.click();
            Thread.sleep(3000);
        } catch (NoSuchElementException e) {
            System.out.println("브랜드 체크박스 못 찾음: " + checkboxId);
            return;
        }

        for (int i = 0; i < item.getNumPages(); i++) {
            Thread.sleep(3000);
            crawlSinglePage(item);

            try {
                WebElement nextButton = driver.findElement(By.cssSelector("a.edge_nav.nav_next"));
                nextButton.click();
            } catch (NoSuchElementException e) {
                break;
            }
        }
    }

    private void crawlSinglePage(CrawlingItem item) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement deviceList = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.className("product_list"))
            );
            List<WebElement> devices = deviceList.findElements(By.cssSelector("li.prod_item"));

            for (WebElement d : devices) {
                try {
                    // 광고 아이템은 건너뛰기
                    if (d.getAttribute("class").contains("prod_ad_item")) continue;

                    // 제품명 추출
                    WebElement nameEl = d.findElement(By.cssSelector("div.prod_info > p.prod_name > a"));
                    String deviceName = nameEl.getText().trim();
                    if (deviceName.contains("중고")) continue;

                    // 가격 추출
                    WebElement priceEl = d.findElement(By.cssSelector("p.price_sect > a"));
                    String priceStr = priceEl.getText().replaceAll("[^0-9]", "");
                    int price = Integer.parseInt(priceStr);

                    // 옵션(사양) 정보 추출 (없을 수도 있으므로 try-catch)
                    String optionText = "";
                    try {
                        WebElement specEl = d.findElement(By.cssSelector("div.spec_list"));
                        optionText = specEl.getText().replace("\n", " / ");
                    } catch (NoSuchElementException e) {
                        // 옵션 정보가 없는 경우 무시
                    }

                    // 모델명 파싱
                    String modelName = getModelParser(item.getCategory()).extractModelName(deviceName);

                    // DB 저장
                    Device device = Device.builder()
                            .brand(item.getBrand())
                            .category(item.getCategory())
                            .deviceName(deviceName)
                            .modelName(modelName)
                            .price(price)
                            .date(LocalDate.now().toString())
                            .option(optionText)
                            .build();

                    deviceRepository.save(device);
                } catch (NoSuchElementException e) {
                    // 특정 상품에서 요소 누락된 경우 스킵
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ModelParser getModelParser(String category) {
        if (category.contains("세탁기")) return washerModelParser;
        if (category.contains("냉장고")) return fridgeModelParser;
        if (category.contains("에어컨")) return airConditionerModelParser;
        return washerModelParser; // fallback
    }
}

