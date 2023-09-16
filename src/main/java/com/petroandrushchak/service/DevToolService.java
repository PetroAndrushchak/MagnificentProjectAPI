package com.petroandrushchak.service;

import com.codeborne.selenide.WebDriverRunner;
import com.petroandrushchak.functional.interfaces.WebAction;
import com.petroandrushchak.helper.JsonParser;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v113.network.Network;
import org.openqa.selenium.devtools.v113.network.model.RequestId;
import org.openqa.selenium.devtools.v113.network.model.RequestWillBeSent;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static java.util.Optional.empty;

@Slf4j
@Service
public class DevToolService {

    @SneakyThrows
    @Synchronized
    public <T> T getResponseFromTheRequestWithAction(String requestPath, WebAction webAction, Class<T> responseClass) {

        Predicate<RequestWillBeSent> predicate = (RequestWillBeSent request)
                -> request.getRequest().getUrl().contains(requestPath) && request.getRequest().getMethod().equals("GET");

        return getResponseFromTheRequestWithAction(predicate, webAction, responseClass);
    }

    @SneakyThrows
    @Synchronized
    public <T> T getResponseFromTheRequestWithAction(Predicate<RequestWillBeSent> predicate, WebAction webAction, Class<T> responseClass) {

        log.info("Getting  response from the Network Interception");
        var devTool = ((ChromeDriver) WebDriverRunner.getWebDriver()).getDevTools();
        devTool.createSessionIfThereIsNotOne();
        devTool.send(Network.enable(empty(), empty(), empty()));
        AtomicReference<T> emailVerifyResponse = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<RequestId> requestID = new AtomicReference<>();
        devTool.addListener(Network.requestWillBeSent(), request -> {
            log.info("Filtering request with path: {}", request.getRequest().getUrl());
            if (predicate.test(request)) {
                log.info("Request with path: {} matching predicate", request.getRequest().getUrl());
                requestID.set(request.getRequestId());
            }
        });

        devTool.addListener(Network.loadingFinished(), loadingFinished -> {
            log.info("Loading for request with id: {} finished", loadingFinished.getRequestId());
            if (requestID.get() == null) {
                log.info("Request ID was not set yet");
                return;
            }
            if (loadingFinished.getRequestId().toString().equals(requestID.get().toString())) {
                log.info("Found desire request id {}", requestID.get());
                String responseBodyValue = devTool.send(Network.getResponseBody(loadingFinished.getRequestId()))
                                                  .getBody();
                emailVerifyResponse.set(JsonParser.parseFromString(responseBodyValue, responseClass));
                latch.countDown();
            }
        });

        webAction.perform();

        boolean result = latch.await(20, TimeUnit.SECONDS);

        devTool.clearListeners();

        if (!result) {
            throw new RuntimeException("Can not find desire response in the network response");
        }

        return emailVerifyResponse.get();
    }
}
