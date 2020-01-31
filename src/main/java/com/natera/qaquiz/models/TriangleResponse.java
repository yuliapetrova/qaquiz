package com.natera.qaquiz.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TriangleResponse {

    @Exclude
    private String id;
    private int firstSide;
    private int secondSide;
    private int thirdSide;
}
