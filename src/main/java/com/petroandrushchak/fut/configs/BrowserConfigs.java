package com.petroandrushchak.fut.configs;


import com.petroandrushchak.configs.ConfigConverters;
import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;

import java.time.Duration;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:browser.properties",
        "system:env",
        "system:properties"
})
public interface BrowserConfigs extends Accessible {

    @Key("browser")
    String browser();

    @ConverterClass(ConfigConverters.ImplicitWaitConverter.class)
    @Key("implicit.wait.seconds")
    Duration implicitWait();

    @Key("headless")
    boolean headless();


}
