package com.petroandrushchak.controler;

import com.petroandrushchak.entity.Status;
import com.petroandrushchak.exceptions.WarningException;
import com.petroandrushchak.mapper.ui.api.PlayerItemMapper;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.process.BrowserProcessHelper;
import com.petroandrushchak.service.BrowserProcessService;
import com.petroandrushchak.service.FutAccountService;
import com.petroandrushchak.steps.SnippingSteps;
import com.petroandrushchak.steps.SnippingValidationsSteps;
import com.petroandrushchak.view.BrowserProcessView;
import com.petroandrushchak.view.FutEaAccountView;
import com.petroandrushchak.view.SnippingView;
import com.petroandrushchak.view.request.SnippingRequestBody;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class FutEAAccountController {

    @Autowired FutAccountService futAccountService;
    @Autowired BrowserProcessService browserProcessService;

    @Autowired SnippingValidationsSteps snippingValidationsSteps;
    @Autowired SnippingSteps snippingSteps;

    @Autowired BrowserProcessHelper browserProcessHelper;

    @CrossOrigin(origins = "*")
    @GetMapping("/futAccounts")
    public List<FutEaAccountView> futAccounts() {
        return futAccountService.findAllFutAccounts();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/startSnipping")
    public Item startSnipping(@Valid @RequestBody SnippingRequestBody snippingRequestBody) {

        var futAccountView = snippingValidationsSteps.validateSnippingRequestFutAccount(snippingRequestBody);
        var item = snippingValidationsSteps.validateSnippingRequestItem(snippingRequestBody);

      //  BrowserProcessView browserProcessView = snippingSteps.startSnipping(futAccountView, item);

        return item;
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/cancelSnipping/{snippingId}")
    public void cancelSnipping(@PathVariable Long snippingId) {

        var browserProcessEntity = browserProcessService.getBrowserProcessEntity(snippingId);
        if (browserProcessEntity.getStatus() != Status.IN_PROGRESS)
            throw new WarningException("Snipping is not in IN_PROGRESS state. Snipping Id: " + snippingId);

        browserProcessHelper.cancelRunningTask(snippingId);
        browserProcessService.cancelBrowserProcess(snippingId);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/snipping/{snippingId}")
    public SnippingView getSnippingStatus(@PathVariable Long snippingId) {
        var entity = browserProcessService.getBrowserProcessEntity(snippingId);

        return SnippingView.builder()
                           .id(entity.getId())
                           .status(entity.getStatus())
                           .futAccountId(entity.getFutAccount().getId())
                           .futEaAccountLogin(entity.getFutAccount().getEaLogin())
                           .build();
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/resetBrowserProcess/{futAccountId}")
    public void resetBrowserProcess(@PathVariable Long futAccountId) {
        var futAccount = futAccountService.getFutAccountById(futAccountId);
        var futAccountBrowserProcesses = browserProcessService.getBrowserProcessEntitiesForFutAccount(futAccount, Status.IN_PROGRESS);

        futAccountBrowserProcesses.forEach(process -> {
            var isTaskInRunningState = browserProcessHelper.isTaskRunning(process.getId());
            if (isTaskInRunningState) {
                throw new WarningException("There is a running task for fut account: " + futAccountId + ". Task id: " + process.getId() + ", firstly cancel it.");
            }

            browserProcessService.resetBrowserProcessForFutAccount(futAccount);

        });
    }

}
