package com.example.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResultDto {
    private String recordIdx;
    private String result;  // ai 모델 결과값

    private Boolean isHuman = false;

    private String text = null; // isHuman 일 경우 stt 결과값 저장
}
