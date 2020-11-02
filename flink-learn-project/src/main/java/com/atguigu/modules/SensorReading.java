package com.atguigu.modules;

import lombok.*;

/**
 * @author : Kasa
 * @date : 2020/10/30 10:49
 * @descripthon :
 */
// 定义温度传感器类

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SensorReading{
    private String id;
    private long timestamp;
    private double temperature;
}
