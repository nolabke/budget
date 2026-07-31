package com.nolabke.service;

import com.nolabke.config.AppInfo;
import com.nolabke.utils.AppLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateService {

    private static final String VERSION_URL =
            "https://api.github.com/repos/nolabke/budget/releases/latest";


    private String getLatestReleaseJson() {

        try {

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(VERSION_URL))
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            return response.body();

        } catch (Exception e) {

            AppLogger.warning(
                    "Cannot check for updates"
            );

            return null;
        }
    }


    public String getLatestVersion() {

        String json = getLatestReleaseJson();

        if (json == null) {
            return null;
        }

        try {

            return json
                    .split("\"tag_name\":\"")[1]
                    .split("\"")[0];

        } catch (Exception e) {

            AppLogger.warning(
                    "Cannot read latest version"
            );

            return null;
        }
    }


    public String getDownloadUrl() {

        String json = getLatestReleaseJson();

        if (json == null) {
            return null;
        }

        try {

            String os = System
                    .getProperty("os.name")
                    .toLowerCase();


            String extension;


            if (os.contains("win")) {
                extension = ".exe";
            }
            else if (os.contains("mac")) {
                extension = ".dmg";
            }
            else {
                extension = ".deb";
            }


            String assets =
                    json.split("\"assets\":\\[")[1]
                            .split("]")[0];


            String[] files =
                    assets.split("\\},\\{");


            for (String file : files) {

                if (file.contains(extension)) {

                    return file
                            .split("\"browser_download_url\":\"")[1]
                            .split("\"")[0];
                }
            }


        } catch (Exception e) {

            AppLogger.warning(
                    "Cannot find update download URL"
            );
        }


        return null;
    }


    public boolean isUpdateAvailable() {

        String latest = getLatestVersion();

        if (latest == null) {
            return false;
        }

        return isNewerVersion(latest);
    }


    private boolean isNewerVersion(String onlineVersion) {

        onlineVersion = onlineVersion.replace("v", "");

        String[] current = AppInfo.getVersion().split("\\.");
        String[] online = onlineVersion.split("\\.");

        int length = Math.max(
                current.length,
                online.length
        );

        for (int i = 0; i < length; i++) {

            int currentPart =
                    i < current.length
                            ? Integer.parseInt(current[i])
                            : 0;

            int onlinePart =
                    i < online.length
                            ? Integer.parseInt(online[i])
                            : 0;


            if (onlinePart > currentPart) {
                return true;
            }

            if (onlinePart < currentPart) {
                return false;
            }
        }

        return false;
    }
}