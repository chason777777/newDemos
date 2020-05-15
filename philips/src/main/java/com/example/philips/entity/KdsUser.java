package com.example.philips.entity;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/8/13
 */
@Document(collection = "kdsUser")
public class KdsUser {
    String _id;
    String userTel;
    String userMail;
    String userPwd;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    @Override
    public String toString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id",_id);
        jsonObject.put("userTel",userTel);
        jsonObject.put("userMail",userMail);
        jsonObject.put("userPwd",userPwd);
        return jsonObject.toString();
    }
}
