package com.eebbk.monkeytest.data;

import java.util.List;

public class MonkeyEventInfo {
    private String machineId;
    private String deviceModel;
    private String osVersion;
    private String startTime;
    private String finishTime;
    private List<String> testPackages;

    public MonkeyEventInfo(String machineId, String deviceModel, String osVersion, String startTime, String finishTime, List<String> testPackages) {
        this.machineId = machineId;
        this.deviceModel = deviceModel;
        this.osVersion = osVersion;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.testPackages = testPackages;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public List<String> getTestPackages() {
        return testPackages;
    }

    public void setTestPackages(List<String> testPackages) {
        this.testPackages = testPackages;
    }

    @Override
    public String toString() {
        return "MonkeyEventInfo{" +
                "machineId='" + machineId + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                '}';
    }
}
