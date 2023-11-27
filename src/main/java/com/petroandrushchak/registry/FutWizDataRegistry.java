package com.petroandrushchak.registry;

import com.petroandrushchak.helper.ProjectHelper;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FutWizDataRegistry {

    private static final String FUT_WIZ_ALL_PLAYERS_DATA_JSON = "/database/futwiz_players/all_players_data.json";

    public static Path allPlayersFilePath() {
        return Paths.get(ProjectHelper.getRootProjectFolderPath(), FUT_WIZ_ALL_PLAYERS_DATA_JSON);
    }

}
