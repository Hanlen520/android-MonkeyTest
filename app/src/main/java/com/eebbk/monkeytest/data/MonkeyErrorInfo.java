package com.eebbk.monkeytest.data;

public class MonkeyErrorInfo {
    private int id;
    private String machineId;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
    private String packageName;
    private String errorType;
    private String errorInfo;
    private String insertTime;

    // for query
    public MonkeyErrorInfo(int id, String machineId, String deviceModel, String osVersion, String appVersion, String packageName, String errorType, String errorInfo, String insertTime) {
        this.id = id;
        this.machineId = machineId;
        this.deviceModel = deviceModel;
        this.osVersion = osVersion;
        this.appVersion = appVersion;
        this.packageName = packageName;
        this.errorType = errorType;
        this.errorInfo = errorInfo;
        this.insertTime = insertTime;
    }

    // for upload
    public MonkeyErrorInfo(String machineId, String deviceModel, String osVersion, String appVersion, String packageName, String errorType, String errorInfo) {
        this.machineId = machineId;
        this.deviceModel = deviceModel;
        this.osVersion = osVersion;
        this.appVersion = appVersion;
        this.packageName = packageName;
        this.errorType = errorType;
        this.errorInfo = errorInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "MonkeyErrorInfo{" +
                "machineId='" + machineId + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", packageName='" + packageName + '\'' +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}
