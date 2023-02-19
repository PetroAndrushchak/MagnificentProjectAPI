package com.petroandrushchak.controler;

import com.petroandrushchak.exceptions.FutEaAccountNotFound;
import com.petroandrushchak.service.FutAccountService;
import com.petroandrushchak.view.FutEaAccountView;
import com.petroandrushchak.view.request.SnippingRequestBody;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class FutEAAccountController {

    @Autowired
    FutAccountService futAccountService;

    @CrossOrigin(origins = "*")
    @GetMapping("/futAccounts")
    public List<FutEaAccountView> futAccounts() {

        return futAccountService.findAllFutAccounts();
//        var futEaAccountView = FutEaAccountView.anFutEaAccount()
//                                               .withEaEmailEmail("test")
//                                               .withEaEmailPassword("test")
//                                               .withEaLogin("test")
//                                               .withEaPassword("test")
//                                               .withId(1L)
//                                               .withUsername("test")
//                                               .build();
//        return List.of(futEaAccountView);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/startSnipping")
    public void startSnipping(@Valid @RequestBody SnippingRequestBody snippingRequestBody) {

        // logic to start snipping
        log.info("Start snipping");
        log.info(snippingRequestBody.toString());

        if (snippingRequestBody.getFutEaAccountId().equals("10")) {
            throw new FutEaAccountNotFound(snippingRequestBody.getFutEaAccountId());
        }
    }

}
