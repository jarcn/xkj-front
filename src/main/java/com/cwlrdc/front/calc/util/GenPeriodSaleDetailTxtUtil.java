package com.cwlrdc.front.calc.util;

import com.cwlrdc.front.common.Constant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

/**
 * 生成当期游戏全国销售、中奖、弃奖、开奖公告详情文件
 * 文件名称：游戏编码_期号.txt（10001_2017022.txt）
 * Created by chenjia on 2017/8/28.
 */
@Slf4j
public class GenPeriodSaleDetailTxtUtil {

    public static String getQuerySql(String gameCode,String periodNum){
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ");
        sb.append("sale.GAME_CODE gameCode,").append("sale.PERIOD_NUM periodNum,").append("sale.PROVINCE_ID provinceId,")
                .append("sale.AMOUNT amount,").append("sale.CANCEL_MONEY cancelMoney,").append("win.WIN_DETAIL winNum,")
                .append(" win.GRADE_COUNT gradeCount,").append("win.PRIZE1_COUNT prize1count,").append("ment.PRIZE1_MONEY prize1Money,")
                .append("win.PRIZE2_COUNT prize2count,").append("ment.PRIZE2_MONEY prize2Money,").append("win.PRIZE3_COUNT prize3count,")
                .append("ment.PRIZE3_MONEY prize3Money,").append("win.PRIZE4_COUNT prize4count,").append("ment.PRIZE4_MONEY prize4Money,")
                .append("win.PRIZE5_COUNT prize5count,").append("ment.PRIZE5_MONEY prize5Money,").append("win.PRIZE6_COUNT prize6count,")
                .append("ment.PRIZE6_MONEY prize6Money,").append("win.PRIZE7_COUNT prize7count,").append("ment.PRIZE7_MONEY prize7Money,")
                .append("win.PRIZE8_COUNT prize8count,").append("ment.PRIZE8_MONEY prize8Money,").append("win.PRIZE9_COUNT prize9count,")
                .append("ment.PRIZE9_MONEY prize9Money,").append("win.PRIZE10_COUNT prize10count,").append("ment.PRIZE10_MONEY prize10Money,")
                .append("ment.PROCESS_STATUS cwlKey,").append(" ment.SALE_MONEY_TOTAL saleMoneyTotal,").append("ment.OVERDUE_MONEY_TOTAL overdueMoney,")
                .append("ment.FUND_BGN fundBgn,").append("ment.GET_INT_BALANCE_ALL getInBalanceAll,").append("ment.GET_INT_BALANCE1 getInBalance1,")
                .append("ment.FUND2_POOL_AUTO_ALL fund2PoolAutoAll,").append("ment.FUND2_POOL_AUTO1 fund2PoolAuto1,").append("ment.FUND2_POOL_HAND fund2PoolHand,")
                .append("ment.FUND2_PRIZE fund2Prize,").append("ment.FUND_TEMP_IN fundTmpIn,").append("ment.FUND_TEMP_OUT fundTmpOut,")
                .append("ment.FUND_TOTAL fundTotal,").append("ment.POOL_BGN poolBgn,").append("ment.POOL_CURRENT poolCurrnt,")
                .append("ment.UNSHOT_OTHER_FLOAT_PRIZE unshotOtherFloatPrize,").append("ment.EXCESS_OTHER_FLOAT_PRIZE excessOtherFloatPrize,").append("ment.POOL_TEMP_IN poolTempIn,")
                .append("ment.POOL_TEMP_OUT poolTempOut,").append("ment.POOL_TOTAL poolTotal");
        sb.append(" FROM T_LTTO_WINSTAT_DATA win ");
        sb.append(" LEFT JOIN T_LTTO_PROVINCE_SALES_DATA sale ");
        sb.append(" ON win.GAME_CODE = sale.GAME_CODE AND win.PERIOD_NUM = sale.PERIOD_NUM AND win.PROVINCE_ID = sale.PROVINCE_ID ");
        sb.append(" LEFT JOIN T_LTTO_LOTTERY_ANNOUNCEMENT ment ");
        sb.append(" ON win.GAME_CODE = ment.GAME_CODE AND win.PERIOD_NUM = ment.PERIOD_NUM ");
        sb.append("  WHERE ");
        sb.append(" win.GAME_CODE = ");
        sb.append(gameCode);
        sb.append("  AND win.PERIOD_NUM = ");
        sb.append(periodNum);
        sb.append(";");
        return sb.toString();
    }


