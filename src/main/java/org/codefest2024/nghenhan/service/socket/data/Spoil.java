package org.codefest2024.nghenhan.service.socket.data;

import java.util.HashMap;
import java.util.Map;

public class Spoil extends Position {
    public static final int STICKY_RICE = 32;
    public static final int CHUNG_CAKE = 33;
    public static final int NINE_TUSK_ELEPHANT = 34;
    public static final int NINE_SPUR_ROOSTER = 35;
    public static final int NINE_MANE_HAIR_HORSE = 36;
    public static final int HOLY_SPIRIT_STONE = 37;

    public static final Map<Integer, Integer> SPOIL_VALUE = new HashMap<>();

    static {
        SPOIL_VALUE.put(STICKY_RICE, 1);
        SPOIL_VALUE.put(CHUNG_CAKE, 2);
        SPOIL_VALUE.put(NINE_TUSK_ELEPHANT, 5);
        SPOIL_VALUE.put(NINE_SPUR_ROOSTER, 3);
        SPOIL_VALUE.put(NINE_MANE_HAIR_HORSE, 4);
        SPOIL_VALUE.put(HOLY_SPIRIT_STONE, 3);
    }

    public int spoil_type;
}
