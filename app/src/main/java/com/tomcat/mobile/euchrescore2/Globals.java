package com.tomcat.mobile.euchrescore2;

public class Globals {
    private static Globals globals = null;

    public static final int TEAM_ONE_ID = 1;
    public static final int TEAM_TWO_ID = 2;

    private Globals() {

    }

    public static void setInstance() {
        globals = new Globals();
    }

    public static synchronized Globals getInstance() {
        return globals;
    }
}
