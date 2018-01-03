package com.cwlrdc.front.common;

import java.math.BigDecimal;

/**
 * Created by yangqiju on 2017/9/12.
 */
public class BigDecimalUtils {

    public static Double toDouble(BigDecimal bigDecimal){
        if (bigDecimal == null){
            return 0.0;
        }
        return bigDecimal.doubleValue();
    }
}
