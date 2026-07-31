package com.nolabke;

import com.nolabke.utils.AppLogger;
import com.nolabke.utils.AppSettings;
import com.nolabke.utils.Messages;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("/fxml/main.fxml")
                );


        Scene scene =
                new Scene(
                        loader.load(),
                        900,
                        600
                );

        MainController controller = loader.getController();
        controller.setStage(stage);

        scene.getRoot().setStyle(
                "-fx-font-size: " + AppSettings.getFontSizeKey() + "px;"
        );

        stage.setTitle(Messages.get("app.title"));
        stage.setScene(scene);

        stage.show();
        AppLogger.info(
                "NoLabke started"
        );
    }

    public static void main(String[] args) {
        launch();
    }
}
