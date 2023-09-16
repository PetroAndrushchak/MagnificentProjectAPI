package com.petroandrushchak.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProjectHelper {

    private static final String ROOT_PROJECT_FOLDER_NAME = "MagnificentProjectAPI";

    public static String getRootProjectFolderPath() {
        String workingDir = System.getProperty("user.dir");

        if (workingDir.endsWith(ROOT_PROJECT_FOLDER_NAME)) {
            return workingDir;
        } else {
            int lastIndex = workingDir.lastIndexOf(ROOT_PROJECT_FOLDER_NAME);
            return workingDir.substring(0, lastIndex + ROOT_PROJECT_FOLDER_NAME.length());
        }
    }

}
