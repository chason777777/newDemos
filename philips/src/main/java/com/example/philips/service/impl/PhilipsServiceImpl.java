package com.example.philips.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.philips.entity.KdsDevice;
import com.example.philips.entity.KdsUser;
import com.example.philips.service.PhilipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/8/13
 */
@Service
public class PhilipsServiceImpl implements PhilipsService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<FileSystemResource> export() {
        List<String> userL = new ArrayList<String>();
        List<String> devL = new ArrayList<String>();
        List<String> norL = new ArrayList<String>();
        List<String> openL = new ArrayList<String>();

        PrintWriter userP = null;
        PrintWriter devP = null;
        PrintWriter norP = null;
        PrintWriter openP = null;
        try {
            userP = new PrintWriter(new FileWriter("data/userList.txt"));
            devP = new PrintWriter(new FileWriter("data/deviceList.txt"));
            norP = new PrintWriter(new FileWriter("data/normalList.txt"));
            openP = new PrintWriter(new FileWriter("data/openList.txt"));

            Query query = new Query();
            query.addCriteria(Criteria.where("versionType").is("kaadas"));
//            query.addCriteria(Criteria.where("userTel").is("8618954359822"));
            List<JSONObject> users = mongoTemplate.find(query, JSONObject.class, "kdsUser");
            for (JSONObject user : users) {// 用户列表
                user.put("_id", user.getString("_id"));
//                System.out.println(user.toString());

                if (!userL.contains(user.getString("_id"))) {
                    userP.println(user.toString());
                    userL.add(user.getString("_id"));
                }

                Query devicequery = new Query();
                devicequery.addCriteria(Criteria.where("uname").is(user.getString("_id")));
                List<JSONObject> devices = mongoTemplate.find(devicequery, JSONObject.class, "kdsDeviceList");// 设备列表
                for (JSONObject device : devices) {
                    device.put("_id", device.getString("_id"));
//                    System.out.println("---------:" + device.toString());

                    if (!devL.contains(device.getString("_id"))) {
                        devP.println(device.toString());
                        devL.add(device.getString("_id"));
                    }

                    Query openquery = new Query();
                    openquery.addCriteria(Criteria.where("lockName").is(device.getString("lockName")));
                    List<JSONObject> opens = mongoTemplate.find(openquery, JSONObject.class, "kdsOpenLockList");// 设备开锁记录列表
                    for (JSONObject open : opens) {
                        open.put("_id", open.getString("_id"));
//                        System.out.println("---------============:" + open.toString());

                        if (!openL.contains(open.getString("_id"))) {
                            openP.println(open.toString());
                            openL.add(open.getString("_id"));
                        }
                    }
                }

                Query normalquery = new Query();
                normalquery.addCriteria(Criteria.where("uid").is(user.getString("_id")));
                List<JSONObject> normals = mongoTemplate.find(normalquery, JSONObject.class, "kdsNormalLock");// 设备用户管理列表
                for (JSONObject normal : normals) {
                    normal.put("_id", normal.getString("_id"));
//                    System.out.println("+++++++++:" + normal.toString());

                    if (!norL.contains(normal.getString("_id"))) {
                        norP.println(normal.toString());
                        norL.add(normal.getString("_id"));
                    }
                }
            }

            userP.flush();
            devP.flush();
            norP.flush();
            openP.flush();

            //这个是文件夹的绝对路径，如果想要相对路径就自行了解写法
            String sourceFile = "data";
            //这个是压缩之后的文件绝对路径
            FileOutputStream fos = new FileOutputStream("data.zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);

            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", "attachment; filename=" + System.currentTimeMillis() + ".zip");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Last-Modified", new Date().toString());
            headers.add("ETag", String.valueOf(System.currentTimeMillis()));

            File file = new File("data.zip");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new FileSystemResource(file));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (userP != null) {
                userP.close();
            }
            if (devP != null) {
                devP.close();
            }
            if (norP != null) {
                norP.close();
            }
            if (openP != null) {
                openP.close();
            }
        }
        return null;
    }

    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
