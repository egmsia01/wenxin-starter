package com.gearwenxin.entity.enums;

import lombok.Getter;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/11/4
 */
@Getter
public enum SamplerType {
    Euler("Euler"),
    Euler_A("Euler a"),
    DPM_2M("DPM++ 2M"),
    DPM_2M_Karras("DPM++ 2M Karras"),
    LMS_Karras("LMS Karras"),
    DPM_SDE("DPM++ SDE"),
    DPM_SDE_Karras("DPM++ SDE Karras"),
    DPM2_a_Karras("DPM2 a Karras"),
    Heun("Heun"),
    DPM_2M_SDE("DPM++ 2M SDE"),
    DPM_2M_SDE_Karras("DPM++ 2M SDE Karras"),
    DPM2("DPM2"),
    DPM2_Karras("DPM2 Karras"),
    DPM2_a("DPM2 a"),
    LMS("LMS");

    private final String value;

    SamplerType(String value) {
        this.value = value;
    }
}
