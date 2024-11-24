package com.example.webserver.service;

import com.example.webserver.entity.Record;
import com.example.webserver.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordService {
    private static final Logger log = LoggerFactory.getLogger(RecordService.class);
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
        record.setChecked(false);   // checked 기본값 false

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

    // checked 가 false 인 Record 반환
    public ResponseEntity<?> getUncheckedRecordsByUsername(String username) {
        List<Record> records = recordRepository.findAllByMemberIdAndCheckedIsFalse(username);
        log.info("Unchecked Records: {}", records);

        if (records.isEmpty()) {
            return ResponseEntity.status(404).body("Unchecked Record가 존재하지 않습니다: " + username);
        }
        return ResponseEntity.ok(records);
    }

    // 주어진 날짜에서 deviceType 별로 Record 반환
    public ResponseEntity<?> getRecordsByDeviceTypeAndDate(String username, String deviceType, LocalDate date) {
        // 날짜의 시작 시간과 종료 시간 계산
        LocalDateTime startDate = date.atStartOfDay(); // 00:00:00
        LocalDateTime endDate = startDate.plusDays(1); // 다음 날 00:00:00

        List<Record> records = recordRepository.findAllByMemberIdAndDeviceTypeAndTimeBetween(username, deviceType, startDate, endDate);

        if (records.isEmpty()) {
            return ResponseEntity.status(404).body(deviceType + "에 대한 Record가 존재하지 않습니다. 날짜: " + date);
        }
        return ResponseEntity.ok(records);
    }

    // 클라이언트가 알림 확인 시 checked 상태 업데이트
    public ResponseEntity<?> updateCheckedStatus(UserDetails userDetails, String id) {
        Optional<Record> recordOptional = recordRepository.findById(id);

        if (recordOptional.isPresent()) {
            Record record = recordOptional.get();

            // Record의 memberId와 userDetails의 username이 같은지 확인
            if (!record.getMemberId().equals(userDetails.getUsername())) {
                return ResponseEntity.status(403).body("사용자가 일치하지 않습니다.");
            }

            record.setChecked(true); // checked 상태 업데이트
            recordRepository.save(record);
            return ResponseEntity.ok(record);
        }
        return ResponseEntity.status(404).body("해당 Record가 존재하지 않습니다.: " + id);
    }
}
