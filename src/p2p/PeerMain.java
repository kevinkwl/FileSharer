package p2p;/**
 * Created by kevin on 02/04/2017.
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PeerMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private PeerController controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("peermain.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        primaryStage.setTitle("Peer " + String.valueOf(Config.currentId));
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
}
