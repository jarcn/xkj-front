package com.cwlrdc.front.calc.bean;

import lombok.Data;

/**
 * 各省销售明细文件销量汇总输入参数
 * Created by chenjia on 2017/4/12.
 */
@Data
public class SalesStatReqBean {
    private String provinceId;
    private String periodNum;
    private String gameCode;
    private String filePath;
}
