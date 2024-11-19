package org.codefest2024.nghenhan.controller;

import com.google.gson.Gson;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.codefest2024.nghenhan.service.caculator.AStarFinder;
import org.codefest2024.nghenhan.service.caculator.DirectionFinder;
import org.codefest2024.nghenhan.service.socket.ClientConfig;
import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.Game;
import org.codefest2024.nghenhan.service.socket.data.GameInfo;
import org.codefest2024.nghenhan.utils.SocketUtils;
import org.codefest2024.nghenhan.utils.TextUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
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
    CheckBox cbFProxy;

    private static final String URL = "http://localhost/";
    private String mPlayerId;
    private String mGameId;

    private Socket mSocket;
    private static GameInfo gameInfo;

    private DirectionFinder directionFinder = new AStarFinder();

    private final Emitter.Listener mOnTickTackListener = objects -> {
        if(objects != null && objects.length != 0){
            long startTime = System.currentTimeMillis();
            String data = objects[0].toString();

            if (!TextUtils.isEmpty(data)) {
                gameInfo = new Gson().fromJson(data, GameInfo.class);
                log.info("Parse time: {}", System.currentTimeMillis() - startTime);

//                if (gameInfo != null) {
//                    Dir dir = directionFinder.find(gameInfo);
//                    movePlayer(dir);
//                }
            }
        }
    };

    private final Emitter.Listener mOnDriveStateListener = objects -> {
        String response = objects[0].toString();
        log.info("ClientConfig.PLAYER.INCOMMING.DRIVE_PLAYER: {}", response);
    };

    private Emitter.Listener mOnJoinGameListener = objects -> {
        String response = objects[0].toString();
        log.info("ClientConfig.PLAYER.INCOMMING.JOIN_GAME: {}", response);
    };

    @Override
    @SuppressWarnings("unknown enum constant DeprecationLevel.ERROR")
    public void initialize(URL url, ResourceBundle resourceBundle) {
        edtGameId.setText(Constants.KEY_MAP);
        edtPlayerId.setText(Constants.KEY_TEAM);
        editTextURL.setText(URL);
        btnStop.setDisable(true);

        txtController.setOnKeyPressed(event -> {
            txtController.setText(event.getCode().toString() + " - " + event.getCode().ordinal());
            int key = event.getCode().ordinal();
            String step = Dir.KEY_TO_STEP.get(key);
            if (!TextUtils.isEmpty(step)) {
                movePlayer(step);
            }
        });
    }

    public void onBtnStopClicked(ActionEvent actionEvent) {
        try {
            btnStop.setDisable(true);
            txtMessage.setText("");
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onButtonRegisterClicked(ActionEvent actionEvent) {
        mPlayerId = edtPlayerId.getText().trim();
        mGameId = edtGameId.getText().trim();
        connectToServer();
    }

    public void btnSend(ActionEvent actionEvent) {
        String step = editTextAction.getText().trim();
        movePlayer(step);
    }

    private void movePlayer(String step) {
        movePlayer(new Dir(step.trim().substring(0,1)));
    }

    private void movePlayer(Dir dir){
        if (mSocket != null) {
            log.info("Dir = {}", dir);
            try {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.DRIVE_PLAYER, new JSONObject(dir.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectToServer() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket = null;
        }

        mSocket = SocketUtils.init(editTextURL.getText(), cbFProxy.isSelected());
        if (mSocket == null) {
            log.warn("Socket null - can't connect");
            return;
        }
        mSocket.on(ClientConfig.PLAYER.INCOMMING.JOIN_GAME, mOnJoinGameListener);
        mSocket.on(ClientConfig.PLAYER.INCOMMING.TICKTACK_PLAYER, mOnTickTackListener);
        mSocket.on(ClientConfig.PLAYER.INCOMMING.DRIVE_PLAYER, mOnDriveStateListener);
        mSocket.on(Socket.EVENT_CONNECT, objects -> {
            log.info("Connected");
            String gameParams = new Game(mGameId, mPlayerId).toString();
            log.info("Game params = {}", gameParams);
            try {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.JOIN_GAME, new JSONObject(gameParams));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        mSocket.on(Socket.EVENT_CONNECT_ERROR, objects -> log.warn("Connect Failed"));
        mSocket.on(Socket.EVENT_DISCONNECT, objects -> log.warn("Disconnected"));
        mSocket.connect();
        btnStop.setDisable(false);
        txtMessage.setText("Running!!!");
    }
}
