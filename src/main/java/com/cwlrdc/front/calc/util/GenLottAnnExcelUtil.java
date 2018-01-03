package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

/**
 * //生成开奖公告excel文件
 * Created by chenjia on 2017/5/8.
 */
@Component
public class GenLottAnnExcelUtil {

    public static HSSFWorkbook bean2Excel(ParaGamePeriodInfo paraGamePeriodInfo, LttoLotteryAnnouncement source, String gameName, String winNum, String allPrize1Detail) {
        String promotionStatus = paraGamePeriodInfo.getPromotionStatus();
        HSSFWorkbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("全国开奖公告");
        sheet.setAutobreaks(true);
        HSSFPrintSetup ps = (HSSFPrintSetup) sheet.getPrintSetup();
        ps.setLandscape(false); // 打印方向，true：横向，false：纵向
        ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); //纸张
        sheet.setMargin(HSSFSheet.BottomMargin, (double) 0.5);// 页边距（下）
        sheet.setMargin(HSSFSheet.LeftMargin, (double) 0.1);// 页边距（左）
        sheet.setMargin(HSSFSheet.RightMargin, (double) 0.1);// 页边距（右）
        sheet.setMargin(HSSFSheet.TopMargin, (double) 0.5);// 页边距（上）
        sheet.setHorizontallyCenter(true);//设置打印页面为水平居中
        sheet.setVerticallyCenter(true);//设置打印页面为垂直居中
        Row gameRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));  //合并第一行的第一列到第九列
        Cell gameCell = gameRow.createCell(0);
        gameCell.setCellStyle(CellStyleUtils.headCellStyleNoBold(wb));
        String title = "中国福利彩票\"" + gameName + "\"";
        gameCell.setCellValue(title);
        Row annonceRow = sheet.createRow(1);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));
        Cell annonceCell = annonceRow.createCell(0);
        annonceCell.setCellStyle(CellStyleUtils.headCellStyleNoBold(wb));
        String info = "全 国 开 奖 公 告";
        annonceCell.setCellValue(info);

        Row periodRow = sheet.createRow(2);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));
        Cell periodCell = periodRow.createCell(0);
        periodCell.setCellStyle(CellStyleUtils.headCellStyleNoBold(wb));
        String periodInfo = "第" + source.getPeriodNum() + "期";
        periodCell.setCellValue(periodInfo);

        Row saleRow = sheet.createRow(4);
        Cell saleCell = saleRow.createCell(0);
        saleCell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        saleCell.setCellValue("一、本期销售额:");
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 3, 5));
        saleRow.createCell(3);
        Cell amontCell = saleRow.getCell(3);
        amontCell.setCellStyle(CellStyleUtils.anncePoolAmont(wb));
        amontCell.setCellValue(String.valueOf(source.getSaleMoneyTotal() == null ? "" : source.getSaleMoneyTotal().longValue()));
        Cell uintCell = saleRow.createCell(6);
        uintCell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        uintCell.setCellValue("元。");

        Row title2 = sheet.createRow(6);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 2));
        Cell title2Cell = title2.createCell(0);
        title2Cell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        title2Cell.setCellValue("二、中奖号码:");
        if (Constant.GameCode.GAME_NAME_LOTO.equals(gameName)) {
            //七乐彩中奖号码表格
            //01|02|03|04|05|06|07+08
            Row winNumTile = sheet.createRow(7);
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 1, 7));
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 8, 9));
            for (int i = 1; i < 10; i++) {
                winNumTile.createCell(i).setCellStyle(CellStyleUtils.cellStyleBold(wb));
            }
            Cell redCell = winNumTile.getCell(1);
            redCell.setCellStyle(CellStyleUtils.cellStyleBold(wb));
            Cell buleCell = winNumTile.getCell(8);
            buleCell.setCellStyle(CellStyleUtils.cellStyleBold(wb));
            redCell.setCellValue("基本号码");
            buleCell.setCellValue("特别号码");

            Row winNumRow = sheet.createRow(8);
            for (int i = 1; i < 10; i++) {
                winNumRow.createCell(i).setCellStyle(CellStyleUtils.cellStyleBigBold(wb));
            }
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 8, 9));

            if (StringUtils.isNotBlank(winNum)) {
                String[] winNums = winNum.split("\\@");
                String redbolls = winNums[0];
                String[] redboll = redbolls.split(",");
                String blueboll = winNums[1];
                winNumRow.getCell(1).setCellValue(redboll[0]);
                winNumRow.getCell(2).setCellValue(redboll[1]);
                winNumRow.getCell(3).setCellValue(redboll[2]);
                winNumRow.getCell(4).setCellValue(redboll[3]);
                winNumRow.getCell(5).setCellValue(redboll[4]);
                winNumRow.getCell(6).setCellValue(redboll[5]);
                winNumRow.getCell(7).setCellValue(redboll[6]);
                winNumRow.getCell(8).setCellValue(blueboll);
            }
        }

        Row winDataTitle = sheet.createRow(10);
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 0, 2));
        Cell title3Cell = winDataTitle.createCell(0);
        title3Cell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        title3Cell.setCellValue("三、中奖结果:");

        int winLevelCount = 0;
        //七乐彩中奖号码表
        if (Constant.GameCode.GAME_NAME_LOTO.equals(gameName)) {
            if (!Constant.Status.WIN_PROMOTION_STATUS_NO.equalsIgnoreCase(promotionStatus)) {
                if (source.getGradeCount() != null) {
                    winLevelCount = source.getGradeCount();
                } else {
                    winLevelCount = 7;
                }
            } else {
                winLevelCount = 7;
            }
            Row winDetail = sheet.createRow(11);
            sheet.addMergedRegion(new CellRangeAddress(11, 11, 1, 2));
            sheet.addMergedRegion(new CellRangeAddress(11, 11, 3, 5));
            sheet.addMergedRegion(new CellRangeAddress(11, 11, 6, 9));
            for (int i = 1; i < 10; i++) {
                winDetail.createCell(i).setCellStyle(CellStyleUtils.cellStyleBold(wb));
            }
            winDetail.getCell(1).setCellValue("奖等");
            winDetail.getCell(3).setCellValue("中奖注数(注)");
            winDetail.getCell(6).setCellValue("中奖金额(元)");

            for (int i = 0; i < winLevelCount; i++) {
                int rowNum = i + 12;
                Row row = sheet.createRow(rowNum);
                for (int j = 1; j < 10; j++) {
                    row.createCell(j).setCellStyle(CellStyleUtils.cellStyleBold(wb));
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 2));
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 5));
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 6, 9));
                }
                createTable(i, row, source);
            }
        }

        Row title4Row = sheet.createRow(winLevelCount + 13);
        sheet.addMergedRegion(new CellRangeAddress(winLevelCount + 13, winLevelCount + 13, 0, 3));
        Cell title4Cell = title4Row.createCell(0);
        title4Cell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        title4Cell.setCellValue("四、下期一等奖奖池累计金:");
        sheet.addMergedRegion(new CellRangeAddress(winLevelCount + 13, winLevelCount + 13, 4, 6));
        Cell jianciCell = title4Row.createCell(4);
        jianciCell.setCellStyle(CellStyleUtils.anncePoolAmont(wb));
        Long jackpotBonus = source.getPoolTotal().longValue();
        jianciCell.setCellValue(String.valueOf(jackpotBonus));
        Cell uint = title4Row.createCell(7);
        uint.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        uint.setCellValue("元。");

        String bullNote = source.getBullNote();
        if (StringUtils.isNotBlank(bullNote)) {
            Row title5Row = sheet.createRow(winLevelCount + 14);
            title5Row.setHeight((short) 1200);
            sheet.addMergedRegion(new CellRangeAddress(winLevelCount + 14, winLevelCount + 14, 1, 8));
            Cell title5Cell = title5Row.createCell(1);
            title5Cell.setCellStyle(CellStyleUtils.noBorder(wb));
            title5Cell.setCellValue(new HSSFRichTextString(bullNote));
        }

        Row title5Row = sheet.createRow(winLevelCount + 15);
        sheet.addMergedRegion(new CellRangeAddress(winLevelCount + 15, winLevelCount + 15, 0, 2));
        Cell title5Cell = title5Row.createCell(0);
        title5Cell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        title5Cell.setCellValue("五、一等奖中奖情况:");

        Row level1Row = sheet.createRow(winLevelCount + 16);
        level1Row.setHeight((short) 1200);
        Cell level1Cell = level1Row.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(winLevelCount + 16, winLevelCount + 16, 1, 9));
        level1Cell.setCellStyle(CellStyleUtils.infoStyleNoBorder(wb));
        level1Cell.setCellValue(new HSSFRichTextString(allPrize1Detail));

        Row title6Row = sheet.createRow(winLevelCount + 17);
        sheet.addMergedRegion(new CellRangeAddress(winLevelCount + 17, winLevelCount + 17, 0, 2));
        Cell title6Cell = title6Row.createCell(0);
        title6Cell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
        title6Cell.setCellValue("六、兑奖期限:");

        Row cashRow = sheet.createRow(winLevelCount + 18);
        Cell cashCell = cashRow.createCell(1);
        cashRow.setHeight((short) 1000);
        sheet.addMergedRegion(new CellRangeAddress(winLevelCount + 18, winLevelCount + 18, 1, 9));
        cashCell.setCellStyle(CellStyleUtils.infoStyleNoBorder(wb));
        cashCell.setCellValue("自开奖之日起60个自然日内兑奖,最后一天为国家法定节假日或者彩票市场休市的," +
                "顺延至节假日后或者彩票市场休市结束后的第一个工作日。");

        Row boot3Row = sheet.createRow(winLevelCount + 30);
        Cell boot3Cell = boot3Row.createCell(6);
        boot3Cell.setCellStyle(CellStyleUtils.annceMentNoBorder(wb));
        boot3Cell.setCellValue("中国福利彩票发行管理中心");
        Row boot4Row = sheet.createRow(winLevelCount + 31);
        Cell boot4Cell = boot4Row.createCell(6);
        boot4Cell.setCellStyle(CellStyleUtils.noBorderCenter(wb));
        boot4Cell.setCellValue(DateFormatUtils.format(System.currentTimeMillis(), "yyyy年MM月dd日"));
        return wb;
    }

    private static void createTable(int i, Row row, LttoLotteryAnnouncement source) {
        switch (i + 1) {
            case 1:
                row.getCell(1).setCellValue("一等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize1Count()));  //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize1Money())); //中奖金额
                break;
            case 2:
                row.getCell(1).setCellValue("二等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize2Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize2Money()));
                break;
            case 3:
                row.getCell(1).setCellValue("三等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize3Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize3Money()));
                break;
            case 4:
                row.getCell(1).setCellValue("四等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize4Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize4Money()));
                break;
            case 5:
                row.getCell(1).setCellValue("五等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize5Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize5Money()));
                break;
            case 6:
                row.getCell(1).setCellValue("六等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize6Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize6Money()));
                break;
            case 7:
                row.getCell(1).setCellValue("七等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize7Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize7Money().longValue()));
                break;
            case 8:
                row.getCell(1).setCellValue("八等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize8Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize8Money()));
                break;
            case 9:
                row.getCell(1).setCellValue("九等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize9Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize9Money()));
                break;
            case 10:
                row.getCell(1).setCellValue("十等奖");    //奖级
                row.getCell(3).setCellValue(String.valueOf(source.getPrize10Count()));         //中奖注数
                row.getCell(6).setCellValue(String.valueOf(source.getPrize10Money()));
                break;
            default:
                return;
        }
    }

}
