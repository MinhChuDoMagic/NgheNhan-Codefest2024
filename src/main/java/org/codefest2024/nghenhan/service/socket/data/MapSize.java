package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

public class MapSize {
    public int rows;
    public int cols;
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
