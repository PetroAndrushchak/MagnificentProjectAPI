package com.petroandrushchak.model.api;

import lombok.Data;
import org.openqa.selenium.devtools.v113.network.model.Request;
import org.openqa.selenium.devtools.v113.network.model.ResponseReceivedExtraInfo;

@Data
public class RequestResponseWrapper {

    Request request;
    ResponseReceivedExtraInfo response;

}
