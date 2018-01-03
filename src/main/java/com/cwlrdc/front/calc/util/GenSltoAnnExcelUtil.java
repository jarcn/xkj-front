package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.ltto.entity.LttoLotteryAnnouncement;
import com.cwlrdc.front.calc.bean.PrintAnnceExcelParaBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

/**
 * 创建双色球开奖公告excel
 * Created by chenjia on 2017/11/3.
 */
public final class GenSltoAnnExcelUtil {

  private GenSltoAnnExcelUtil() { }


  private static final String CWL_NAME = "中国福利彩票";
  private static final String EXCEL_TITLE = "全 国 开 奖 公 告";
  private static final String CASH_DESCRIPTION = "自开奖之日起60个自然日内兑奖,最后一天为国家法定节假日或者彩票市场休市的,"
      + "顺延至节假日后或者彩票市场休市结束后的第一个工作日。";
  private static final String CWL_SIGNA = "中国福利彩票发行管理中心";
  private static final String MONEY_UNIT = "元。";
  private static final String WIN_LEVEL_1 = "一等奖";
  private static final String WIN_LEVEL_2 = "二等奖";
  private static final String WIN_LEVEL_3 = "三等奖";
  private static final String WIN_LEVEL_4 = "四等奖";
  private static final String WIN_LEVEL_5 = "五等奖";
  private static final String WIN_LEVEL_6 = "六等奖";
  private static final String CX_WIN_LEVEL_1 = "其中:一等奖复式投注";
  private static final String CX_WIN_LEVEL_6 = "其中:六等奖复式投注";

  private static final String RED_NUM_ZH = "红球号码";
  private static final String BLUE_NUM_ZH = "蓝球号码";

  private static final String TITLE_1 = "一、本期销售额：";
  private static final String TITLE_2 = "二、中奖号码：";
  private static final String TITLE_3 = "三、中奖结果：";
  private static final String TITLE_4 = "四、下期一等奖奖池累计金额:";
  private static final String TITLE_5 = "五、一等奖中奖情况：";
  private static final String TITLE_6 = "六、兑奖期限：";


