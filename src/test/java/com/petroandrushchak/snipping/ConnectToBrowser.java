package com.petroandrushchak.snipping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ConnectToBrowser {

    public static void main(String[] args) {

        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "/Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/src/main/resources/chromedriver");

        // Set up ChromeOptions to specify the remote debugging URL
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-debugging-port=9223");
//        options.addArguments("user-data-dir=/Users/pandrushchak.appwell/Library/Application Support/Google/Chrome/Profile 1");

       // options.addArguments("--remote-allow-origins", "http://localhost:9323");
        options.setExperimentalOption("debuggerAddress", "localhost:9323");

        // Attach to the existing Chrome session
        WebDriver driver = new ChromeDriver(options);

        // Perform Selenium actions on the existing browser window
        driver.get("https://stackoverflow.com/questions/75674124/can-i-set-remote-allow-origins-without-using-a-wildcard-in-selenium");

    }
}
