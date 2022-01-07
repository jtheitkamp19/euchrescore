package com.tomcat.mobile.euchrescore2.DataTables;

public abstract class DataTable {
    String DATABASE_NAME = "scoresdata.db";
    int DATABASE_VERSION = 1;
    protected String[] COLUMNS;
    protected String[] DATA_TYPES;
    protected String TABLE_NAME;

    public String[] getColumns() {
        return COLUMNS;
    }

    public String getTableName() {
        return TABLE_NAME;
    }
}
