package com.example.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResultDto {
    private String userIdx;
    private String recordIdx;
    private String result;
}
