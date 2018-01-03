package com.cwlrdc.front.calc.util;

import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

@Slf4j
public class Base64Encode {
    public static String base64EncodeFileName(String fileName){
        Base64 base64 = new Base64();
        try {
            return "=?UTF-8?B?"
                + new String(base64.encode(fileName.getBytes("UTF-8")))
                + "?=";
        } catch (UnsupportedEncodingException e) {
            log.error("异常",e);
            throw new RuntimeException("未知编码异常");
        }
    }
}
