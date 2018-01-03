package com.cwlrdc.front.calc.util;

import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.Constant;
import com.cwlrdc.front.common.GamePeriodBatch;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 批量生成期号
 * 双色球每周销售三期，全国统一在每周二、四、日开奖
 * 七乐彩每周销售三期，全国统一在每周一、三、五开奖
 * Created by chenjia on 2017/6/1.
 */
@Slf4j
public class BatchGenPeriodNumUtil {

    /**
     * 生成期号
     */
    public static List<ParaGamePeriodInfo> generate(GamePeriodBatch info,
        List<String> suspendedlist, List<String> workingdaylist,
        List<String> nonworkdaylist, Map<String, Long> lastYearPeriodCancelWindateMap,
        Map<Integer, Integer> lastYearPeriodweekCountMap) {
        try {
            if (info != null) {
                List<ParaGamePeriodInfo> periodInfos = null;
                if (Constant.GameCode.GAME_CODE_SLTO.equals(info.getGameCode())) {
                    periodInfos = createSltoPeriod(info, suspendedlist, workingdaylist,
                        nonworkdaylist);
                    setPeekWeekAndOverDue(periodInfos, lastYearPeriodCancelWindateMap,
                        lastYearPeriodweekCountMap);
                } else if (Constant.GameCode.GAME_CODE_LOTO.equals(info.getGameCode())) {
                    periodInfos = createLotoPeriod(info, suspendedlist, workingdaylist,
                        nonworkdaylist);
                    setPeekWeekAndOverDue(periodInfos, lastYearPeriodCancelWindateMap,
                        lastYearPeriodweekCountMap);
                } else {
                    log.warn("批量生成期号错误,游戏编码错误[{}]", info.getGameCode());
                    return null;
                }
                if (periodInfos != null && periodInfos.size() > 1) {
                    return repeatAbandonedPrize(periodInfos);
                }
                return periodInfos;
            }
        } catch (Exception e) {
            log.debug("批量生成期号异常", e);
        }
        return null;
    }

    /**
     * 当前期和后期对比弃奖详情相同，则当前期设置为0
     * @param periodInfos
     * @return
     */
    private static List<ParaGamePeriodInfo> repeatAbandonedPrize(
        List<ParaGamePeriodInfo> periodInfos) {
        if (periodInfos == null || periodInfos.size() <= 1) {
            return periodInfos;
        }
        for (int i = 0; i < periodInfos.size() - 1; i++) {
            ParaGamePeriodInfo perInfo = periodInfos.get(i);
            ParaGamePeriodInfo lastOneInfo = periodInfos.get(i+1);

            if(StringUtils.equals(perInfo.getOverduePeriod(),lastOneInfo.getOverduePeriod())){
                perInfo.setOverduePeriod("0");
            }
        }
        return periodInfos;

    }

