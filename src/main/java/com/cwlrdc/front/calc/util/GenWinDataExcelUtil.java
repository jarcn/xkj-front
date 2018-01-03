package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.ProvinceInfoCache;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 中奖数据收集excel导出
 * Created by chenjia on 2017/8/21.
 */
public class GenWinDataExcelUtil {
    //中奖情况报告单
    public static HSSFWorkbook list2Excel(List<LttoWinstatData> list, String gameCode, String periodNum,GameInfoCache gameInfoCache,ProvinceInfoCache provinceInfoCache) {
        String[] heads = {"当前期号", "省码", "省名", "一等奖", "二等奖", "三等奖", "四等奖", "五等奖", "六等奖", "七等奖", "八等奖", "九等奖", "十等奖"};
        //创建excel工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建第一个sheet，命名为 new sheet
        Sheet sheet = wb.createSheet("中奖情况报告单");
        sheet.setAutobreaks(true);
        HSSFPrintSetup ps = (HSSFPrintSetup) sheet.getPrintSetup();
        ps.setLandscape(true); // 打印方向，true：横向，false：纵向
        ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); //纸张
        sheet.setMargin(HSSFSheet.BottomMargin,(double) 0.5 );// 页边距（下）
        sheet.setMargin(HSSFSheet.LeftMargin,(double) 0.1 );// 页边距（左）
        sheet.setMargin(HSSFSheet.RightMargin,( double ) 0.1 );// 页边距（右）
        sheet.setMargin(HSSFSheet.TopMargin,( double ) 0.5 );// 页边距（上）
        sheet.setHorizontallyCenter(true);//设置打印页面为水平居中
        sheet.setVerticallyCenter(true);//设置打印页面为垂直居中
        Row fileTitile = sheet.createRow(0);
        for (int i = 0; i < heads.length; i++) {
            sheet.autoSizeColumn(i);
            Cell cell = fileTitile.createCell(i);
            cell.setCellStyle(CellStyleUtils.winDataTilteStyle(wb));
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));  //合并第一行的第一列到第13列
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("中国福利彩票\"").append(gameInfoCache.getGameName(gameCode)).append("\"中奖情况报告单第").append(periodNum + "期");
        fileTitile.getCell(0).setCellValue(stringBuilder.toString());

        Row unitRow = sheet.createRow(1);
        for (int i = 0; i < heads.length; i++) {
            Cell cell = unitRow.createCell(i);
            cell.setCellStyle(CellStyleUtils.winDataUnitStyle(wb));
        }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 8, 12));
        unitRow.getCell(8).setCellValue("单位:个数");

        Row tableHead = sheet.createRow(4);
        for (int i = 0; i < heads.length; i++) {
            Cell cell = tableHead.createCell(i);
            cell.setCellStyle(CellStyleUtils.winDataThead(wb));
            cell.setCellValue(heads[i]);
        }

        Integer win1Count = 0, win2Count = 0, win3Count = 0, win4Count = 0,
                win5Count = 0, win6Count = 0, win7Count = 0, win8Count = 0,
                win9Count = 0, win10Count = 0;

        Cell dataCell = null;
        //拼接数据表格
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                LttoWinstatData bean = list.get(i);
                Row dataRow = sheet.createRow(i + 5);
                for (int j = 0; j < heads.length; j++) {
                    dataCell = dataRow.createCell(j);
                    if(j == 2){
                        dataCell.setCellStyle(CellStyleUtils.winDataProvinceName(wb));
                    }else{
                        dataCell.setCellStyle(CellStyleUtils.winDataTbody(wb));
                    }
                }
                dataRow.getCell(0).setCellValue(bean.getPeriodNum());
                dataRow.getCell(1).setCellValue(bean.getProvinceId());
                dataRow.getCell(2).setCellValue(provinceInfoCache.getProvinceName(bean.getProvinceId()));
                dataRow.getCell(3).setCellValue(String.valueOf(bean.getPrize1Count()));
                dataRow.getCell(4).setCellValue(String.valueOf(bean.getPrize2Count()));
                dataRow.getCell(5).setCellValue(String.valueOf(bean.getPrize3Count()));
                dataRow.getCell(6).setCellValue(String.valueOf(bean.getPrize4Count()));
                dataRow.getCell(7).setCellValue(String.valueOf(bean.getPrize5Count()));
                dataRow.getCell(8).setCellValue(String.valueOf(bean.getPrize6Count()));
                dataRow.getCell(9).setCellValue(String.valueOf(bean.getPrize7Count()));
                dataRow.getCell(10).setCellValue(String.valueOf(bean.getPrize8Count()));
                dataRow.getCell(11).setCellValue(String.valueOf(bean.getPrize9Count()));
                dataRow.getCell(12).setCellValue(String.valueOf(bean.getPrize10Count()));

                win1Count += bean.getPrize1Count().intValue();
                win2Count += bean.getPrize2Count().intValue();
                win3Count += bean.getPrize3Count().intValue();
                win4Count += bean.getPrize4Count().intValue();
                win5Count += bean.getPrize5Count().intValue();
                win6Count += bean.getPrize6Count().intValue();
                win7Count += bean.getPrize7Count().intValue();
                win8Count += bean.getPrize8Count().intValue();
                win9Count += bean.getPrize9Count().intValue();
                win10Count += bean.getPrize10Count().intValue();
            }
        }

        Row footRow = sheet.createRow(list.size() + 5);
        for (int i = 0; i < heads.length; i++) {
            Cell cell = footRow.createCell(i);
            cell.setCellStyle(CellStyleUtils.winDataBoot(wb));
        }
        footRow.getCell(2).setCellValue(list.size());
        footRow.getCell(3).setCellValue(win1Count);
        footRow.getCell(4).setCellValue(win2Count);
        footRow.getCell(5).setCellValue(win3Count);
        footRow.getCell(6).setCellValue(win4Count);
        footRow.getCell(7).setCellValue(win5Count);
        footRow.getCell(8).setCellValue(win6Count);
        footRow.getCell(9).setCellValue(win7Count);
        footRow.getCell(10).setCellValue(win8Count);
        footRow.getCell(11).setCellValue(win9Count);
        footRow.getCell(12).setCellValue(win10Count);

        Row infoRow = sheet.createRow(list.size() + 7);
        HSSFCellStyle style = CellStyleUtils.saleDataUnitStyle(wb);
        infoRow.createCell(4).setCellStyle(style);
        infoRow.getCell(4).setCellValue("操作员:");
        infoRow.createCell(9).setCellStyle(style);
        infoRow.getCell(9).setCellValue("核对员:");


        Row partInfo = sheet.createRow(list.size() + 9);
        partInfo.createCell(11).setCellStyle(style);
        partInfo.getCell(11).setCellValue("中彩中心技术管理部");

        Row timeFoot = sheet.createRow(list.size() + 10);
        timeFoot.createCell(11).setCellStyle(CellStyleUtils.saleDataUnitStyle(wb));
        timeFoot.getCell(11).setCellValue(DateFormatUtils.format(System.currentTimeMillis(), "yyyy年MM月dd日"));

        return wb;
    }
}
