package com.petroandrushchak.service;

import com.codeborne.selenide.WebDriverRunner;
import com.petroandrushchak.functional.interfaces.WebAction;
import com.petroandrushchak.helper.JsonParser;
import com.petroandrushchak.model.api.RequestResponseWrapper;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v113.network.Network;
import org.openqa.selenium.devtools.v113.network.model.Request;
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
                -> request.getRequest().getUrl().contains(requestPath) && request.getRequest().getMethod().equalsIgnoreCase("GET");

        return getResponseFromTheRequestWithAction(predicate, webAction, responseClass);
    }

    @SneakyThrows
    @Synchronized
    public RequestResponseWrapper getRequestResponseFromTheRequestWithAction(String requestPath, RequestMethod requestMethod, WebAction webAction) {

        Predicate<RequestWillBeSent> predicate = (RequestWillBeSent request)
                -> request.getRequest().getUrl().contains(requestPath) && request.getRequest().getMethod().equalsIgnoreCase(requestMethod.getName());

        return getRequestResponseFromTheRequestWithAction(predicate, webAction);
    }

    @SneakyThrows
    @Synchronized
    public <T> T getResponseFromTheRequestWithAction(Predicate<RequestWillBeSent> predicate, WebAction webAction, Class<T> responseClass) {

        log.debug("Getting  parsedResponse from the Network Interception");
        var devTool = ((ChromeDriver) WebDriverRunner.getWebDriver()).getDevTools();
        devTool.createSessionIfThereIsNotOne();
        devTool.send(Network.enable(empty(), empty(), empty()));
        AtomicReference<T> parsedResponse = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<RequestId> requestID = new AtomicReference<>();
        devTool.addListener(Network.requestWillBeSent(), request -> {
            log.debug("Filtering request with path: {}", request.getRequest().getUrl());
            if (predicate.test(request)) {
                log.debug("Request with path: {} matching predicate", request.getRequest().getUrl());
                requestID.set(request.getRequestId());
            }
        });

        devTool.addListener(Network.loadingFinished(), loadingFinished -> {
            log.debug("Loading for request with id: {} finished", loadingFinished.getRequestId());
            if (requestID.get() == null) {
                log.info("Request ID was not set yet");
                return;
            }
            if (loadingFinished.getRequestId().toString().equals(requestID.get().toString())) {
                log.debug("Found desire request id {}", requestID.get());
                String responseBodyValue = devTool.send(Network.getResponseBody(loadingFinished.getRequestId()))
                                                  .getBody();
                parsedResponse.set(JsonParser.parseFromString(responseBodyValue, responseClass));
                latch.countDown();
            }
        });

        webAction.perform();

        boolean result = latch.await(20, TimeUnit.SECONDS);

        devTool.clearListeners();

        if (!result) {
            throw new RuntimeException("Can not find desire parsedResponse in the network parsedResponse");
        }

        return parsedResponse.get();
    }

    @SneakyThrows
    @Synchronized
    private RequestResponseWrapper getRequestResponseFromTheRequestWithAction(Predicate<RequestWillBeSent> predicate, WebAction webAction) {

        log.debug("Getting  parsedResponse from the Network Interception");
        var devTool = ((ChromeDriver) WebDriverRunner.getWebDriver()).getDevTools();
        devTool.createSessionIfThereIsNotOne();
        devTool.send(Network.enable(empty(), empty(), empty()));
        AtomicReference<RequestResponseWrapper> requestResponseWrapper = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<RequestId> requestID = new AtomicReference<>();
        AtomicReference<Request> requestSent = new AtomicReference<>();
        devTool.addListener(Network.requestWillBeSent(), request -> {
            log.debug("Filtering request with path: {}", request.getRequest().getUrl());
            if (predicate.test(request)) {
                log.debug("Request with path: {} matching predicate", request.getRequest().getUrl());
                requestID.set(request.getRequestId());
                if (request.getRequest().getHeaders() == null) {
                    log.error("!!!!!!!! ---------  Request headers are null --------- !!!!!!!!");
                    throw new RuntimeException("Request headers are null");
                }
                requestSent.set(request.getRequest());
            }
        });

        devTool.addListener(Network.responseReceivedExtraInfo(), responseReceivedExtraInfo -> {
            log.info("Response received extra info for request with id: {}", responseReceivedExtraInfo.getRequestId());
            if (requestID.get() == null) {
                log.info("Request ID was not set yet");
                return;
            }

            if (responseReceivedExtraInfo.getRequestId().toString().equals(requestID.get().toString())) {
                log.debug("Found desire request id {}", requestID.get());
                RequestResponseWrapper wrapper = new RequestResponseWrapper();
                wrapper.setRequest(requestSent.get());
                wrapper.setResponse(responseReceivedExtraInfo);
                requestResponseWrapper.set(wrapper);
                latch.countDown();
            }
        });

        webAction.perform();

        boolean result = latch.await(20, TimeUnit.SECONDS);

        devTool.clearListeners();

        if (!result) {
            throw new RuntimeException("Can not find desire parsedResponse in the network parsedResponse");
        }

        return requestResponseWrapper.get();
    }

    public enum RequestMethod {
        GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

        String name;

        RequestMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
