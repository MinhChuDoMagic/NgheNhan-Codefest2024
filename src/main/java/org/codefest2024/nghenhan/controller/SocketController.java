package org.codefest2024.nghenhan.controller;

import com.google.gson.Gson;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.codefest2024.nghenhan.service.caculator.FarmStrategy;
import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.socket.ClientConfig;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.SocketUtils;
import org.codefest2024.nghenhan.utils.TextUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class SocketController implements Initializable {
    @FXML
    TextField edtGameId;
    @FXML
    TextField edtPlayerId;

    @FXML
    TextField editTextURL;

    @FXML
    javafx.scene.control.TextField edtPowerType;

    @FXML
    Button btnStart;
    @FXML
    Button btnStop;
    @FXML
    TextField editTextAction;
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

    private Strategy strategy = new FarmStrategy();

    private final Emitter.Listener mOnTickTackListener = objects -> {
        if (objects != null && objects.length != 0) {
            long startTime = System.currentTimeMillis();
            String data = objects[0].toString();

            if (!TextUtils.isEmpty(data)) {
                gameInfo = new Gson().fromJson(data, GameInfo.class);

                if (gameInfo != null) {
                    List<Order> orders = strategy.find(gameInfo);
//                    log.info("Calculate time: {}", System.currentTimeMillis() - startTime);
                    orders.forEach(this::handleOrder);
                }
            }

//            log.info("Process time: {}", System.currentTimeMillis() - startTime);
        }
    };

    private void handleOrder(Order order) {
        if (order instanceof Action action) {
            handleAction(action);
        } else if (order instanceof Dir dir) {
            movePlayer(dir);
        }
    }

    private void movePlayer(String step) {
        movePlayer(new Dir(step.trim().substring(0, 1)));
    }

    private void movePlayer(Dir dir) {
        if (mSocket != null) {
            log.info("Dir = {}", dir);
            try {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.DRIVE_PLAYER, new JSONObject(dir.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAction(Action action) {
        if (mSocket != null) {
            log.info("Action = {}", action);
            try {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.ACTION, new JSONObject(action.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private final Emitter.Listener mOnDriveStateListener = objects -> {
        String response = objects[0].toString();
        log.info("ClientConfig.PLAYER.INCOMMING.DRIVE_PLAYER: {}", response);
    };

    private Emitter.Listener mOnJoinGameListener = objects -> {
        String response = objects[0].toString();
        log.info("ClientConfig.PLAYER.INCOMMING.JOIN_GAME: {}", response);
    };

    private final Emitter.Listener mOnRegisterPowerResponse = objects -> {
        if (objects != null && objects.length > 0) {
            String response = objects[0].toString();
            log.info("ClientConfig.PLAYER.INCOMMING.REGISTER_POWER: {}", response);
        }
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
            } else {
                String action = Dir.KEY_TO_ACTION.get(event.getCode().ordinal());
                if (action != null) {
                    sendAction(action);
                }
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
        String powerType = edtPowerType.getText().trim();
        connectToServer();
        if (!powerType.isEmpty()) {
            try {
                JSONObject params = new JSONObject();
                params.put("gameId", mGameId);
                params.put("type", powerType);

                mSocket.emit("register character power", params);
                txtMessage.setText("Registered character power with type: " + powerType + "\n");
            } catch (JSONException | NumberFormatException e) {
                e.printStackTrace();
                txtMessage.setText("Failed to register character power: " + e.getMessage() + "\n");
            }
        } else {
            txtMessage.appendText("Power Type is empty. Skipping registration.\n");
        }
    }

    public void btnSend(ActionEvent actionEvent) {
        String step = editTextAction.getText().trim();
//        String action = editTextAction.getText().trim();
        movePlayer(step);
//        sendAction(action);
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

    private void sendAction(String action) {
        sendAction(action, null, null);
    }

    private void sendAction(String action, String characterType, Map<String, Integer> payload) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", action);

            if (characterType != null) {
                json.put("characterType", characterType);
            }
            if (payload != null) {
                json.put("payload", new JSONObject(payload));
            }

            if (mSocket != null) {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.ACTION, json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        mSocket.on(ClientConfig.PLAYER.INCOMMING.REGISTER_POWER, mOnRegisterPowerResponse);
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
