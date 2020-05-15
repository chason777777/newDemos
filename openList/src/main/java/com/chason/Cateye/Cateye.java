package com.chason.Cateye;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/22
 */
public class Cateye {
    public static void main(String[] args) throws Exception {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("C:\\Users\\windows10\\Desktop\\sssss.json"))));
        File file = new File("C:\\Users\\windows10\\Desktop\\productList-07-22.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String str;
        Map<String, String> map = new HashMap<String, String>();
        while (StringUtils.isNotEmpty(str = br.readLine())) {
            JsonObject jsonObject = new JsonObject(str);
            JsonArray ja = jsonObject.getJsonArray("deviceList");
            if (ja.size() > 0) {
                for (int i = 0; i < ja.size(); i++) {
                    JsonObject o = ja.getJsonObject(i);
                    if (o != null & !map.containsKey(o.getString("deviceId")) && o.getString("device_type").equals("kdscateye")) {
                        pw.println(o.getString("deviceId"));
                        pw.flush();
                        map.put(o.getString("deviceId"), o.getString("deviceId"));
                    }
                }
            }
        }
        System.out.println(map.size());
    }
}
