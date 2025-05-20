package me.sallim.api.domain.crawler.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromeDriverConfig {

    @Bean
    public ChromeDriver chromeDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
    }
}
