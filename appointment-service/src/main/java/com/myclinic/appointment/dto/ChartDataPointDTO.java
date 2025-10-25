package com.myclinic.appointment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataPointDTO {
    private String name;          // date hoặc status name
    private Object value;         // revenue  hoặc count (Long)
    private Double percentage;    // optional cho pie chart
}