package com.cwlrdc.front.common;

import lombok.Data;

/**
 * Created by chenjia on 2017/6/1.
 */
@Data
public class GamePeriodBatch {
    private String gameCode;
    private String startTime;
    private String endTime;
    private String periodNum;
    private Integer cashDeadline;
}
