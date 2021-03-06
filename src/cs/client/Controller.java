package cs.client;

import com.jfoenix.controls.JFXListView;
import cs.Config;

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


public class Controller implements Initializable {

    /**
     * UI
     */
    @FXML
    protected Label pathLabel;

    protected StringProperty path = new SimpleStringProperty();

    @FXML
    protected TextField hostTextField;

    @FXML
    protected TextField portTextField;

    @FXML
    protected Button connectButton;


    @FXML
    protected JFXListView fileListView;

    @FXML
    protected Label statusLabel;

    protected StringProperty status = new SimpleStringProperty();

    /**
     * backend
     */
    protected FileClient fileClient = new FileClient("localhost", 2333);  // stub client


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        status.setValue("Disconnected.");
        path.setValue("/");
        hostTextField.setText(Config.DEFAULT_SERVER);
        portTextField.setText(String.valueOf(Config.DEFAULT_PORT));

        pathLabel.textProperty().bind(path);
        statusLabel.textProperty().bind(status);
    }

    @FXML
    public void connectAction() {
        fileClient.setHost(hostTextField.getText());
        fileClient.setPort(Integer.parseInt(portTextField.getText()));

        if (fileClient.isConnected()) {
            fileClient.disconnect();
        }

        if (fileClient.connect()) {
            statusLabel.setStyle("-fx-text-fill: #33ff33;-fx-background-color: black");
            status.setValue("Connected to " + hostTextField.getText() + ":" + portTextField.getText());
            path.setValue(fileClient.getPath());
            setFileList();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;-fx-background-color: black");
            status.setValue("Disconnected");
            err("Cannot connect to " + hostTextField.getText() + ":" + portTextField.getText());
        }
    }

    private void changeDirectoryAction(String pathname) {
        if (!fileClient.isConnected()) {
            return;
        }
        Platform.runLater(() -> {
            fileClient.changeDirectory(pathname);
            setFileList();
            path.setValue(fileClient.getPath());
        });
    }

    @FXML
    public void upAction() {
        changeDirectoryAction("..");
    }

    @FXML
    public void gotoFolderAction(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) { // double click
            String remoteDir = (String)fileListView.getSelectionModel().getSelectedItem();
            if (remoteDir.endsWith("/")) {
                changeDirectoryAction(remoteDir.substring(0, remoteDir.length() - 1));
            } else {
                getFile(remoteDir);
            }
        }
    }


    @FXML
    public void downloadAction() {
        if (fileListView.getItems().isEmpty()) {
            return;
        }
        String file = (String) fileListView.getSelectionModel().getSelectedItem();
        if (!file.endsWith("/")) {
            getFile(file);
        }
    }

    private void setFileList() {
        if (!fileClient.isConnected()) {
            return;
        }
        Platform.runLater(() -> {
            fileListView.setItems(fileClient.getFiles());
        });
    }

    protected void getFile(String file) {
        if (!fileClient.isConnected()) {
            return;
        }
        Platform.runLater(() -> {
            if (fileClient.downloadFile(file)) {
                info("Download " + file + " successfully to " + Config.CLIENT_ROOT);
            } else {
                err("Download " + file + " failed");
            }
        });
    }

    public FileClient getFileClient() {
        return fileClient;
    }

    private void changeStatus(String news) {
        status.setValue(news);
    }

    protected void info(String news) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Got something for you");
        alert.setContentText(news);

        alert.showAndWait();
    }

    protected void err(String news) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something's not right");
        alert.setContentText(news);

        alert.showAndWait();
    }

}
