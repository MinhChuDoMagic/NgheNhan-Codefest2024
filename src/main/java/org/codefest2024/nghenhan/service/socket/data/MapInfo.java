package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;

import java.util.List;

public class MapInfo {
    public static final int BLANK = 0;
    public static final int WALL = 1;
    public static final int BOX = 2;
    public static final int BRICK = 3;
    public static final int PRISON = 5;
    public static final int BADGE = 6;
    public static final int DESTROYED = 7;

    public MapSize size;
    public List<Player> players;
    public int[][] map;
    public List<Bomb> bombs;
    public List<Spoil> spoils;
    public List<WeaponHammer> weaponHammers;
    public List<WeaponWind> weaponWinds;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
