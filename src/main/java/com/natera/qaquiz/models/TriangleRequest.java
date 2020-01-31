package com.natera.qaquiz.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class TriangleRequest {

    private String separator;
    private String input;
}
