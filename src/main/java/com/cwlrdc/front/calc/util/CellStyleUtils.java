package com.cwlrdc.front.calc.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

/**
 * Created by chenjia on 2017/4/26.
 */
public class CellStyleUtils {

    //单元格样式
    public static HSSFCellStyle cellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFDataFormat textformat = wb.createDataFormat();
        cellStyle.setDataFormat(textformat.getFormat("@"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    public static HSSFCellStyle cellStyleBold(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFDataFormat textformat = wb.createDataFormat();
        cellStyle.setDataFormat(textformat.getFormat("@"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 14);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    public static HSSFCellStyle cellStyleBigBold(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFDataFormat textformat = wb.createDataFormat();
        cellStyle.setDataFormat(textformat.getFormat("@"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("Arial");
        font2.setFontHeightInPoints((short) 18);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }


    //单元格样式
    public static HSSFCellStyle cellStyleNoBorder(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("微软雅黑");
        font2.setFontHeightInPoints((short) 14);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    public static HSSFCellStyle annceMentNoBorder(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 16);
        cellStyle.setFont(font2);
        return cellStyle;
    }


    public static HSSFCellStyle anncePoolAmont(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("Arial");
        font2.setFontHeightInPoints((short) 14);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    public static HSSFCellStyle infoStyleNoBorder(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true); //设置自动换行;
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 靠左
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //设置垂直对齐的样式为居中对齐;
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    //单元格样式
    public static HSSFCellStyle noBorderCenter(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 14);
        cellStyle.setFont(font2);
        return cellStyle;
    }


    //单元格样式
    public static HSSFCellStyle noBorderRight(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); //靠右
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 14);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    public static HSSFCellStyle noBorderLeft(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); //靠左
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 14);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    public static HSSFCellStyle noBorder(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); //靠左
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //设置垂直对齐的样式为居中对齐;
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font2.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font2);
        return cellStyle;
    }


    //表头字体样式
    public static HSSFCellStyle headCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle ztStyle = wb.createCellStyle();
        ztStyle.setAlignment(HSSFCellStyle.VERTICAL_CENTER);
        ztStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font ztFont = wb.createFont();
        ztFont.setColor(Font.COLOR_NORMAL);
        ztFont.setFontHeightInPoints((short) 22);
        ztFont.setFontName("微软雅黑");
        ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        ztStyle.setFont(ztFont);
        return ztStyle;
    }

    public static HSSFCellStyle noticeTilteCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle ztStyle = wb.createCellStyle();
        ztStyle.setAlignment(HSSFCellStyle.VERTICAL_CENTER);
        ztStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font ztFont = wb.createFont();
        ztFont.setColor(Font.COLOR_NORMAL);
        ztFont.setFontHeightInPoints((short) 18);
        ztFont.setFontName("黑体");
        ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        ztStyle.setFont(ztFont);
        return ztStyle;
    }


    //表头字体样式 不加粗
    public static HSSFCellStyle headCellStyleNoBold(HSSFWorkbook wb) {
        HSSFCellStyle ztStyle = wb.createCellStyle();
        ztStyle.setAlignment(HSSFCellStyle.VERTICAL_CENTER);
        ztStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font ztFont = wb.createFont();
        ztFont.setColor(Font.COLOR_NORMAL);
        ztFont.setFontHeightInPoints((short) 22);
        ztFont.setFontName("黑体");
        ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        ztStyle.setFont(ztFont);
        return ztStyle;
    }


    public static HSSFCellStyle borderCenterBold(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        Font ztFont = wb.createFont();
        ztFont.setColor(Font.COLOR_NORMAL);
        ztFont.setFontHeightInPoints((short)12);
        ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        ztFont.setFontName("宋体");
        cellStyle.setFont(ztFont);
        return cellStyle;
    }


    //弃奖汇总数据excel表头样式
    public static HSSFCellStyle overDueTitle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFDataFormat textformat = wb.createDataFormat();
        cellStyle.setDataFormat(textformat.getFormat("@"));
//        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
//        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
//        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
//        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("黑体");
        font2.setFontHeightInPoints((short) 20);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    public static HSSFCellStyle overDueThead(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        HSSFDataFormat textformat = wb.createDataFormat();
        cellStyle.setDataFormat(textformat.getFormat("@"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 居右
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    //弃奖excel签名字体样式
    public static HSSFCellStyle overDueBootNoBorder(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 靠左
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font2);
        return cellStyle;
    }



    //销售排名表，head格式
    public static HSSFCellStyle saleRankThead(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        Font ztFont = wb.createFont();
        ztFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        ztFont.setFontHeightInPoints((short)10);
        ztFont.setFontName("宋体");
        cellStyle.setFont(ztFont);
        return cellStyle;
    }


    //销售排名表，body格式
    public static HSSFCellStyle saleRankTbody(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 居右
        Font ztFont = wb.createFont();
        ztFont.setFontHeightInPoints((short)10);
        ztFont.setFontName("宋体");
        cellStyle.setFont(ztFont);
        return cellStyle;
    }


    //销售排名表，boot格式
    public static HSSFCellStyle saleRankNoBorderLeft(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); //靠左
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 10);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }


    //中奖情况报告单标题样式
    public static HSSFCellStyle winDataTilteStyle(HSSFWorkbook wb) {
        HSSFCellStyle ztStyle = wb.createCellStyle();
        ztStyle.setAlignment(HSSFCellStyle.VERTICAL_CENTER);
        ztStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font ztFont = wb.createFont();
        ztFont.setColor(Font.COLOR_NORMAL);
        ztFont.setFontHeightInPoints((short) 18);
        ztFont.setFontName("宋体");
        ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        ztStyle.setFont(ztFont);
        return ztStyle;
    }

    //中奖情况报告单 单位字体
    public static HSSFCellStyle winDataUnitStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    //销售情况页脚落款字体
    public static HSSFCellStyle saleDataUnitStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    //销售排名表，body格式
    public static HSSFCellStyle winDataThead(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居右
        Font ztFont = wb.createFont();
        ztFont.setFontName("宋体");
        ztFont.setFontHeightInPoints((short)10);
        ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(ztFont);
        return cellStyle;
    }

    //中奖明细表，合计行格式
    public static HSSFCellStyle winDataBoot(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 居右
        Font ztFont = wb.createFont();
        ztFont.setFontName("宋体");
        ztFont.setFontHeightInPoints((short)10);
        ztFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(ztFont);
        return cellStyle;
    }

    //销售排名表，body格式
    public static HSSFCellStyle winDataTbody(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 居右
        Font ztFont = wb.createFont();
        ztFont.setFontName("宋体");
        ztFont.setFontHeightInPoints((short)10);
        cellStyle.setFont(ztFont);
        return cellStyle;
    }

    //中奖明细省码格式，body格式
    public static HSSFCellStyle winDataProvinceName(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居右
        Font ztFont = wb.createFont();
        ztFont.setFontName("宋体");
        ztFont.setFontHeightInPoints((short)10);
        cellStyle.setFont(ztFont);
        return cellStyle;
    }


    //摇奖通知单
    public static HSSFCellStyle noticeUnitStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 居中
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font2);
        return cellStyle;
    }

    //摇奖通知单
    public static HSSFCellStyle moneyUnitStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 居中
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        HSSFFont font2 = wb.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font2);
        return cellStyle;
    }

}
