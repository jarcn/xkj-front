package com.cwlrdc.front.common;

/**
 * Created by chenjia on 2017/4/26.
 */
public class ExcelException extends Exception {

    public ExcelException() {
    }

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
