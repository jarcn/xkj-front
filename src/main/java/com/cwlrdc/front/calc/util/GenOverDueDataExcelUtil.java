package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.ltto.entity.LttoCancelWinStatData;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.unlto.twls.commonutil.component.CommonUtils;
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
 * 弃奖情况汇总数据Excel导出
 * Created by chenjia on 2017/8/21.
 */
public class GenOverDueDataExcelUtil {

    public static HSSFWorkbook list2Excel(List<LttoCancelWinStatData> list, String gameCode, String periodNum, ProvinceInfoCache provinceInfoCache, GameInfoCache gameInfoCache) {
        String[] heads = new String[]{"省码", "省名", "弃奖额", "一等奖", "二等奖", "三等奖", "四等奖", "五等奖", "六等奖", "七等奖", "八等奖", "九等奖", "十等奖"};
        //创建excel工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建第一个sheet，命名为 new sheet
        Sheet sheet = wb.createSheet("弃奖统计");
        sheet.setAutobreaks(true);
        HSSFPrintSetup ps = (HSSFPrintSetup) sheet.getPrintSetup();
        ps.setLandscape(true); // 打印方向，true：横向，false：纵向
        ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); //纸张
        sheet.setMargin(HSSFSheet.BottomMargin, (double) 0.5);// 页边距（下）
        sheet.setMargin(HSSFSheet.LeftMargin, (double) 0.1);// 页边距（左）
        sheet.setMargin(HSSFSheet.RightMargin, (double) 0.1);// 页边距（右）
        sheet.setMargin(HSSFSheet.TopMargin, (double) 0.5);// 页边距（上）
        sheet.setHorizontallyCenter(true);//设置打印页面为水平居中
        sheet.setVerticallyCenter(true);//设置打印页面为垂直居中
        Row fileTitile = sheet.createRow(0);
        for (int i = 0; i < heads.length; i++) {
            Cell cell = fileTitile.createCell(i);
            cell.setCellStyle(CellStyleUtils.overDueTitle(wb));
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, heads.length - 1));  //合并第一行的第一列到第13列
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("中国福利彩票\"").append(gameInfoCache.getGameName(gameCode)).append("\"弃奖情况报告单第").append(periodNum + "期");
        fileTitile.getCell(0).setCellValue(stringBuffer.toString());

        Row tableHead = sheet.createRow(1);
        for (int i = 0; i < heads.length; i++) {
            sheet.autoSizeColumn(i);
            Cell cell = tableHead.createCell(i);
            cell.setCellStyle(CellStyleUtils.overDueThead(wb));
            cell.setCellValue(heads[i]);
        }

        Long allCancelMoney = 0L, cancel1Money = 0L, cancel2Money = 0L, cancel3Money = 0L,
                cancel4Money = 0L, cancel5Money = 0L, cancel6Money = 0L, cancel7Money = 0L,
                cancel8Money = 0L, cancel9Money = 0L, cancel10Money = 0L;

        //拼接数据表格
        if (!CommonUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                LttoCancelWinStatData bean = list.get(i);
                Row dataRow = sheet.createRow(i + 2);
                for (int j = 0; j < heads.length; j++) {
                    Cell dataCell = dataRow.createCell(j);
                    dataCell.setCellStyle(CellStyleUtils.saleRankTbody(wb));
                }
                dataRow.getCell(0).setCellValue(bean.getProvinceId());
                dataRow.getCell(1).setCellStyle(CellStyleUtils.cellStyle(wb));
                dataRow.getCell(1).setCellValue(provinceInfoCache.getProvinceName(bean.getProvinceId()));
                dataRow.getCell(2).setCellValue(String.valueOf(bean.getAllCanceledMoney().longValue()));
                dataRow.getCell(3).setCellValue(String.valueOf(bean.getCanceled1Count().longValue()));
                dataRow.getCell(4).setCellValue(String.valueOf(bean.getCanceled2Count().longValue()));
                dataRow.getCell(5).setCellValue(String.valueOf(bean.getCanceled3Count().longValue()));
                dataRow.getCell(6).setCellValue(String.valueOf(bean.getCanceled4Count().longValue()));
                dataRow.getCell(7).setCellValue(String.valueOf(bean.getCanceled5Count().longValue()));
                dataRow.getCell(8).setCellValue(String.valueOf(bean.getCanceled6Count().longValue()));
                dataRow.getCell(9).setCellValue(String.valueOf(bean.getCanceled7Count().longValue()));
                dataRow.getCell(10).setCellValue(String.valueOf(bean.getCanceled8Count().longValue()));
                dataRow.getCell(11).setCellValue(String.valueOf(bean.getCanceled9Count().longValue()));
                dataRow.getCell(12).setCellValue(String.valueOf(bean.getCanceled10Count().longValue()));

                allCancelMoney += bean.getAllCanceledMoney().longValue();
                cancel1Money += bean.getCanceled1Count().longValue();
                cancel2Money += bean.getCanceled2Count().longValue();
                cancel3Money += bean.getCanceled3Count().longValue();
                cancel4Money += bean.getCanceled4Count().longValue();
                cancel5Money += bean.getCanceled5Count().longValue();
                cancel6Money += bean.getCanceled6Count().longValue();
                cancel7Money += bean.getCanceled7Count().longValue();
                cancel8Money += bean.getCanceled8Count().longValue();
                cancel9Money += bean.getCanceled9Count().longValue();
                cancel10Money += bean.getCanceled10Count().longValue();
            }
        }

        Row lastRow = sheet.createRow(list.size() + 2);
        for (int i = 0; i < heads.length; i++) {
            Cell cell = lastRow.createCell(i);
            cell.setCellStyle(CellStyleUtils.overDueThead(wb));
        }
        lastRow.getCell(1).setCellValue(list.size());
        lastRow.getCell(2).setCellValue(String.valueOf(allCancelMoney));
        lastRow.getCell(3).setCellValue(String.valueOf(cancel1Money));
        lastRow.getCell(4).setCellValue(String.valueOf(cancel2Money));
        lastRow.getCell(5).setCellValue(String.valueOf(cancel3Money));
        lastRow.getCell(6).setCellValue(String.valueOf(cancel4Money));
        lastRow.getCell(7).setCellValue(String.valueOf(cancel5Money));
        lastRow.getCell(8).setCellValue(String.valueOf(cancel6Money));
        lastRow.getCell(9).setCellValue(String.valueOf(cancel7Money));
        lastRow.getCell(10).setCellValue(String.valueOf(cancel8Money));
        lastRow.getCell(11).setCellValue(String.valueOf(cancel9Money));
        lastRow.getCell(12).setCellValue(String.valueOf(cancel10Money));


        Row footRow = sheet.createRow(list.size() + 3);
        HSSFCellStyle style = CellStyleUtils.overDueBootNoBorder(wb);
        footRow.createCell(1).setCellStyle(style);
        footRow.getCell(1).setCellValue("操作员");
        footRow.createCell(5).setCellStyle(style);
        footRow.getCell(5).setCellValue("核对员");
        footRow.createCell(8).setCellStyle(style);
        footRow.getCell(8).setCellValue("中彩中心技术管理部");
        footRow.createCell(11).setCellStyle(style);
        footRow.getCell(11).setCellValue(DateFormatUtils.format(System.currentTimeMillis(), "yyyy年MM月dd日"));

        return wb;
    }

}
