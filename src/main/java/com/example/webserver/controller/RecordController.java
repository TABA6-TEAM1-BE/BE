package com.example.webserver.controller;

import com.example.webserver.entity.Record;
import com.example.webserver.login.CustomUserDetails;
import com.example.webserver.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @PostMapping("/records")
    public ResponseEntity<Record> saveRecord(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("file")MultipartFile file) throws Exception {
        Record record = recordService.saveRecord(userDetails, file);
        return ResponseEntity.ok(record);

    }

    @GetMapping("/records")
    public ResponseEntity<List<Record>> getRecords(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Record> records =recordService.getRecordsByUsername(userDetails.getUsername());
        return ResponseEntity.ok(records);
    }

    // 음성파일 백앤드에서 받아서 recordIdx, time 저장 후 ai 넘겨줌
    @PostMapping("/input")
    public ResponseEntity<Record> fileInput(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("file") MultipartFile file) throws Exception {
        Record record = recordService.fileInput(userDetails, file);
        return ResponseEntity.ok(record);
    }

    // 알림 미확인 Record 조회 -> checked 가 false
    @GetMapping("/records/unchecked")
    public ResponseEntity<?> getUncheckedRecords(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return recordService.getUncheckedRecordsByUsername(userDetails.getUsername());
    }

    // 해당 날짜의 deviceType 별로 Record 조회
    @GetMapping("/records/device-type/{deviceType}/date")
    public ResponseEntity<?> getRecordsByDeviceType(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String deviceType, @RequestParam("date") String recordDate) {
        // 문자열을 LocalDate로 변환
        LocalDate date = LocalDate.parse(recordDate);
        return recordService.getRecordsByDeviceTypeAndDate(userDetails.getUsername(), deviceType, date);
    }

    // 클라이언트가 알림 확인 시 checked -> true 로 업데이트
    @PatchMapping("/records/{id}/checked")
    public ResponseEntity<?> updateCheckedStatus(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {
        return recordService.updateCheckedStatus(userDetails, id);
    }
}
