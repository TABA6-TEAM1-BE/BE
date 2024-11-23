package com.example.webserver.repository;

import com.example.webserver.entity.Record;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordRepository extends MongoRepository<Record, String> {
    public List<Record> findAllByMemberId(String memberId);

    // checked 가 false 인 Record 반환 -> 클라이언트가 아직 확인하지 못한 알림
    public List<Record> findAllByMemberIdAndCheckedIsFalse(String memberId);

    // 해당 날짜의 deviceType 별로 Record 조회
    List<Record> findAllByMemberIdAndDeviceTypeAndTimeBetween(String memberId, String deviceType, LocalDateTime startDate, LocalDateTime endDate);
}
