package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

import java.util.List;

public class MapInfo {
    public MapSize size;
    public List<Player> players;
    public int[][] map;
    public List<Bomb> bombs;
    public List<Spoil> spoils;
    public List<WeaponHammers> weaponHammers;
    public List<WeaponWinds> weaponWinds;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
