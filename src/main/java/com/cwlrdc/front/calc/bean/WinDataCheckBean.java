package com.cwlrdc.front.calc.bean;


import lombok.Data;

import java.util.Objects;

/**
 * Created by chenjia on 2017/4/12.
 */
@Data
public class WinDataCheckBean implements Comparable<WinDataCheckBean> {

    private String gameCode;
    private String periodNum;
    private String provinceId;

    //省上报结果
    private Integer provinceWin1;
    private Integer provinceWin2;
    private Integer provinceWin3;
    private Integer provinceWin4;
    private Integer provinceWin5;
    private Integer provinceWin6;
    private Integer provinceWin7;
    private Integer provinceWin8;
    private Integer provinceWin9;
    private Integer provinceWin10;
    //中奖检索结果
    private Integer smWin1;
    private Integer smWin2;
    private Integer smWin3;
    private Integer smWin4;
    private Integer smWin5;
    private Integer smWin6;
    private Integer smWin7;
    private Integer smWin8;
    private Integer smWin9;
    private Integer smWin10;

    private Integer status; //0:正确 1:错误

    @Override
    public int compareTo(WinDataCheckBean o) {
        return this.getProvinceId().compareTo(o.getProvinceId());
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {return true;}
        if (!(o instanceof WinDataCheckBean)) {
            return false;
        }
        WinDataCheckBean user = (WinDataCheckBean) o;
        return provinceId == user.provinceId &&
                Objects.equals(gameCode, user.gameCode) &&
                Objects.equals(periodNum, user.periodNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameCode, periodNum, provinceId);
    }
}