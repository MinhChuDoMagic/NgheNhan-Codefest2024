package org.codefest2024.nghenhan.service.socket;

public class ClientConfig {
    public static class PLAYER {
        public static class OUTGOING {
            public static final String JOIN_GAME = "join game";
            public static final String DRIVE_PLAYER = "drive player";
            public static final String ACTION = "action";
        }

        public static class INCOMMING {
            public static final String JOIN_GAME = "join game";
            public static final String TICKTACK_PLAYER = "ticktack player";
            public static final String DRIVE_PLAYER = "drive player";
            public static final String REGISTER_POWER = "register character power";
        }
    }
}
