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
    public static final int SPOIL = 10;
    public static final int CAPTURED_BADGE = 16;
    public static final int PLAYER = 21;
    public static final int CHILD = 22;
    public static final int ENEMY = 23;
    public static final int ENEMY_CHILD = 24;
    public static final int BOMB = 25;
    public static final int BOMB_EXPLODE = 26;
    public static final int HAMMER_EXPLODE = 27;
    public static final int WIND = 28;

    public MapSize size;
    public List<Player> players;
    public int[][] map;
    public List<Bomb> bombs;
    public List<Spoil> spoils;
    public List<Hammer> weaponHammers;
    public List<Wind> weaponWinds;

    public Player player;
    public Player enemy;
    public Player child;
    public Player enemyChild;

    public Bomb playerBomb;
    public Bomb enemyBomb;
    public Bomb childBomb;
    public Bomb enemyChildBomb;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