    public static List<ParaGamePeriodInfo> createSltoPeriod(GamePeriodBatch info,
        List<String> suspenedlist, List<String> workdaylist, List<String> nonworkdaylist) {
        List<ParaGamePeriodInfo> result = new ArrayList<>();
        int cashDeadline = info.getCashDeadline();
        String gameCode = info.getGameCode();
        String startDateFirst = info.getStartTime().trim();
        String startdate = info.getStartTime().trim();
        String enddate = info.getEndTime().trim();
        int periodnum = 0;
        int dayofweek = 0;
        boolean isIndeadlinestop = false;
        boolean isNewPeriodstop = false;
        boolean isEndPeriodStop = false;
        List<String> suspendmatchlist = new ArrayList<String>();
        Calendar calendparse = null;
        if (suspenedlist.size() > 0) {
            Map<String, List<String>> suspendMap = new HashMap<String, List<String>>();
            for (String suspend : suspenedlist) {
                String year = suspend.substring(0, 4);
                if (!suspendMap.containsKey(year)) {
                    List<String> maplist = new ArrayList<String>();
                    suspendMap.put(year, maplist);
                }
                suspendMap.get(year).add(suspend);
            }
            String enddateyear = enddate.substring(0, 4);
            suspendmatchlist = suspendMap.get(enddateyear);
            Calendar calParse = Calendar.getInstance();
            if (suspendmatchlist != null) {
            	Collections.sort(suspendmatchlist);
            	calParse.setTime(DateUtil.parseDate(suspendmatchlist.get(0), "yyyy-MM-dd"));
            } else {
            	calParse.setTime(DateUtil.parseDate(enddate, "yyyy-MM-dd"));
            }
            dayofweek = calParse.get(Calendar.DAY_OF_WEEK);
            if (dayofweek == Calendar.SUNDAY || dayofweek == Calendar.TUESDAY
                || dayofweek == Calendar.THURSDAY) {
                isIndeadlinestop = true;
            }
            if (dayofweek == Calendar.MONDAY || dayofweek == Calendar.WEDNESDAY
                || dayofweek == Calendar.FRIDAY) {
                isNewPeriodstop = true;
            }
            calendparse = Calendar.getInstance();
            calendparse.setTime(DateUtil
                .parseDate(suspendmatchlist.get(suspendmatchlist.size() - 1), "yyyy-MM-dd"));
            int endparsedayofweek = calendparse.get(Calendar.DAY_OF_WEEK);

            if (endparsedayofweek == Calendar.SUNDAY || endparsedayofweek == Calendar.TUESDAY
                || endparsedayofweek == Calendar.THURSDAY) {
                isEndPeriodStop = true;
            }
        }

        Calendar cal = DateUtils.toCalendar(DateUtil.parseDate(startdate, "yyyy-MM-dd"));
        Calendar calend = DateUtils.toCalendar(DateUtil.parseDate(enddate, "yyyy-MM-dd"));

        int suspendeddays = 0;
        String lastPeriodEndTime = "";
        while (cal.compareTo(calend) <= 0) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            String currentday = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
            if (suspendmatchlist.contains(currentday)) {
                suspendeddays++;
                continue;
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                continue;
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                if (suspendeddays == 0) {
                    periodnum++;
                    startdate = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
                } else if (isNewPeriodstop && isEndPeriodStop) {
                    periodnum++;
                    startdate = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
                    suspendeddays = 0;
                } else if (isIndeadlinestop) {
                    isIndeadlinestop = false;
                    suspendeddays = 0;
                    if (!isEndPeriodStop) {
                        periodnum++;
                        startdate = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
                    }
                }
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                if (suspendeddays != 0 && isNewPeriodstop) {
                    isNewPeriodstop = false;
                    suspendeddays = 0;
                    continue;
                }
                if (suspendeddays != 0 && calendparse != null && cal.compareTo(calendparse) > 0) {
                    suspendeddays = 0;
                }
                if (periodnum == 1) {
                    lastPeriodEndTime = startDateFirst;
                }
                boolean firstPeriod = (result.size() == 0) ? true : false;
                ParaGamePeriodInfo drawnumber = createNewPeriod(gameCode, cal, periodnum,
                    cashDeadline, workdaylist, nonworkdaylist, lastPeriodEndTime, firstPeriod);
                if (drawnumber != null) {
                    result.add(drawnumber);
                }
                lastPeriodEndTime = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
            }
        }
        return result;
    }

    public static List<ParaGamePeriodInfo> createLotoPeriod(GamePeriodBatch info,
        List<String> suspenedlist, List<String> workdaylist, List<String> nonworkdaylist) {
        List<ParaGamePeriodInfo> result = new ArrayList<>();
        String startdate = info.getStartTime();
        String startDateFirst = info.getStartTime().trim();
        String enddate = info.getEndTime();
        int periodnum = 0;
        int cashDeadline = info.getCashDeadline();
        String gameCode = info.getGameCode();

        boolean isIndeadlinestop = false;
        boolean isNewPeriodstop = false;
        boolean isEndPeriodStop = false;
        List<String> suspendmatchlist = new ArrayList<String>();
        Calendar calendparse = null;
        if (suspenedlist.size() > 0) {
            Map<String, List<String>> suspendMap = new HashMap<String, List<String>>();
            for (String suspend : suspenedlist) {
                String year = suspend.substring(0, 4);
                if (!suspendMap.containsKey(year)) {
                    List<String> maplist = new ArrayList<String>();
                    suspendMap.put(year, maplist);
                }
                suspendMap.get(year).add(suspend);
            }
            String enddateyear = enddate.substring(0, 4);
            suspendmatchlist = suspendMap.get(enddateyear);
            Collections.sort(suspendmatchlist);
            Calendar calParse = Calendar.getInstance();
            calParse.setTime(DateUtil.parseDate(suspendmatchlist.get(0), "yyyy-MM-dd"));
            int dayofweek = calParse.get(Calendar.DAY_OF_WEEK);

            if (dayofweek == Calendar.MONDAY || dayofweek == Calendar.WEDNESDAY
                || dayofweek == Calendar.FRIDAY) {
                isIndeadlinestop = true;
            }
            if (dayofweek == Calendar.TUESDAY || dayofweek == Calendar.THURSDAY
                || dayofweek == Calendar.SATURDAY) {
                isNewPeriodstop = true;
            }
            calendparse = Calendar.getInstance();
            calendparse.setTime(DateUtil
                .parseDate(suspendmatchlist.get(suspendmatchlist.size() - 1), "yyyy-MM-dd"));
            int endparsedayofweek = calendparse.get(Calendar.DAY_OF_WEEK);
            if (endparsedayofweek == Calendar.MONDAY || endparsedayofweek == Calendar.WEDNESDAY
                || endparsedayofweek == Calendar.FRIDAY) {
                isEndPeriodStop = true;
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.parseDate(startdate, "yyyy-MM-dd"));

        Calendar calend = Calendar.getInstance();
        calend.setTime(DateUtil.parseDate(enddate, "yyyy-MM-dd"));
        int stopdays = 0;
        String lastPeriodEndTime = "";
        while (cal.compareTo(calend) <= 0) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            String currentday = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
            if (suspendmatchlist.contains(currentday)) {
                stopdays++;
                continue;
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                continue;
            }
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                if (stopdays == 0) {
                    periodnum++;
                    startdate = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
                } else if (isNewPeriodstop && isEndPeriodStop) {
                    periodnum++;
                    startdate = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
                    stopdays = 0;
                } else if (isIndeadlinestop) {
                    isIndeadlinestop = false;
                    stopdays = 0;
                    if (!isEndPeriodStop) {
                        periodnum++;
                        startdate = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
                    }
                }
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                if (stopdays != 0 && isNewPeriodstop) {
                    isNewPeriodstop = false;
                    stopdays = 0;
                    continue;
                }
                if (periodnum == 1) {
                    lastPeriodEndTime = startDateFirst;
                }
                if (stopdays != 0 && calendparse != null && cal.compareTo(calendparse) > 0) {
                    stopdays = 0;
                }
                boolean firstPeriod = (result.size() == 0) ? true : false;
                ParaGamePeriodInfo drawnumber = createNewPeriod(gameCode, cal, periodnum,
                    cashDeadline, workdaylist, nonworkdaylist, lastPeriodEndTime, firstPeriod);
                if (drawnumber != null) {
                    result.add(drawnumber);
                }
                lastPeriodEndTime = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
            }
        }
        return result;
    }

    private static ParaGamePeriodInfo createNewPeriod(String gameCode, Calendar cal, int periodnum,
        int cashDeadline, List<String> workdaylist, List<String> nonworkdaylist,
        String lastPeriodEndTime, boolean firstPeriod) {
        String year = cal.get(Calendar.YEAR) + "";
        String period = convertPeriod(periodnum);

        Calendar periodstart = Calendar.getInstance();
        periodstart.setTime(DateUtil.parseDate(lastPeriodEndTime, "yyyy-MM-dd"));
        // 期长
        long perioddurationlongvalue = cal.getTime().getTime() - periodstart.getTime().getTime();
        long periodduration = perioddurationlongvalue / (1000 * 3600 * 24) + 1;

        Date deadlinedate = cal.getTime();
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTime(deadlinedate);

        for (int i = 0; i < cashDeadline; i++) {
            tmpCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (int i = 0; i < cashDeadline; i++) {
            String hopeabandoncal = DateUtil.formatDate(tmpCal.getTime(), "yyyy-MM-dd");
            int hopeDayOfWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
            if (workdaylist.contains(hopeabandoncal) ||
                (hopeDayOfWeek != Calendar.SUNDAY && hopeDayOfWeek != Calendar.SATURDAY
                    && !nonworkdaylist.contains(hopeabandoncal))) {
                break;
            } else {
                tmpCal.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        if (Integer.parseInt(year) - Integer.parseInt(lastPeriodEndTime.substring(0, 4)) == 1) {
            //跨年 期号归下一年 001
            if (firstPeriod) {
                period = "001";
            } else {
                return null;
            }
        }
        long durationlongvalue = tmpCal.getTimeInMillis() - deadlinedate.getTime();
        long cashdeadlineduration = durationlongvalue / (1000 * 3600 * 24) + 1;
        ;
        ParaGamePeriodInfo drawnumber = buildDrawNumberinfo(gameCode, year + period,
            lastPeriodEndTime, (int) periodduration, cal.getTime(), tmpCal.getTime(),
            (int) cashdeadlineduration);
        return drawnumber;
    }

    private static ParaGamePeriodInfo buildDrawNumberinfo(String gameCode, String periodnum,
        String lastPeriodEndTime,
        int periodduration, Date periodendTime, Date cashEndTime, int cashDeadline) {
        ParaGamePeriodInfo drawnumber = new ParaGamePeriodInfo();
        drawnumber.setGameCode(gameCode);
        drawnumber.setPeriodNum(periodnum);
        drawnumber.setProvinceId(Constant.Status.CWL_CODE_00);
        drawnumber.setPeriodBeginTime(lastPeriodEndTime);
        drawnumber.setPeriodCycle(periodduration);
        drawnumber.setPeriodEndTime(DateUtil.formatDate(periodendTime, "yyyy-MM-dd"));
        drawnumber.setPeriodMonth(DateUtil.day2MonthInYear(periodendTime));
        drawnumber.setPeriodYear(Integer.valueOf(DateFormatUtils.format(periodendTime, "yyyy")));
        drawnumber.setCashEndTime(DateUtil.formatDate(cashEndTime, "yyyy-MM-dd"));
        drawnumber.setCancelWinDate(cashEndTime.getTime());
        drawnumber.setLotteryDate(drawnumber.getPeriodEndTime());
        drawnumber.setStatus(0);
        drawnumber.setCashTerm(cashDeadline);
        drawnumber.setPromotionStatus(Constant.Status.WIN_PROMOTION_STATUS_NO);
        drawnumber.setFlowNode(String.valueOf(Constant.Status.TASK_LTTOERY_FLOW_0));
        return drawnumber;
    }

    private static void setPeekWeekAndOverDue(List<ParaGamePeriodInfo> periods,
        Map<String, Long> lastYearPeriodCancelWindateMap,
        Map<Integer, Integer> lastYearPeriodweekCountMap) {
        int periodweek = 1;
        int maxcount = 3;
        int index = 1;
        Map<String, Long> periodcancelDateMap = new HashMap<String, Long>();
        periodcancelDateMap.putAll(lastYearPeriodCancelWindateMap);

        int paddingperiodweek = 0;
        int lastmaxperiodweek = 0;
        List<Integer> periodweeklist = new ArrayList<>(lastYearPeriodweekCountMap.keySet());
        if (periodweeklist != null && periodweeklist.size() > 0) {
            Collections.sort(periodweeklist);
            Collections.reverse(periodweeklist);
            lastmaxperiodweek = periodweeklist.get(0);
            int lastyearmaxperiodweekcount = lastYearPeriodweekCountMap.get(lastmaxperiodweek);
            if (lastyearmaxperiodweekcount != 3) {
                paddingperiodweek = 3 - lastyearmaxperiodweekcount;
            }
        }
        for (ParaGamePeriodInfo period : periods) {
            periodcancelDateMap.put(period.getPeriodNum(), period.getCancelWinDate());
            if (paddingperiodweek <= 0) {
                period.setPeriodWeek(periodweek);
                index++;
                if (index > maxcount) {
                    index = 1;
                    periodweek++;
                }
            } else {
                period.setPeriodWeek(lastmaxperiodweek);
                paddingperiodweek--;
            }
        }

        for (ParaGamePeriodInfo period : periods) {
            String startdate = period.getPeriodBeginTime();
            String enddate = period.getPeriodEndTime();
            long startdatelong = DateUtil.parseDate(startdate, "yyyy-MM-dd").getTime();
            long enddatelong = DateUtil.parseDate(enddate, "yyyy-MM-dd").getTime();
            int periodcount = 0;
            String overdueperiod = periodcount + "";
            List<String> periodList = new ArrayList<String>();
            period.setOverduePeriod("0");
            for (Map.Entry<String, Long> entry : periodcancelDateMap.entrySet()) {
                String cancelperiod = entry.getKey();
                long canceldate = entry.getValue();
                if (canceldate >= startdatelong && canceldate < enddatelong) {
                    periodcount++;
                    periodList.add(cancelperiod);
                }
            }
            if (periodList.size() > 0) {
                Collections.sort(periodList);
                if (periodcount < 10) {
                    overdueperiod = "000" + periodcount;
                } else if (periodcount < 100) {
                    overdueperiod = "00" + periodcount;
                } else if (periodcount < 1000) {
                    overdueperiod = "0" + periodcount;
                }
                period.setOverduePeriod(overdueperiod + periodList.get(0));
            }
        }
    }

    public static String convertPeriod(int period) {
        String str = "";
        if (period < 10) {
            str = "00" + period;
        } else if (period < 100) {
            str = "0" + period;
        } else {
            str = period + "";
        }
        return str;
    }

}
