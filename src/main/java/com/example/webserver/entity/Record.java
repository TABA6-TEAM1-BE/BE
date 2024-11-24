package com.example.webserver.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    @Id
    private String id;

    private String memberId;

    private String deviceType;

    private LocalDateTime time;

    public Record(String memberId, String deviceType, LocalDateTime time) {
        this.memberId = memberId;
        this.deviceType = deviceType;
        this.time = time;
    }
}
