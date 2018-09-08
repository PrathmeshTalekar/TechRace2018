package com.techrace.spit.techrace2018;

public class Feed {
    private String mTitle;
    private String mInfo;
    private String mTime;


    public Feed(String title, String info, String time) {
        mTitle = title;
        mInfo = info;
        mTime = time;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
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
