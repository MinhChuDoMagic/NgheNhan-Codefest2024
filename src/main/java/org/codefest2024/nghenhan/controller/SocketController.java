package org.codefest2024.nghenhan.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class SocketController implements Initializable {
    @FXML
    javafx.scene.control.TextField edtGameId;
    @FXML
    javafx.scene.control.TextField edtPlayerId;

    @FXML
    javafx.scene.control.TextField editTextURL;

    @FXML
    javafx.scene.control.Button btnStart;
    @FXML
    javafx.scene.control.Button btnStop;
    @FXML
    javafx.scene.control.TextField editTextAction;
    @FXML
    TextArea txtController;
    @FXML
    TextArea txtMessage;
    @FXML
    CheckBox cbDebugMode, cbFProxy;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
