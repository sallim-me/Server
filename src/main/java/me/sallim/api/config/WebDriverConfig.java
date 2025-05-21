package me.sallim.api.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Profile;

@Profile("crawler")
@Configuration
public class WebDriverConfig {

    @Bean
    public ChromeDriver chromeDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
    }
}
