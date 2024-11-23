package com.example.webserver.repository;

import com.example.webserver.entity.OpenAiResponseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenAiResponseRepository extends MongoRepository<OpenAiResponseDocument, String> {
}