    public static String creatTxtFile(List<HashMap<String, Object>> mapList){
        StringBuilder sb = new StringBuilder();
        for(HashMap<String, Object> map : mapList){
            sb.append(convertStr(map.get("gameCode"))+",");
            sb.append(convertStr(map.get("periodNum"))+",");
            sb.append(convertStr(map.get("provinceId"))+",");
            sb.append(convertStr(map.get("amount"))+",");
            sb.append(convertStr(map.get("cancelMoney"))+",");
            sb.append(convertStr(map.get("winNum"))+",");
            sb.append(convertStr(map.get("gradeCount"))+",");
            sb.append(convertStr(map.get("prize1count"))+",");
            sb.append(convertStr(map.get("prize1Money"))+",");
            sb.append(convertStr(map.get("prize2count"))+",");
            sb.append(convertStr(map.get("prize2Money"))+",");
            sb.append(convertStr(map.get("prize3count"))+",");
            sb.append(convertStr(map.get("prize3Money"))+",");
            sb.append(convertStr(map.get("prize4count"))+",");
            sb.append(convertStr(map.get("prize4Money"))+",");
            sb.append(convertStr(map.get("prize5count"))+",");
            sb.append(convertStr(map.get("prize5Money"))+",");
            sb.append(convertStr(map.get("prize6count"))+",");
            sb.append(convertStr(map.get("prize6Money"))+",");
            sb.append(convertStr(map.get("prize7count"))+",");
            sb.append(convertStr(map.get("prize7Money"))+",");
            sb.append(convertStr(map.get("prize8count"))+",");
            sb.append(convertStr(map.get("prize8Money"))+",");
            sb.append(convertStr(map.get("prize9count"))+",");
            sb.append(convertStr(map.get("prize9Money"))+",");
            sb.append(convertStr(map.get("prize10count"))+",");
            sb.append(convertStr(map.get("prize10Money"))+",");
            sb.append(Constant.Key.PROVINCEID_OF_CWL+",");
            sb.append(convertStr(map.get("saleMoneyTotal"))+",");
            sb.append(convertStr(map.get("overdueMoney"))+",");
            sb.append(convertStr(map.get("fundBgn"))+",");
            sb.append(convertStr(map.get("getInBalanceAll"))+",");
            sb.append(convertStr(map.get("getInBalance1"))+",");
            sb.append(convertStr(map.get("fund2PoolAutoAll"))+",");
            sb.append(convertStr(map.get("fund2PoolAuto1"))+",");
            sb.append(convertStr(map.get("fund2PoolHand"))+",");
            sb.append(convertStr(map.get("fund2Prize"))+",");
            sb.append(convertStr(map.get("fundTmpIn"))+",");
            sb.append(convertStr(map.get("fundTmpOut"))+",");
            sb.append(convertStr(map.get("fundTotal"))+",");
            sb.append(convertStr(map.get("poolBgn"))+",");
            sb.append(convertStr(map.get("poolCurrnt"))+",");
            sb.append(convertStr(map.get("unshotOtherFloatPrize"))+",");
            sb.append(convertStr(map.get("excessOtherFloatPrize"))+",");
            sb.append(convertStr(map.get("poolTempIn"))+",");
            sb.append(convertStr(map.get("poolTempOut"))+",");
            sb.append(convertStr(map.get("poolTotal"))+"\r\n");
        }
        return sb.toString();
    }

    private static String convertStr(Object obj){
        if(obj == null){
            return "0";
        }else{
            return obj.toString();
        }
    }

}
