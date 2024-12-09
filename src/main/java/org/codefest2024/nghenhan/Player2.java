package org.codefest2024.nghenhan;

import io.socket.client.Socket;
import org.codefest2024.nghenhan.service.socket.ClientConfig;
import org.codefest2024.nghenhan.service.socket.data.Game;
import org.codefest2024.nghenhan.utils.SocketUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class Player2 {
    private static final String URL = "http://localhost/";
    private static final String mPlayerId = "player2-xxx";
    private static final String mGameId = Constants.KEY_MAP;

    public static void main(String[] args) {
        Socket mSocket = SocketUtils.init(URL, false);
        if (mSocket == null) {
            return;
        }

        mSocket.on(Socket.EVENT_CONNECT, objects -> {
            String gameParams = new Game(mGameId, mPlayerId).toString();
            try {
                mSocket.emit(ClientConfig.PLAYER.OUTGOING.JOIN_GAME, new JSONObject(gameParams));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mSocket.connect();
    }
}
