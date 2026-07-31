package com.nolabke;

import com.nolabke.config.AppInfo;
import com.nolabke.service.UpdateService;
import com.nolabke.utils.AppSettings;
import com.nolabke.utils.Messages;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu settingsMenu;

    @FXML
    private Menu helpMenu;

    @FXML
    private BudgetController budgetViewController;

    @FXML
    private MenuItem exportMenuItem;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem fontSizeMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private Menu languageMenu;

    @FXML
    private MenuItem langPlItem;

    @FXML
    private MenuItem langEnItem;

    @FXML
    private MenuItem langDeItem;

    private Stage primaryStage;

    @FXML
    private TabPane mainTabs;

    @FXML
    private Tab budgetTab;

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (budgetViewController != null) {
            System.out.println("BudgetController connected");
        } else {
            System.out.println("BudgetController NULL");
        }
        if (fileMenu != null) {
            updateLabels();
        } else {
            System.err.println("ERROR: fileMenu is null!");
        }

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    protected void showAbout() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(Messages.get("menu.about"));

        alert.setHeaderText(
                Messages.get("app.title")
        );

        alert.setContentText(
                Messages.get("menu.about.content")
                        + "\n\n"
                        + "Version "
                        + AppInfo.getVersion()
                        + "\n\n© 2026 nolabke.com"
        );

        alert.showAndWait();
    }

    @FXML
    protected void exitApp() {
        Platform.exit();
    }


    @FXML
    protected void openFontSettings() {

        Stage stage = new Stage();

        stage.setTitle("Font size");


        ComboBox<String> combo = new ComboBox<>();

        combo.getItems().addAll(
                "Small",
                "Medium",
                "Large",
                "Extra Large"
        );


        int current = AppSettings.getFontSizeKey();

        if(current <= 12)
            combo.setValue("Small");
        else if(current <= 16)
            combo.setValue("Medium");
        else if(current <= 20)
            combo.setValue("Large");
        else
            combo.setValue("Extra Large");


        Button save = new Button("Apply");


        save.setOnAction(e -> {

            int size = switch(combo.getValue()) {

                case "Small" -> 12;
                case "Large" -> 20;
                case "Extra Large" -> 24;
                default -> 16;
            };


            AppSettings.setFontSizeKey(size);


            rootPane.getScene()
                    .getRoot()
                    .setStyle(
                            "-fx-font-size: "
                                    + size
                                    + "px;"
                    );


            stage.close();
        });


        VBox box = new VBox(
                15,
                combo,
                save
        );

        box.setStyle(
                "-fx-padding:20;"
        );


        stage.setScene(
                new Scene(box, 250,150)
        );


        stage.show();
    }


    private void changeLanguage(String languageCode) {
        Messages.setLanguage(languageCode);
        updateLabels();

        if(budgetViewController != null){
            budgetViewController.refreshLanguage();
        }

    }

    @FXML
    private void setLanguagePl() {
        changeLanguage("pl");
    }

    @FXML
    private void setLanguageEn() {
        changeLanguage("en");
    }

    @FXML
    private void setLanguageDe() {
        changeLanguage("de");
    }

    private void updateLabels() {
        if (fileMenu != null) fileMenu.setText(Messages.get("menu.file"));
        if (settingsMenu != null) settingsMenu.setText(Messages.get("menu.settings"));
        if (helpMenu != null) helpMenu.setText(Messages.get("menu.help"));
        if (languageMenu != null) languageMenu.setText(Messages.get("menu.language"));

        if (exportMenuItem != null) exportMenuItem.setText(Messages.get("menu.export"));
        if (exitMenuItem != null) exitMenuItem.setText(Messages.get("menu.exit"));
        if (fontSizeMenuItem != null) fontSizeMenuItem.setText(Messages.get("menu.fontsize"));
        if (aboutMenuItem != null) aboutMenuItem.setText(Messages.get("menu.about"));

        // LANGUAGE MENU ITEMS
        if (langPlItem != null) langPlItem.setText(Messages.get("menu.lang.pl"));
        if (langEnItem != null) langEnItem.setText(Messages.get("menu.lang.en"));
        if (langDeItem != null) langDeItem.setText(Messages.get("menu.lang.de"));

        // TITLE
        if (primaryStage != null) primaryStage.setTitle(Messages.get("app.title"));
    }

    @FXML
    protected void export() {

        budgetViewController.export(primaryStage);
    }

    @FXML
    private void checkForUpdates() {

        UpdateService updateService = new UpdateService();

        if (updateService.isUpdateAvailable()) {

            showAlert(
                    Messages.get("update.title"),
                    Messages.get("update.available")
            );

        } else {

            showAlert(
                    Messages.get("update.title"),
                    Messages.get("update.latest")
            );
        }
    }

}