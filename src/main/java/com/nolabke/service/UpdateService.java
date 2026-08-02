package com.nolabke.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nolabke.config.AppInfo;
import com.nolabke.utils.AppLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateService {

    private static final String VERSION_URL =
            "https://api.github.com/repos/nolabke/budget/releases/latest";

    private final ObjectMapper mapper = new ObjectMapper();

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

            JsonNode root =
                    mapper.readTree(json);

            return root.get("tag_name")
                    .asText();

        } catch (Exception e) {

            AppLogger.error(
                    "Cannot read latest version",
                    e
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

            JsonNode root =
                    mapper.readTree(json);


            String os =
                    System.getProperty("os.name")
                            .toLowerCase();


            String extension;

            if (os.contains("win")) {
                extension = ".exe";

            } else if (os.contains("mac")) {
                extension = ".dmg";

            } else {
                extension = ".deb";
            }


            String architecture =
                    getArchitecture();


            JsonNode assets =
                    root.get("assets");


            if (assets == null || !assets.isArray()) {
                return null;
            }


            for (JsonNode asset : assets) {

                String name =
                        asset.get("name")
                                .asText()
                                .toLowerCase();


                boolean correctExtension =
                        name.endsWith(extension);


                boolean correctArchitecture =
                        name.contains(architecture);


                if (correctExtension
                        && correctArchitecture) {

                    return asset
                            .get("browser_download_url")
                            .asText();
                }
            }


        } catch (Exception e) {

            AppLogger.error(
                    "Cannot parse update information",
                    e
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

    private String getArchitecture() {

        String arch = System
                .getProperty("os.arch")
                .toLowerCase();


        if (arch.equals("amd64")
                || arch.equals("x86_64")) {

            return "amd64";
        }


        if (arch.equals("aarch64")
                || arch.equals("arm64")) {

            return "arm64";
        }


        return arch;
    }
}