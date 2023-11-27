package com.petroandrushchak.futbin.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.futbin.models.search.page.Version;
import com.petroandrushchak.helper.Waiter;
import jakarta.validation.constraints.NotNull;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@Component
public class SearchFilterComponent {

    SelenideElement container = $(".filters-holder .menu_bar");

    SelenideElement versionDropdownButton = container.$("li[class *='list-inline-item versions']");

    SelenideElement priceFilterLabel = container.$("li:has(img[class *='coins_icon_web_filters'])");
    SelenideElement priceFilterMinPriceInput = priceFilterLabel.$("input[id = 'MinPS_Price']");
    SelenideElement priceFilterMaxPriceInput = priceFilterLabel.$("input[id = 'MaxPS_Price']");
    SelenideElement priceFilterFilterByPriceButton = priceFilterLabel.$("button[data-id = 'PS_Price']");

    @RealPerson
    public void setPriceRange(long min, long max) {
        priceFilterLabel.click();
        priceFilterMinPriceInput.shouldBe(Condition.visible);
        enterMinPrice(min);
        enterMaxPrice(max);
        clickFilterByPriceButton();
        Waiter.waitFor(Duration.ofSeconds(3));
    }

    @RealPerson
    public SearchFilterComponent enterMinPrice(long min) {
        priceFilterMinPriceInput.sendKeys(String.valueOf(min));
        return this;
    }

    @RealPerson
    public SearchFilterComponent enterMaxPrice(long max) {
        priceFilterMaxPriceInput.sendKeys(String.valueOf(max));
        return this;
    }

    @RealPerson
    public SearchFilterComponent clickFilterByPriceButton() {
        priceFilterFilterByPriceButton.click();
        return this;
    }

    @RealPerson
    public void selectVersion(@NotNull Version version) {
        if (version == Version.ALL_GOLD) {
            selectAllGoldFilterOption();
        } else {
            throw new IllegalArgumentException("Version " + version + " is not supported");
        }
    }

    @RealPerson
    public void selectAllGoldFilterOption() {
        Waiter.waitRandomTimeFromTwoToThreeMinutes();
        versionDropdownButton.click();
        Waiter.waitRandomTimeFromTwoToThreeMinutes();
        var goldFilterOption = $x("//*[text()=' Gold']/..");
        Actions actions = new Actions(WebDriverRunner.getWebDriver());
        actions.moveToElement(goldFilterOption).perform();
        Waiter.waitRandomTimeFromTwoToThreeMinutes();
        goldFilterOption.$("a[data-value = 'gold']").click();
    }

}
