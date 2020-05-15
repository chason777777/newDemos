package com.chason;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        InputStream inputStream = App.class.getResourceAsStream("/lockopList-0703.log");
        try {
            FileOutputStream fot = new FileOutputStream("C:\\Users\\windows10\\Desktop\\ll2.txt");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(fot));

            String str = IOUtils.toString(inputStream, "UTF-8");
            JsonObject jsonObject = new JsonObject(str);
            JsonArray items = jsonObject.getJsonObject("hits").getJsonArray("hits");
            List<String> list = new ArrayList<String>();
            items.forEach(e -> {
                String s = "db.kdsOpenLockList.update({\"open_time\":\"" +
                        (new JsonObject(new JsonObject(e.toString()).getJsonObject("_source").getString("payload")).getJsonObject("eventparams").getInteger("zigBeeLocalTime")) * 1000
                        + "\"},{$set:{\"open_time\":\"" + new JsonObject(new JsonObject(e.toString()).getJsonObject("_source").getString("payload")).getString("timestamp") + "\"}})";
                if(new JsonObject(new JsonObject(e.toString()).getJsonObject("_source").getString("payload")).getJsonObject("eventparams").getInteger("devecode") == 2){
                    list.add(s);
                    pw.println(s);
                    System.out.println(new JsonObject(e.toString()).getJsonObject("_source").getString("payload").toString());
                }
            });
            pw.flush();
            pw.close();
            System.out.println(list.get(0));
            System.out.println(list.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
