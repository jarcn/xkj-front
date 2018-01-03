package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.ltto.entity.LttoProvinceSalesData;
import com.cwlrdc.commondb.ltto.entity.LttoWinstatData;
import com.cwlrdc.front.common.ProvinceInfoCache;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.List;

/**
 * 销售排名表（只有双色球有销售排名表）
 * Created by chenjia on 2017/8/21.
 */
public class GenSalesRankExcelUtil {

    //生成销售数据排序excel
    public static HSSFWorkbook createExcel(List<LttoProvinceSalesData> salelist, HashMap<String, String> resultMap,
                                     HashMap<String, LttoWinstatData> winMap, LttoLotteryAnnouncement announcement,ProvinceInfoCache provinceInfoCache) {
        HSSFWorkbook wb = new HSSFWorkbook();
        String[] thead = {"排名", "省市", "一等奖年累计", "二等奖年累计", "销售量(元)", "一等奖", "二等奖", "三等奖", "四等奖", "五等奖", "六等奖", "七等奖", "八等奖", "九等奖", "十等奖"};
        Sheet st = wb.createSheet();
        HSSFPrintSetup ps = (HSSFPrintSetup) st.getPrintSetup();
        ps.setLandscape(true); // 打印方向，true：横向，false：纵向
        ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); //纸张
        st.setMargin(HSSFSheet.BottomMargin,( double ) 0.5 );// 页边距（下）
        st.setMargin(HSSFSheet.LeftMargin,( double ) 0.1 );// 页边距（左）
        st.setMargin(HSSFSheet.RightMargin,( double ) 0.1 );// 页边距（右）
        st.setMargin(HSSFSheet.TopMargin,( double ) 0.5 );// 页边距（上）
        st.setHorizontallyCenter(true);//设置打印页面为水平居中
        st.setVerticallyCenter(true);//设置打印页面为垂直居中
        //表头
        Row headrow = st.createRow(1);
        for (int i = 0; i < thead.length; i++) {
            if(i>1){
                st.setColumnWidth(i,3000);
            }else{
                st.setColumnWidth(i,2000);
            }
            Cell headCell = headrow.createCell(i);
            headCell.setCellStyle(CellStyleUtils.saleRankThead(wb));
            headCell.setCellValue(thead[i]);
        }
        //合计
        Long t1 = 0L, t2 = 0L, t3 = 0L, t4 = 0L, t5 = 0L, t6 = 0L, t7 = 0L, t8 = 0L, t9 = 0L, t10 = 0L, t11 = 0L, t12 = 0L, t13 = 0L;
        //表数据
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
        for (int i = 0; i < salelist.size(); i++) {
            LttoProvinceSalesData salesData = salelist.get(i);
            Row dataRow = st.createRow(i + 2);
            for (int j = 0; j < thead.length; j++) {
                dataRow.createCell(j).setCellStyle(cellStyle);
            }
            String provinceId = salesData.getProvinceId();
            String[] prizeCounts = resultMap.get(provinceId).split(",");
            LttoWinstatData winstatData = winMap.get(provinceId);
            dataRow.getCell(0).setCellValue(i + 1);
            dataRow.getCell(1).setCellValue(provinceInfoCache.getProvinceName(provinceId));
            dataRow.getCell(2).setCellValue(prizeCounts[0]);
            dataRow.getCell(3).setCellValue(prizeCounts[1]);
            dataRow.getCell(4).setCellValue(String.valueOf(salesData.getAmount().longValue()));
            dataRow.getCell(5).setCellValue(String.valueOf(winstatData.getPrize1Count()));
            dataRow.getCell(6).setCellValue(String.valueOf(winstatData.getPrize2Count()));
            dataRow.getCell(7).setCellValue(String.valueOf(winstatData.getPrize3Count()));
            dataRow.getCell(8).setCellValue(String.valueOf(winstatData.getPrize4Count()));
            dataRow.getCell(9).setCellValue(String.valueOf(winstatData.getPrize5Count()));
            dataRow.getCell(10).setCellValue(String.valueOf(winstatData.getPrize6Count()));
            dataRow.getCell(11).setCellValue(String.valueOf(winstatData.getPrize7Count()));
            dataRow.getCell(12).setCellValue(String.valueOf(winstatData.getPrize8Count()));
            dataRow.getCell(13).setCellValue(String.valueOf(winstatData.getPrize9Count()));
            dataRow.getCell(14).setCellValue(String.valueOf(winstatData.getPrize10Count()));
            t1 += Long.parseLong(prizeCounts[0]); //一等奖年累计
            t2 += Long.parseLong(prizeCounts[1]); //二等奖年累计
            t3 += Long.valueOf(salesData.getAmount().longValue()); //本期销售总额

            t4 += winstatData.getPrize1Count(); //本期一等奖
            t5 += winstatData.getPrize2Count();
            t6 += winstatData.getPrize3Count();
            t7 += winstatData.getPrize4Count();
            t8 += winstatData.getPrize5Count();
            t9 += winstatData.getPrize6Count();
            t10 += winstatData.getPrize7Count();
            t11 += winstatData.getPrize8Count();
            t12 += winstatData.getPrize9Count();
            t13 += winstatData.getPrize10Count();
        }
        Row totalRow = st.createRow(salelist.size() + 2);
        for (int i = 0; i < thead.length; i++) {
            totalRow.createCell(i).setCellStyle(CellStyleUtils.saleRankThead(wb));
        }
        totalRow.getCell(1).setCellValue("合计");
        totalRow.getCell(2).setCellValue(String.valueOf(t1));
        totalRow.getCell(3).setCellValue(String.valueOf(t2));
        totalRow.getCell(4).setCellValue(String.valueOf(t3));

        totalRow.getCell(5).setCellValue(String.valueOf(t4));
        totalRow.getCell(6).setCellValue(String.valueOf(t5));
        totalRow.getCell(7).setCellValue(String.valueOf(t6));
        totalRow.getCell(8).setCellValue(String.valueOf(t7));
        totalRow.getCell(9).setCellValue(String.valueOf(t8));
        totalRow.getCell(10).setCellValue(String.valueOf(t9));
        totalRow.getCell(11).setCellValue(String.valueOf(t10));
        totalRow.getCell(12).setCellValue(String.valueOf(t11));
        totalRow.getCell(13).setCellValue(String.valueOf(t12));
        totalRow.getCell(14).setCellValue(String.valueOf(t13));

        Row bootRow = st.createRow(salelist.size() + 3);
        st.addMergedRegion(new CellRangeAddress(salelist.size() + 3, salelist.size() + 3, 0, thead.length));
        Cell bootCell = bootRow.createCell(0);
        bootCell.setCellStyle(CellStyleUtils.saleRankNoBorderLeft(wb));
        bootCell.setCellValue("                      注：本期奖池资金累积" + announcement.getPoolTotal() + "元，"
                + "一等奖单注金额" + announcement.getPrize1Money() + "元，"
                + "二等奖单注金额" + announcement.getPrize2Money() + "元。");
        return wb;
    }

}
