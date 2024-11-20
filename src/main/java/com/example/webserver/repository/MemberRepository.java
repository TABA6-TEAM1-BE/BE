package com.example.webserver.repository;

import com.example.webserver.entity.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

    public Optional<Member> findByUsername(String username);

}
