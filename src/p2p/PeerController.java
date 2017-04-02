package p2p;

import com.jfoenix.controls.JFXListView;
import cs.*;
import cs.client.FileClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by kevin on 02/04/2017.
 */
public class PeerController extends cs.client.Controller {
    /**
     * UI
     */
    @FXML
    private Label whoami;
    @FXML
    private Button peerNext1;
    @FXML
    private Button peerNext2;


    private int myPeerId;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        super.initialize(url, rb);

        myPeerId = Config.currentId;
        whoami.setText("I'm peer " + String.valueOf(myPeerId));
        peerNext1.setText("Peer " + (myPeerId + 1) % Config.TOTAL_PEERS);
        peerNext2.setText("Peer " + (myPeerId + 2) % Config.TOTAL_PEERS);

        fileClient.setRoot(Config.DEFAULT_ROOT);
    }


    @FXML
    public void connectPeerNext1Action() {
        hostTextField.setText(Config.DEFAULT_SERVER);
        portTextField.setText(String.valueOf(Config.DEFAULT_PORT_BASE + (myPeerId + 1) % Config.TOTAL_PEERS));
        connectAction();
    }

    @FXML
    public void connectPeerNext2Action() {
        hostTextField.setText(Config.DEFAULT_SERVER);
        portTextField.setText(String.valueOf(Config.DEFAULT_PORT_BASE + (myPeerId + 2) % Config.TOTAL_PEERS));
        connectAction();
    }

    @Override
    protected void getFile(String file) {
        if (!fileClient.isConnected()) {
            return;
        }
        Platform.runLater(() -> {
            if (fileClient.downloadFile(file)) {
                info("Download " + file + " successfully to " + Config.DEFAULT_ROOT);
            } else {
                err("Download " + file + " failed");
            }
        });
    }

}
