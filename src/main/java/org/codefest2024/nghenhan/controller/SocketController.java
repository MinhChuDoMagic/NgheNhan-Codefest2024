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
import org.codefest2024.nghenhan.service.caculator.farming.FarmStrategy;
import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.socket.ClientConfig;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.SocketUtils;
import org.codefest2024.nghenhan.utils.Utils;
import org.codefest2024.nghenhan.utils.constant.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class SocketController implements Initializable {
    @FXML
    TextField edtGameId;
    @FXML
    TextField edtPlayerId;

    @FXML
    TextField edtPowerType;

    @FXML
    TextField editTextURL;

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

    @FXML
    private CheckBox cbUseChild;

    @FXML
    private TextField editTextDestination;


    private static final String URL = "http://localhost/";
    private String mPlayerId;
    private String mGameId;
    private String powerType;

    private Socket mSocket;
    private static GameInfo gameInfo;

    private Strategy strategy = new FarmStrategy();

    private final Emitter.Listener mOnTickTackListener = objects -> {
        if (objects != null && objects.length != 0) {
            long startTime = System.currentTimeMillis();
            String data = objects[0].toString();

            if (!Utils.isEmpty(data)) {
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
        movePlayer(new Dir(step.trim().substring(0, 1), cbUseChild.isSelected()));
    }

    private void movePlayer(Dir dir) {
        if (mSocket != null && !dir.direction.isEmpty()) {
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
        String response = objects[0].toString();
        log.info("ClientConfig.PLAYER.INCOMMING.REGISTER_POWER: {}", response);
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        edtGameId.setText(Constants.KEY_MAP);
        edtPlayerId.setText(Constants.KEY_TEAM);
        edtPowerType.setText(Constants.CHARACTER_POWER);
        editTextURL.setText(URL);
        btnStop.setDisable(true);

        txtController.setOnKeyPressed(event -> {
            txtController.setText(event.getCode().toString() + " - " + event.getCode().ordinal());
            int key = event.getCode().ordinal();
            String step = Dir.KEY_TO_STEP.get(key);
            if (!Utils.isEmpty(step)) {
                movePlayer(step);
            } else {
                String action = Dir.KEY_TO_ACTION.get(event.getCode().ordinal());
                processActionForPlayer(action);
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
        powerType = edtPowerType.getText().trim();
        if (Utils.isEmpty(powerType)) {
            txtMessage.appendText("Power Type is empty. Skipping registration.\n");
            return;
        }
        connectToServer();
    }

    public void btnSend(ActionEvent actionEvent) {
        String step = editTextAction.getText().trim();
        String action = editTextAction.getText().trim();
        if (isStep(step)) {
            movePlayer(step);
        } else {
            processActionForPlayer(action);
        }
    }

    private boolean isStep(String input) {
        return input.matches("[0-9b]+");
    }

    private void sendAction(Action action) {
        try {
            if (mSocket != null) {
                String json = new Gson().toJson(action);
                log.info("Sending json: {}", json);
                log.info("Sending Action: {}", action.toString());
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.ACTION, new JSONObject(action.toString()));
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
                JSONObject powerParams = new JSONObject();
                powerParams.put("gameId", mGameId);
                powerParams.put("type", Integer.parseInt(powerType));
                log.info("Registering power with params: {}", powerParams);
                mSocket.emit(ClientConfig.PLAYER.INCOMMING.REGISTER_POWER, powerParams);
                txtMessage.setText("Registered power type: " + powerType + "\n");
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

    private Position processPositionForWeapon(String text) {
        if (text != null && text.contains(",")) {
            String[] parts = text.split(", ");
            try {
                return new Position(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }
        }
        return null;
    }
    
    private void processActionForPlayer(String action) {
        String useChild = cbUseChild.isSelected() ? Action.USE_CHILD : null;
        String destination = editTextDestination.getText().trim();
        Position position = "1".equals(edtPowerType.getText().trim()) ? processPositionForWeapon(destination) : null;

        Payload payload = new Payload();
        payload.destination = position;

        log.info("Action: {}, Payload: {}", action, payload);

        switch (action) {
            case Dir.SWITCH_WEAPON -> sendAction(new Action(Action.SWITCH_WEAPON, useChild));
            case Dir.USE_WEAPON -> sendAction(new Action(Action.USE_WEAPON, payload, useChild));
            case Dir.MARRY_WIFE -> sendAction(new Action(Action.MARRY_WIFE));
        }
    }

}
