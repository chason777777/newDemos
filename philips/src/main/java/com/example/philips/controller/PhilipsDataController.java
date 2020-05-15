package com.example.philips.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.philips.service.PhilipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/8/13
 */
@RestController
@RequestMapping("/philips/data")
public class PhilipsDataController {
    @Autowired
    private PhilipsService philipsService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("/export")
    public ResponseEntity<FileSystemResource> export() {

        return philipsService.export();
    }
}
