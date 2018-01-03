package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 导出期信息Excel
 * Created by chenjia on 2017/7/29.
 */
@Component
public class GenPeriodInfoExcelUtil {

    //生成周期库excel报表
    public static HSSFWorkbook genPeriodInfoExcel(List<ParaGamePeriodInfo> periodInfoList,String headTile){

        //创建excel工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("游戏周期库");

        //表头
        String[] theads = {"序号","游戏", "期号","省码", "促销状态","期开日期", "期长", "期结日期","月","周" ,"年","兑奖期长", "兑奖截止日期","开奖日期", "弃奖期详情","开奖状态"};
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));  //合并第一行的第一列到第9列
        Row fristRow = sheet.createRow(0);
        Cell headCell = fristRow.createCell(0);
        headCell.setCellType(HSSFCell.CELL_TYPE_STRING);
        headCell.setCellStyle(CellStyleUtils.headCellStyle(wb));
        headCell.setCellValue(headTile); //文件头标题

        sheet.setColumnWidth(5, 4700);
        sheet.setColumnWidth(7, 4700);
        sheet.setColumnWidth(12, 4700);
        sheet.setColumnWidth(13, 3000);
        sheet.setColumnWidth(14, 3000);

        //表标题
        Row secondRow = sheet.createRow(1);
        for(int i=0;i<theads.length;i++){
            Cell tileCell = secondRow.createCell(i);
            tileCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            tileCell.setCellStyle(CellStyleUtils.cellStyle(wb));
            tileCell.setCellValue(theads[i]);
        }

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font ztFont = wb.createFont();
        ztFont.setColor(Font.COLOR_NORMAL);
        ztFont.setFontHeightInPoints((short) 10);
        ztFont.setFontName("宋体");
        cellStyle.setFont(ztFont);

        //期号内容
        for(int j=0;j<periodInfoList.size();j++) {
            ParaGamePeriodInfo periodInfo = periodInfoList.get(j);
            Row infoRow = sheet.createRow(j+2);

            Cell infoCell0 = infoRow.createCell(0);
            infoCell0.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell0.setCellStyle(cellStyle);
            infoCell0.setCellValue(j+1);

            Cell infoCell1 = infoRow.createCell(1);
            infoCell1.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell1.setCellStyle(cellStyle);
            infoCell1.setCellValue(converString(periodInfo.getGameCode()));

            Cell infoCell2 = infoRow.createCell(2);
            infoCell2.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell2.setCellStyle(cellStyle);
            infoCell2.setCellValue(converString(periodInfo.getPeriodNum()));

            Cell infoCell3 = infoRow.createCell(3);
            infoCell3.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell3.setCellStyle(cellStyle);
            infoCell3.setCellValue(converString(periodInfo.getProvinceId()));

            Cell infoCell4 = infoRow.createCell(4);
            infoCell4.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell4.setCellStyle(cellStyle);
            infoCell4.setCellValue(converString(periodInfo.getPromotionStatus()));

            Cell infoCell5 = infoRow.createCell(5);
            infoCell5.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell5.setCellStyle(cellStyle);
            infoCell5.setCellValue(converString(periodInfo.getPeriodBeginTime()+" 20:00:00"));

            Cell infoCell6 = infoRow.createCell(6);
            infoCell6.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell6.setCellStyle(cellStyle);
            infoCell6.setCellValue(converString(periodInfo.getPeriodCycle()));

            Cell infoCell7 = infoRow.createCell(7);
            infoCell7.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell7.setCellStyle(cellStyle);
            infoCell7.setCellValue(converString(periodInfo.getPeriodEndTime()+" 19:00:00"));

            Cell infoCell8 = infoRow.createCell(8);
            infoCell8.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell8.setCellStyle(cellStyle);
            infoCell8.setCellValue(converString(periodInfo.getPeriodMonth()));

            Cell infoCell9 = infoRow.createCell(9);
            infoCell9.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell9.setCellStyle(cellStyle);
            infoCell9.setCellValue(converString(periodInfo.getPeriodWeek()));

            Cell infoCell10 = infoRow.createCell(10);
            infoCell10.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell10.setCellStyle(cellStyle);
            infoCell10.setCellValue(converString(periodInfo.getPeriodYear()));

            Cell infoCell11 = infoRow.createCell(11);
            infoCell11.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell11.setCellStyle(cellStyle);
            infoCell11.setCellValue(converString(periodInfo.getCashTerm()));

            Cell infoCell12 = infoRow.createCell(12);
            infoCell12.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell12.setCellStyle(cellStyle);
            infoCell12.setCellValue(converString(periodInfo.getCashEndTime()+" 23:59:59"));

            Cell infoCell13 = infoRow.createCell(13);
            infoCell13.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell13.setCellStyle(cellStyle);
            infoCell13.setCellValue(converString(periodInfo.getLotteryDate()));

            Cell infoCell14 = infoRow.createCell(14);
            infoCell14.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell14.setCellStyle(cellStyle);
            infoCell14.setCellValue(converString(periodInfo.getOverduePeriod()));

            Cell infoCell15 = infoRow.createCell(15);
            infoCell15.setCellType(HSSFCell.CELL_TYPE_STRING);
            infoCell15.setCellStyle(cellStyle);
            infoCell15.setCellValue(periodInfo.getStatus());

        }

        Row bootRow = sheet.createRow(periodInfoList.size()+3);
        Cell bootCell = bootRow.createCell(5);
        bootCell.setCellStyle(CellStyleUtils.noBorderCenter(wb));
        bootCell.setCellValue("市场二部领导签字：");

        Cell bootTime = bootRow.createCell(7);
        bootTime.setCellStyle(CellStyleUtils.noBorderCenter(wb));
        sheet.addMergedRegion(new CellRangeAddress(periodInfoList.size()+3, periodInfoList.size()+3, 7, 15));  //合并第一行的第一列到第9列
        bootTime.setCellValue("          年     月     日");

        return wb;
    }


    private static  String converString(Object val){
        if(null != val){
            return val.toString();
        }
        return "";
    }

}
