package com.example.webserver;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "member")
@Data
public class Member {

    @Id
    private String id;

    private String name;

    public Member(String name) {
        this.name = name;
    }
}
