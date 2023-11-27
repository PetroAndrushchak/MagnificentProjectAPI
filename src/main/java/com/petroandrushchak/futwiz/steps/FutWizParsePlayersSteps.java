package com.petroandrushchak.futwiz.steps;

import com.petroandrushchak.futwiz.pages.FutWizPlayersPage;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.service.FutWizService;
import com.petroandrushchak.steps.FutWizMappingSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class FutWizParsePlayersSteps {

    @Autowired FutWizService futWizService;
    @Autowired FutWizPlayersPage futWizPlayersPage;
    @Autowired FutWizMappingSteps futWizMappingSteps;

    public List<ThirdPartySitePlayer> parsePlayersFromFutWiz(String filteredPage) {

        List<ThirdPartySitePlayer> players = new ArrayList<>();

        String nextPage = filteredPage;
        boolean playersArePresent = true;

        //Total pages 663 with 28 items for each page
        while (playersArePresent) {
            Waiter.waitFor(Duration.ofSeconds(1));
            var pageContent = futWizService.getPageForUrl(nextPage);
            if (futWizPlayersPage.isPageContainsPlayers(pageContent)) {
                var rawPlayers = futWizPlayersPage.parsePlayerFromPage(pageContent);
                var playersFromOnePage = futWizMappingSteps.mapNewRawPlayersToPlayers(rawPlayers);
                players.addAll(playersFromOnePage);
            } else {
                playersArePresent = false;
            }
            nextPage = incrementPageNumber(nextPage);
        }

        return players;
    }

    public static String incrementPageNumber(String originalUrl) {
        try {
            // Parse the original URL
            URI uri = new URI(originalUrl);
            String query = uri.getQuery();

            // Split the query string into parameters
            String[] params = query.split("&");
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                if (param.startsWith("page=")) {
                    // Extract the current page number
                    int currentPage = Integer.parseInt(param.substring(5));
                    // Increment the page number by 1
                    int newPage = currentPage + 1;
                    // Update the parameter
                    params[i] = "page=" + newPage;
                }
            }

            // Reconstruct the updated URL
            String updatedQuery = String.join("&", params);
            String updatedUrl = originalUrl.replace(query, updatedQuery);

            return updatedUrl;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return originalUrl; // Return the original URL in case of an error
        }
    }
}
