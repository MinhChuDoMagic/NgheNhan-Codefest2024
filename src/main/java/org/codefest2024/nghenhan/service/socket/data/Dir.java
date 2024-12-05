package org.codefest2024.nghenhan.service.socket.data;

import com.google.gson.Gson;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public final class Dir extends Order {
    public static final String LEFT = "1";
    public static final String RIGHT = "2";
    public static final String UP = "3";
    public static final String DOWN = "4";
    public static final String ACTION = "b";
    public static final String INVALID = "";
    public static final String STOP = "x";
    public static final String USE_CHILD = "child";
    public static final String SWITCH_WEAPON = "j";
    public static final String USE_WEAPON = "k";
    public static final String MARRY_WIFE = "l";


    public static final Map<Integer, String> KEY_TO_STEP = new HashMap<>();
    public static final Map<Integer, String> KEY_TO_ACTION = new HashMap<>();

    static {
        KEY_TO_STEP.put(KeyCode.UP.ordinal(), UP);
        KEY_TO_STEP.put(KeyCode.LEFT.ordinal(), LEFT);
        KEY_TO_STEP.put(KeyCode.DOWN.ordinal(), DOWN);
        KEY_TO_STEP.put(KeyCode.RIGHT.ordinal(), RIGHT);
        KEY_TO_STEP.put(KeyCode.SPACE.ordinal(), ACTION);

        KEY_TO_ACTION.put(KeyCode.J.ordinal(), SWITCH_WEAPON);
        KEY_TO_ACTION.put(KeyCode.K.ordinal(), USE_WEAPON);
        KEY_TO_ACTION.put(KeyCode.L.ordinal(), MARRY_WIFE);
    }

    public String direction;

    public Dir(String direction, boolean useChild) {
        this.direction = direction;
        this.characterType = useChild ? USE_CHILD : null;
    }

    public Dir(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
