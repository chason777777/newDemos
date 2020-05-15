package com.example.philips.entity;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/8/13
 */
public class KdsDevice {
    String _id;
    String lockName;
    String model;
    String deviceSN;
    String uname;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    @Override
    public String toString(){
        return _id + "-" + lockName + "-" + uname + "-" + deviceSN;
    }
}
