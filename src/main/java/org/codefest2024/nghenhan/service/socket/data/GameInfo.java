package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

public class GameInfo {
    public long id;
    public long timestamp;
    public String tag;
    public long gameRemainTime;
    public MapInfo map_info;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