  /**
   * 双色球促销开奖公告打印
   */
  public static HSSFWorkbook createSltoAnnExcel(PrintAnnceExcelParaBean paraBean) {
    LttoLotteryAnnouncement announcement = paraBean.getAnnouncement();

    HSSFWorkbook wb = new HSSFWorkbook();
    Sheet sheet = wb.createSheet();
    int columnWidth = sheet.getColumnWidth(1);
    sheet.setColumnWidth((short) 0, (short) 1600);
    sheet.setColumnWidth((short) 8, (short) columnWidth * 2);
    setPrintExcelStyle(sheet);

    Row row1 = sheet.createRow(0);
    sheet.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum(), 0, 8));
    Cell row1Cell1 = row1.createCell(0);
    row1Cell1.setCellStyle(CellStyleUtils.headCellStyleNoBold(wb));
    row1Cell1.setCellValue(CWL_NAME + "“" + paraBean.getGameName() + "”");

    Row row2 = sheet.createRow(1);
    sheet.addMergedRegion(new CellRangeAddress(row2.getRowNum(), row2.getRowNum(), 0, 8));
    Cell row2Cell1 = row2.createCell(0);
    row2Cell1.setCellStyle(CellStyleUtils.headCellStyleNoBold(wb));
    row2Cell1.setCellValue(EXCEL_TITLE);

    Row row3 = sheet.createRow(2);
    sheet.addMergedRegion(new CellRangeAddress(row3.getRowNum(), row3.getRowNum(), 0, 8));
    Cell row3Cell1 = row3.createCell(0);
    row3Cell1.setCellStyle(CellStyleUtils.headCellStyleNoBold(wb));
    row3Cell1.setCellValue("第" + paraBean.getPeriodNum() + "期");

    Row row5 = sheet.createRow(4);
    Cell row5Cell1 = row5.createCell(0);
    row5Cell1.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    Cell row5Cell4 = row5.createCell(3);
    sheet.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 3, 5));
    row5Cell4.setCellStyle(CellStyleUtils.anncePoolAmont(wb));
    Cell row5Cell7 = row5.createCell(6);
    row5Cell7.setCellStyle(CellStyleUtils.noBorderLeft(wb));

    row5Cell1.setCellValue(TITLE_1);

    row5Cell4.setCellValue(announcement.getSaleMoneyTotal() != null ? announcement.getSaleMoneyTotal().longValue() : 0.00);
    row5Cell7.setCellValue(MONEY_UNIT);

    Row row7 = sheet.createRow(6);
    Cell row7Cell1 = row7.createCell(0);
    row7Cell1.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    row7Cell1.setCellValue(TITLE_2);

    Row row8 = sheet.createRow(7);
    Cell row8Cell1 = row8.createCell(1);
    CellRangeAddress redBollRegion = new CellRangeAddress(row8.getRowNum(), row8.getRowNum(), 1, 6);
    sheet.addMergedRegion(redBollRegion);
    setRegionBorder(1, redBollRegion, sheet, wb);
    row8Cell1.setCellStyle(CellStyleUtils.cellStyleBold(wb));
    row8Cell1.setCellValue(RED_NUM_ZH);

    Cell row8Cell8 = row8.createCell(7);
    CellRangeAddress blueBollRegion = new CellRangeAddress(row8.getRowNum(), row8.getRowNum(), 7,8);
    sheet.addMergedRegion(blueBollRegion);
    setRegionBorder(1, blueBollRegion, sheet, wb);
    row8Cell8.setCellStyle(CellStyleUtils.cellStyleBold(wb));
    row8Cell8.setCellValue(BLUE_NUM_ZH);

    Row row9 = sheet.createRow(8);
    Cell row9Cell2 = row9.createCell(1);
    row9Cell2.setCellStyle(CellStyleUtils.cellStyleBigBold(wb));
    Cell row9Cell3 = row9.createCell(2);
    row9Cell3.setCellStyle(CellStyleUtils.cellStyleBigBold(wb));
    Cell row9Cell4 = row9.createCell(3);
    row9Cell4.setCellStyle(CellStyleUtils.cellStyleBigBold(wb));
    Cell row9Cell5 = row9.createCell(4);
    row9Cell5.setCellStyle(CellStyleUtils.cellStyleBigBold(wb));
    Cell row9Cell6 = row9.createCell(5);
    row9Cell6.setCellStyle(CellStyleUtils.cellStyleBigBold(wb));
    Cell row9Cell7 = row9.createCell(6);
    row9Cell7.setCellStyle(CellStyleUtils.cellStyleBigBold(wb));
    CellRangeAddress buleNumRegion = new CellRangeAddress(row9.getRowNum(), row9.getRowNum(), 7, 8);
    sheet.addMergedRegion(buleNumRegion);
    setRegionBorder(1, buleNumRegion, sheet, wb);
    Cell row9Cell8 = row9.createCell(7);
    row9Cell8.setCellStyle(CellStyleUtils.cellStyleBigBold(wb));

    String[] winNums = parseWinNum(paraBean.getWinNum());
    row9Cell2.setCellValue(winNums[0]);
    row9Cell3.setCellValue(winNums[1]);
    row9Cell4.setCellValue(winNums[2]);
    row9Cell5.setCellValue(winNums[3]);
    row9Cell6.setCellValue(winNums[4]);
    row9Cell7.setCellValue(winNums[5]);
    row9Cell8.setCellValue(winNums[6]);

    Row row11 = sheet.createRow(10);
    Cell row11Cell1 = row11.createCell(0);
    row11Cell1.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    row11Cell1.setCellValue(TITLE_3);
    createWinDetail(wb, 11, "奖等", "中奖注数(注)", "中奖金额(元)");

    boolean firstPromotion = paraBean.isFirstPromotion();
    boolean sixPromotion = paraBean.isSixPromotion();
    String prize1Count = announcement.getPrize1Count().toString();
    String prize1Money = announcement.getPrize1Money().toString();
    String prize2Count = announcement.getPrize2Count().toString();
    String prize2Money = announcement.getPrize2Money().toString();
    String prize3Count = announcement.getPrize3Count().toString();
    String prize3Money = announcement.getPrize3Money().toString();
    String prize4Count = announcement.getPrize4Count().toString();
    String prize4Money = announcement.getPrize4Money().toString();
    String prize5Count = announcement.getPrize5Count().toString();
    String prize5Money = announcement.getPrize5Money().toString();
    String prize6Count = announcement.getPrize6Count().toString();
    String prize6Money = announcement.getPrize6Money().toString();

    Long poolTotal = announcement.getPoolTotal().longValue();
    String bullNote = announcement.getBullNote();

    //一等奖,六等奖同时促销
    if (firstPromotion && sixPromotion) {
      String prize7Count = announcement.getPrize7Count().toString();
      long prize7Money = announcement.getPrize7Money().longValue();
      String prize8Count = announcement.getPrize8Count().toString();
      String prize8Money = announcement.getPrize8Money().toString();
      createWinDetail(wb, 12, WIN_LEVEL_1, prize1Count, prize1Money);
      String pj1 = Long.parseLong(prize1Money) + prize7Money + "(含派奖" + prize7Money + ")";
      createWinDetail(wb, 13, CX_WIN_LEVEL_1, prize7Count, pj1);
      createWinDetail(wb, 14, WIN_LEVEL_2, prize2Count, prize2Money);
      createWinDetail(wb, 15, WIN_LEVEL_3, prize3Count, prize3Money);
      createWinDetail(wb, 16, WIN_LEVEL_4, prize4Count, prize4Money);
      createWinDetail(wb, 17, WIN_LEVEL_5, prize5Count, prize5Money);
      createWinDetail(wb, 18, WIN_LEVEL_6, prize6Count, prize6Money);
      String pj6 =
          Long.parseLong(prize6Money) + Long.parseLong(prize8Money) + "(含派奖" + prize8Money + ")";
      createWinDetail(wb, 19, CX_WIN_LEVEL_6, prize8Count, pj6);

      createJackpotRow(wb, 21, TITLE_4, poolTotal.toString(), MONEY_UNIT);
      createPromotionJackpotRow(wb, 22, "下期一等奖派奖金额：", paraBean.getCxPrize1PoolMoney(),
          MONEY_UNIT); // 一等奖派奖奖池余额
      createPromotionJackpotRow(wb, 23, "六等奖派奖奖金余额：", paraBean.getCxPrize6PoolMoney(),
          MONEY_UNIT); // 六等奖派奖奖池余额

      if (StringUtils.isNotBlank(bullNote)) {
        createAnnceNote(wb, 25, bullNote);
      } else {
        sheet.createRow(25).setZeroHeight(true);
      }
      create1PrizeDetatil(wb, 26, TITLE_5, paraBean.getAllPrize1Detail());
      sheet.createRow(28).setZeroHeight(true);
      createDeclaration(wb, 29, TITLE_6, CASH_DESCRIPTION);
      createAutograph(wb, 42, CWL_SIGNA);
      createAutograph(wb, 43, paraBean.getPrintDate());
    }
    //只有一等奖促销
    if (firstPromotion && !sixPromotion) {
      String prize7Count = announcement.getPrize7Count().toString();
      long prize7Money = announcement.getPrize7Money().longValue();
      createWinDetail(wb, 12, WIN_LEVEL_1, prize1Count, prize1Money);
      String pj1 = (Long.parseLong(prize1Money) + prize7Money) + "(含派奖" + prize7Money + ")";
      createWinDetail(wb, 13, CX_WIN_LEVEL_1, prize7Count, pj1);
      createWinDetail(wb, 14, WIN_LEVEL_2, prize2Count, prize2Money);
      createWinDetail(wb, 15, WIN_LEVEL_3, prize3Count, prize3Money);
      createWinDetail(wb, 16, WIN_LEVEL_4, prize4Count, prize4Money);
      createWinDetail(wb, 17, WIN_LEVEL_5, prize5Count, prize5Money);
      createWinDetail(wb, 18, WIN_LEVEL_6, prize6Count, prize6Money);
      sheet.createRow(19).setZeroHeight(true);
      createJackpotRow(wb, 21, TITLE_4, poolTotal.toString(), MONEY_UNIT);
      createPromotionJackpotRow(wb, 22, "下期一等奖派奖金额：", paraBean.getCxPrize1PoolMoney(),
          MONEY_UNIT); // 一等奖派奖奖池余额
      if (StringUtils.isNotBlank(bullNote)) {
        createAnnceNote(wb, 23, bullNote);
      } else {
        sheet.createRow(23).setZeroHeight(true);
      }
      sheet.createRow(25).setZeroHeight(true);
      create1PrizeDetatil(wb, 26, TITLE_5, paraBean.getAllPrize1Detail());
      sheet.createRow(28).setZeroHeight(true);
      createDeclaration(wb, 29, TITLE_6, CASH_DESCRIPTION);
      createAutograph(wb, 42, CWL_SIGNA);
      createAutograph(wb, 43, paraBean.getPrintDate());
    }
    //只有六等奖促销
    if (!firstPromotion && sixPromotion) {
      String prize8Count = announcement.getPrize8Count().toString();
      String prize8Money = announcement.getPrize8Money().toString();
      createWinDetail(wb, 12, WIN_LEVEL_1, prize1Count, prize1Money);
      sheet.createRow(13).setZeroHeight(true);
      createWinDetail(wb, 14, WIN_LEVEL_2, prize2Count, prize2Money);
      createWinDetail(wb, 15, WIN_LEVEL_3, prize3Count, prize3Money);
      createWinDetail(wb, 16, WIN_LEVEL_4, prize4Count, prize4Money);
      createWinDetail(wb, 17, WIN_LEVEL_5, prize5Count, prize5Money);
      createWinDetail(wb, 18, WIN_LEVEL_6, prize6Count, prize6Money);
      String pj6 =  (Long.parseLong(prize6Money) + Long.parseLong(prize8Money)) + "(含派奖" + prize8Money + ")";
      createWinDetail(wb, 19, CX_WIN_LEVEL_6, prize8Count, pj6);
      createJackpotRow(wb, 21, TITLE_4, poolTotal.toString(), MONEY_UNIT);
      createPromotionJackpotRow(wb, 23, "六等奖派奖奖金余额：", paraBean.getCxPrize6PoolMoney(),
          MONEY_UNIT); // 六等奖派奖奖池余额
      if (StringUtils.isNotBlank(bullNote)) {
        createAnnceNote(wb, 25, bullNote);
      } else {
        sheet.createRow(25).setZeroHeight(true);
      }
      create1PrizeDetatil(wb, 26, TITLE_5, paraBean.getAllPrize1Detail());
      sheet.createRow(28).setZeroHeight(true);
      createDeclaration(wb, 29, TITLE_6, CASH_DESCRIPTION);
      createAutograph(wb, 42, CWL_SIGNA);
      createAutograph(wb, 43, paraBean.getPrintDate());
    }
    //非促销
    if (!firstPromotion && !sixPromotion) {
      createWinDetail(wb, 12, WIN_LEVEL_1, prize1Count, prize1Money);
      createWinDetail(wb, 13, WIN_LEVEL_2, prize2Count, prize2Money);
      createWinDetail(wb, 14, WIN_LEVEL_3, prize3Count, prize3Money);
      createWinDetail(wb, 15, WIN_LEVEL_4, prize4Count, prize4Money);
      createWinDetail(wb, 16, WIN_LEVEL_5, prize5Count, prize5Money);
      createWinDetail(wb, 17, WIN_LEVEL_6, prize6Count, prize6Money);
      createJackpotRow(wb, 19, TITLE_4, poolTotal.toString(), MONEY_UNIT);
      if (StringUtils.isNotBlank(bullNote)) {
        createAnnceNote(wb, 20, bullNote);
      } else {
        sheet.createRow(20).setZeroHeight(true);
      }
      sheet.createRow(21).setZeroHeight(true);
      if (StringUtils.isNotBlank(paraBean.getCxPrize1PoolMoney()) && StringUtils.isNotBlank(paraBean.getCxPrize6PoolMoney())){
        createPromotionJackpotRow(wb, 22, "下期一等奖派奖金额：", paraBean.getCxPrize1PoolMoney(), MONEY_UNIT); // 一等奖派奖奖池余额
        createPromotionJackpotRow(wb, 23, "六等奖派奖奖金余额：", paraBean.getCxPrize6PoolMoney(), MONEY_UNIT); // 六等奖派奖奖池余额
        create1PrizeDetatil(wb, 24, TITLE_5, paraBean.getAllPrize1Detail());
        sheet.createRow(26).setZeroHeight(true);
        createDeclaration(wb, 27, TITLE_6, CASH_DESCRIPTION);
        createAutograph(wb, 40, CWL_SIGNA);
        createAutograph(wb, 41, paraBean.getPrintDate());
      } else {
        create1PrizeDetatil(wb, 23, TITLE_5, paraBean.getAllPrize1Detail());
        sheet.createRow(25).setZeroHeight(true);
        createDeclaration(wb, 26, TITLE_6, CASH_DESCRIPTION);
        createAutograph(wb, 39, CWL_SIGNA);
        createAutograph(wb, 40, paraBean.getPrintDate());
      }
    }

    return wb;
  }

  /**
   * 打印在开奖公告中的话
   */
  private static void createAnnceNote(HSSFWorkbook wb, int rowNum, String bullNote) {
    HSSFSheet sheet = wb.getSheetAt(0);
    if (StringUtils.isNotBlank(bullNote)) {
      Row row = sheet.createRow(rowNum);
      row.setHeight((short) 1200);
      sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 8));
      Cell title5Cell = row.createCell(1);
      title5Cell.setCellStyle(CellStyleUtils.noBorder(wb));
      title5Cell.setCellValue(new HSSFRichTextString(bullNote));
    }
  }

  /**
   * 创建中奖详情表
   */
  private static void createWinDetail(HSSFWorkbook wb, int rowNum, String title, String count,
      String moneyDetial) {
    HSSFSheet sheet = wb.getSheetAt(0);

    Row row = sheet.createRow(rowNum);
    CellRangeAddress cellRangeAddress1 = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1,
        3);
    sheet.addMergedRegion(cellRangeAddress1);
    setRegionBorder(1, cellRangeAddress1, sheet, wb);
    Cell rowCell1 = row.createCell(1);
    rowCell1.setCellStyle(CellStyleUtils.cellStyleBold(wb));
    rowCell1.setCellValue(title);

    CellRangeAddress cellRangeAddress2 = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4,
        5);
    sheet.addMergedRegion(cellRangeAddress2);
    setRegionBorder(1, cellRangeAddress2, sheet, wb);
    Cell rowCell4 = row.createCell(4);
    rowCell4.setCellStyle(CellStyleUtils.cellStyleBold(wb));
    rowCell4.setCellValue(count);

    CellRangeAddress cellRangeAddress3 = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6,
        8);
    sheet.addMergedRegion(cellRangeAddress3);
    setRegionBorder(1, cellRangeAddress3, sheet, wb);
    Cell rowCell6 = row.createCell(6);
    rowCell6.setCellStyle(CellStyleUtils.cellStyleBold(wb));
    rowCell6.setCellValue(moneyDetial);
  }

  /**
   * 创建非促销奖池信息
   */
  private static void createJackpotRow(HSSFWorkbook wb, int rowNum, String title, String money,
      String unit) {
    HSSFSheet sheet = wb.getSheetAt(0);

    Row row5 = sheet.createRow(rowNum);
    Cell row5Cell1 = row5.createCell(0);
    row5Cell1.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    sheet.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 0, 4));
    row5Cell1.setCellValue(title);

    Cell row5Cell4 = row5.createCell(5);
    sheet.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 5, 7));
    row5Cell4.setCellStyle(CellStyleUtils.anncePoolAmont(wb));
    row5Cell4.setCellValue(money);

    Cell row5Cell7 = row5.createCell(8);
    row5Cell7.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    row5Cell7.setCellValue(unit);

  }

  /**
   * 创建促销奖池信息
   */
  private static void createPromotionJackpotRow(HSSFWorkbook wb, int rowNum, String title,
      String money, String unit) {
    HSSFSheet sheet = wb.getSheetAt(0);

    Row row5 = sheet.createRow(rowNum);
    Cell row5Cell1 = row5.createCell(1);
    row5Cell1.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    sheet.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 1, 4));
    row5Cell1.setCellValue(title);

    Cell row5Cell4 = row5.createCell(5);
    sheet.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 5, 7));
    row5Cell4.setCellStyle(CellStyleUtils.anncePoolAmont(wb));
    row5Cell4.setCellValue(money);

    Cell row5Cell7 = row5.createCell(8);
    row5Cell7.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    row5Cell7.setCellValue(unit);
  }

  /**
   * 创建一等奖全国中奖详情
   */
  private static void create1PrizeDetatil(HSSFWorkbook wb, int rowNum, String title,
      String detail) {
    HSSFSheet sheet = wb.getSheetAt(0);
    Row row = sheet.createRow(rowNum);
    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));
    Cell title5Cell = row.createCell(0);
    title5Cell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    title5Cell.setCellValue(title);

    Row level1Row = sheet.createRow(rowNum + 1);
    level1Row.setHeight((short) 1200);
    Cell level1Cell = level1Row.createCell(1);
    sheet.addMergedRegion(new CellRangeAddress(level1Row.getRowNum(), level1Row.getRowNum(), 1, 8));
    level1Cell.setCellStyle(CellStyleUtils.infoStyleNoBorder(wb));
    level1Cell.setCellValue(new HSSFRichTextString(detail));

  }

  /**
   * 创建兑奖期限说明
   */
  private static void createDeclaration(HSSFWorkbook wb, int rowNum, String title,
      String declaration) {
    Sheet sheet = wb.getSheetAt(0);
    Row title6Row = sheet.createRow(rowNum);
    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));
    Cell title6Cell = title6Row.createCell(0);
    title6Cell.setCellStyle(CellStyleUtils.noBorderLeft(wb));
    title6Cell.setCellValue(title);

    Row cashRow = sheet.createRow(rowNum + 1);
    Cell cashCell = cashRow.createCell(1);
    cashRow.setHeight((short) 800);
    sheet.addMergedRegion(new CellRangeAddress(cashRow.getRowNum(), cashRow.getRowNum(), 1, 8));
    cashCell.setCellStyle(CellStyleUtils.infoStyleNoBorder(wb));
    cashCell.setCellValue(declaration);

  }

  /**
   * 创建文件签名
   */
  private static void createAutograph(HSSFWorkbook wb, int rowNum, String detail) {
    HSSFSheet sheet = wb.getSheetAt(0);
    Row boot3Row = sheet.createRow(rowNum);
    Cell boot3Cell = boot3Row.createCell(6);
    boot3Cell.setCellStyle(CellStyleUtils.annceMentNoBorder(wb));
    boot3Cell.setCellValue(detail);
  }

  /**
   * 中奖号码转化为数组
   */
  private static String[] parseWinNum(String winNum) {
    String[] winNums = new String[7];
    if (StringUtils.isNotBlank(winNum)) {
      String[] allboll = winNum.split("\\+");
      String[] redbolls = allboll[0].split(",");
      String blueboll = allboll[1];
      for (int i = 0; i < redbolls.length; i++) {
        winNums[i] = redbolls[i];
      }
      winNums[6] = blueboll;
    }
    return winNums;
  }

  /**
   * 设置sheet打印格式
   */
  private static void setPrintExcelStyle(Sheet sheet) {
    sheet.setAutobreaks(true);
    HSSFPrintSetup ps = (HSSFPrintSetup) sheet.getPrintSetup();
    // 打印方向，true：横向，false：纵向
    ps.setLandscape(false);
    //设置打印页面为水平居中
    sheet.setHorizontallyCenter(true);
    //设置打印页面为垂直居中
    sheet.setVerticallyCenter(true);
  }

  /**
   * @param border 边框宽度
   * @param region 合并单元格区域范围
   */
  private static void setRegionBorder(int border, CellRangeAddress region, Sheet sheet,
      Workbook wb) {
    RegionUtil.setBorderBottom(border, region, sheet, wb);
    RegionUtil.setBorderLeft(border, region, sheet, wb);
    RegionUtil.setBorderRight(border, region, sheet, wb);
    RegionUtil.setBorderTop(border, region, sheet, wb);
  }
}
