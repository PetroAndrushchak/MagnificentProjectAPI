package com.petroandrushchak.registry;

import com.petroandrushchak.helper.ProjectHelper;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FutInternalDataRegistry {

    private static final String FUT_WEB_INTERNAL_PLAYERS_DATA = "/database/fut_web_app_data/players.json";
    private static final String FUT_WEB_INTERNAL_DATA = "/database/fut_web_app_data/en-US.json";

    public static Path playersDataFilePath() {
        return Paths.get(ProjectHelper.getRootProjectFolderPath(), FUT_WEB_INTERNAL_PLAYERS_DATA);
    }

    public static Path futWebInternalData() {
        return Paths.get(ProjectHelper.getRootProjectFolderPath(), FUT_WEB_INTERNAL_DATA);
    }
}
