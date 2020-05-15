package com.example.philips.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/8/13
 */
public interface PhilipsService {
    ResponseEntity<FileSystemResource> export();
}
