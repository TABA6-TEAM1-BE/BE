package com.example.webserver.controller;

import com.example.webserver.dto.AiResultDto;
import com.example.webserver.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiResultController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RecordService recordService;
    private static final Logger log = LoggerFactory.getLogger(AiResultController.class);

    // Ai 모델에게서 해당 HTTP 요청을 보내서 결과값 받아옴
    @PostMapping("/results")
    public ResponseEntity<String> receiveAIResult(@RequestBody AiResultDto resultDto) {
        try {
            String userIdx = resultDto.getUserIdx(); // 사용자 식별
            String recordIdx = resultDto.getRecordIdx();    // 기록 식별
            String result = resultDto.getResult();  // AI 결과값

            // Record 업데이트 (resultTime 및 deviceType 저장)
            boolean isUpdated = recordService.updateRecordWithAIResult(recordIdx, result);
            if (!isUpdated) {
                log.warn("Record with recordIdx {} not found", recordIdx);
                return ResponseEntity.status(404).body("Record not found");
            }

            // WebSocket 경로로 메시지 전송
            String destination = "/socket/" + userIdx;
            messagingTemplate.convertAndSend(destination, result);

            log.info("WebSocket message sent to {}: {}", destination, result);
            return ResponseEntity.ok("Message sent to WebSocket");
        } catch (Exception e) {
            log.error("Error while sending WebSocket message: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to send WebSocket message");
        }
    }
}
