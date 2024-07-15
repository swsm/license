package com.swsm.license.sdk.model;

import java.util.List;
import lombok.Data;

/**
 * @author swsm
 * @date 2024-07-11
 */
@Data
public class HardwareInfo {

    /**
     * 可被允许的IP地址
     */
    private List<String> ipAddress;

    /**
     * 可被允许的MAC地址
     */
    private List<String> macAddress;

    /**
     * 可被允许的CPU序列号
     */
    private String cpuSerial;

    /**
     * 可被允许的主板序列号
     */
    private String mainBoardSerial;
    
}
