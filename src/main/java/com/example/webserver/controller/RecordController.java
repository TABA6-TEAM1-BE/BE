package com.example.webserver.controller;

import com.example.webserver.entity.Record;
import com.example.webserver.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @PostMapping("/records")
    public ResponseEntity<Record> saveRecord(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("file")MultipartFile file) throws Exception {
        Record record = recordService.saveRecord(userDetails, file);
        return ResponseEntity.ok(record);

    }

    @GetMapping("/records")
    public ResponseEntity<List<Record>> getRecords(@AuthenticationPrincipal UserDetails userDetails) {
        List<Record> records =recordService.getRecordsByUsername(userDetails.getUsername());
        return ResponseEntity.ok(records);
    }
}
