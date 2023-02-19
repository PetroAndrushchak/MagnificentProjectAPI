package com.petroandrushchak.aop;

import com.petroandrushchak.fut.pages.webdriver.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CloseBrowserAspect {

    @After("@annotation(CloseBrowser)")
    public void closeBrowser() {
        log.info("Inside Close Browser Aspect");
        WebDriverManager.closeWebDriver();
    }
}
