package com.techrace.spit.techrace2018;

public class Feed {
    private String mTitle;
    private String mInfo;

    public Feed(String title, String info) {
        mTitle = title;
        mInfo = info;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmInfo() {
        return mInfo;
    }

    public void setmInfo(String mInfo) {
        this.mInfo = mInfo;
    }
}
