package com.petroandrushchak.fut.steps;

import com.petroandrushchak.fut.mapper.BasicMapper;
import com.petroandrushchak.fut.pages.dto.CookieDto;
import com.petroandrushchak.fut.pages.helper.BrowserHelper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BrowserSteps {

    public void reloadPage() {
        BrowserHelper.reloadThePage();
    }

    public void saveCookiesInBrowser(List<CookieDto> cookieDto) {
        log.info("Saving cookies in browser");
        List<Cookie> cookies = cookieDto.stream()
                                         .map(BasicMapper.INSTANCE::convert)
                                         .toList();
        BrowserHelper.deleteAllCookiesFromBrowser();
        BrowserHelper.saveCookiesInBrowser(cookies);
    }

    public List<CookieDto> readCookiesFromBrowser() {
        log.info("Reading cookies from browser");
        return BrowserHelper.readAllCookiesFromBrowser().stream()
                            .map(BasicMapper.INSTANCE::convert)
                            .collect(Collectors.toList());
    }


}
