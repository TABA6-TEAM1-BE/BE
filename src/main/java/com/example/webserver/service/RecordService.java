package com.example.webserver.service;

import com.example.webserver.entity.Record;
import com.example.webserver.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;

    public String classifySound(MultipartFile file) throws Exception {
        // AI 모델 호출 로직 (샘플로 REST 호출 방식 사용)
        String aiModelUrl = "http://ai-model-service/analyze";

        // 파일 전송 및 결과 수신 로직
        // 예제용 코드 (HTTP Client 필요)
        String deviceType = callAiModel(file, aiModelUrl);

        return deviceType;
    }


    public Record saveRecord(UserDetails userDetails, MultipartFile file) throws Exception {

        String deviceType = classifySound(file);
        LocalDateTime time = LocalDateTime.now();
        // Record 생성
        Record record = new Record();
        record.setDeviceType(deviceType);
        record.setTime(time);
        record.setMemberId(userDetails.getUsername());

        // 실제 데이터베이스 저장 로직 추가 (예: JPA 사용)
        recordRepository.save(record);

        return record;
    }


    private String callAiModel(MultipartFile file, String url) {
        // AI 모델 호출 및 결과 반환
        // HTTP Client 라이브러리 필요 (예: RestTemplate, WebClient 등)
        return "세탁기"; // 샘플 반환값
    }

    public List<Record> getRecordsByUsername(String username) {
        return recordRepository.findAllByMemberId(username);
    }
}
