package com.example.webserver.repository;

import com.example.webserver.entity.Record;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends MongoRepository<Record, String> {
    public List<Record> findAllByMemberId(String memberId);
}
