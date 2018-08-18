package com.techrace.spit.techrace2018;

public class Core {
    private String mName;
    private String mPosition;

    public Core(String name, String position) {
        mName = name;
        mPosition = position;
    }

    public String getmPosition() {
        return mPosition;
    }

    public void setmPosition(String mPosition) {
        this.mPosition = mPosition;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
