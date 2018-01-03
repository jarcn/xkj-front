package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.front.common.GameInfoCache;
import com.cwlrdc.front.common.ProvinceInfoCache;
import com.unlto.twls.commonutil.component.CommonUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

import static com.cwlrdc.front.calc.util.CellStyleUtils.cellStyle;

/**
 * 生成摇奖通知单Excel
 * Created by chenjia on 2017/8/21.
 */
public class GenPrintNoticeExcelUtil {

    public static HSSFWorkbook list2Excel(List<LttoProvinceSalesData> list, String gameCode, String periodNum, GameInfoCache gameInfoCache, ProvinceInfoCache provinceInfoCache) {
        String[] heads = {"玩法", "当前期号", "省码", "省名称", "销售总额"};
        //创建excel工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建第一个sheet，命名为 new sheet
        Sheet sheet = wb.createSheet("销量统计");
        sheet.setAutobreaks(true);
        HSSFPrintSetup ps = (HSSFPrintSetup) sheet.getPrintSetup();
        ps.setLandscape(false); // 打印方向，true：横向，false：纵向
        ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); //纸张
        sheet.setMargin(HSSFSheet.BottomMargin,( double ) 0.5 );// 页边距（下）
        sheet.setMargin(HSSFSheet.LeftMargin,( double ) 0.1 );// 页边距（左）
        sheet.setMargin(HSSFSheet.RightMargin,( double ) 0.1 );// 页边距（右）
        sheet.setMargin(HSSFSheet.TopMargin,( double ) 0.5 );// 页边距（上）
        sheet.setHorizontallyCenter(true);//设置打印页面为水平居中
        sheet.setVerticallyCenter(true);//设置打印页面为垂直居中
        for (int i = 0; i < 100; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 10; j++) {
                sheet.setColumnWidth(j, 15 * 250);
                sheet.setDefaultRowHeight((short) 18); //TODO
                row.createCell(j);
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));  //合并第一行的第一列到第七列
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));  //合并第一行的第一列到第七列
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));  //合并第一行的第一列到第七列

        String gameName = gameInfoCache.getGameName(gameCode); //TODO 转换名称
        sheet.getRow(0).getCell(0).setCellValue("中国福利彩票\"" + gameName + "\"");
        sheet.getRow(1).getCell(0).setCellValue("摇 奖 通 知 单");
        sheet.getRow(2).getCell(0).setCellValue("第" + periodNum + "期");
        for (int i = 0; i < 3; i++) {
            sheet.getRow(i).getCell(0).setCellStyle(CellStyleUtils.noticeTilteCellStyle(wb));// 样式应用到该单元格上
        }

        //第四行创建表头
        Row head = sheet.getRow(3);
        for (int i = 0; i < heads.length; i++) {
            Cell cell = head.createCell(i + 1);
            cell.setCellStyle(cellStyle(wb));
            cell.setCellValue(heads[i]);
        }

        Long toalMoney = 0L;
        if (!CommonUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                LttoProvinceSalesData bean = list.get(i);
                Row bodyData = sheet.getRow(i + 4);

                Cell cell1 = bodyData.getCell(1);
                cell1.setCellStyle(cellStyle(wb));
                cell1.setCellValue(gameInfoCache.getGameName(bean.getGameCode()));

                Cell cell2 = bodyData.getCell(2);
                cell2.setCellStyle(cellStyle(wb));
                cell2.setCellValue(bean.getPeriodNum());

                Cell cell3 = bodyData.getCell(3);
                cell3.setCellStyle(cellStyle(wb));
                cell3.setCellValue(bean.getProvinceId());

                Cell cell4 = bodyData.getCell(4);
                cell4.setCellStyle(cellStyle(wb));
                cell4.setCellValue(provinceInfoCache.getProvinceName(bean.getProvinceId()));

                Cell cell5 = bodyData.getCell(5);
                cell5.setCellStyle(CellStyleUtils.moneyUnitStyle(wb));
                cell5.setCellValue(String.valueOf(bean.getAmount().longValue()).isEmpty()?"0":String.valueOf(bean.getAmount().longValue()));

                toalMoney += bean.getAmount().longValue();
            }
        }
        int rowNum = 4 + list.size();
        Row bootRow = sheet.getRow(rowNum);
        bootRow.getCell(1).setCellStyle(cellStyle(wb));
        bootRow.getCell(1).setCellValue("合计");
        bootRow.getCell(4).setCellStyle(CellStyleUtils.moneyUnitStyle(wb));
        bootRow.getCell(2).setCellStyle(CellStyleUtils.moneyUnitStyle(wb));
        bootRow.getCell(3).setCellStyle(CellStyleUtils.moneyUnitStyle(wb));
        bootRow.getCell(4).setCellValue(String.valueOf(list.size()));
        bootRow.getCell(5).setCellStyle(CellStyleUtils.moneyUnitStyle(wb));
        bootRow.getCell(5).setCellValue(String.valueOf(toalMoney));


        Row noticeRow1 = sheet.getRow(5 + list.size());
        Cell noticenoCell1 = noticeRow1.createCell(1);
        noticenoCell1.setCellStyle(CellStyleUtils.noticeUnitStyle(wb));
        sheet.addMergedRegion(new CellRangeAddress(5 + list.size(), 5 + list.size(), 1, 5));  //合并第一行的第一列到第七列
        noticenoCell1.setCellValue("        本期\""+gameName+"\"电脑福利彩票数据已汇总完毕，投注总额为：");
        Row noticeRow2 = sheet.getRow(6 + list.size());
        Cell noticenoCell0 = noticeRow2.createCell(1);
        noticenoCell0.setCellStyle(CellStyleUtils.anncePoolAmont(wb));
        noticenoCell0.setCellValue(String.valueOf(toalMoney));
        Cell noticenoCell2 = noticeRow2.createCell(2);
        noticenoCell2.setCellStyle(CellStyleUtils.noticeUnitStyle(wb));
        sheet.addMergedRegion(new CellRangeAddress(6 + list.size(), 6 + list.size(), 2, 5));  //合并第一行的第一列到第七列
        noticenoCell2.setCellValue("元，经检查，数据完整、真实，可以开始摇奖。");

        Row date = sheet.getRow(8 + list.size());
        date.getCell(2).setCellStyle(CellStyleUtils.saleDataUnitStyle(wb));
        date.getCell(2).setCellValue("操作员：");
        date.getCell(4).setCellStyle(CellStyleUtils.saleDataUnitStyle(wb));
        date.getCell(4).setCellValue("核对员：");


        Row date1 = sheet.getRow(10 + list.size());
        date1.getCell(5).setCellStyle(CellStyleUtils.saleDataUnitStyle(wb));
        date1.getCell(5).setCellValue("中彩中心技术管理部");
        Row date2 = sheet.getRow(11 + list.size());
        date2.getCell(5).setCellStyle(CellStyleUtils.saleDataUnitStyle(wb));
        date2.getCell(5).setCellValue(DateFormatUtils.format(System.currentTimeMillis(), "yyyy年MM月dd日"));

        return wb;
    }


}
