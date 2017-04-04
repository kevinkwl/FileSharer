package cs.client;

import cs.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 850, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (controller.getFileClient().isConnected()) {
            controller.getFileClient().disconnect();
        }
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java FileClient <client_root>");
            System.exit(1);
        }

        Config.CLIENT_ROOT = args[0];
        launch(args);
    }
}
