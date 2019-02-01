package com.eebbk.monkeytest.data;

/**
 * Created by admin on 2018/5/16.
 */

public class MonkeyResultEvent {
    private boolean hasErrorInfo;
    private String errorPackageNames;

    public MonkeyResultEvent(boolean hasErrorInfo, String errorPackageNames) {
        this.hasErrorInfo = hasErrorInfo;
        this.errorPackageNames = errorPackageNames;
    }

    public boolean isHasErrorInfo() {
        return hasErrorInfo;
    }

    public void setHasErrorInfo(boolean hasErrorInfo) {
        this.hasErrorInfo = hasErrorInfo;
    }

    public String getErrorPackageNames() {
        return errorPackageNames;
    }

    public void setErrorPackageNames(String errorPackageNames) {
        this.errorPackageNames = errorPackageNames;
    }
}
