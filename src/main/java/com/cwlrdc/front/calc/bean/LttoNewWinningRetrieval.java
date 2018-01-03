package com.cwlrdc.front.calc.bean;

import lombok.Data;

import java.util.Objects;

@Data
public class LttoNewWinningRetrieval implements Comparable<LttoNewWinningRetrieval>{

    private String gameCode;

    private String provinceId;

    private String periodNum;

    private Integer ticketType;

    private Integer prize1Count;


    private Integer prize2Count;


    private Integer prize3Count;


    private Integer prize4Count;


    private Integer prize5Count;


    private Integer prize6Count;


    private Integer prize7Count;


    private Integer prize8Count;


    private Integer prize9Count;


    private Integer prize10Count;


    private Integer prize11Count;


    private Integer prize12Count;


    private Integer prize13Count;


    private Integer prize14Count;


    private Integer prize15Count;


    private Integer prize16Count;


    private Integer prize17Count;


    private Integer prize18Count;


    private Integer prize19Count;


    private Integer prize20Count;


    private Integer prize21Count;


    private Integer prize22Count;


    private Integer prize23Count;


    private Integer prize24Count;


    private Integer prize25Count;


    private Integer prize26Count;


    private Integer prize27Count;


    private Integer prize28Count;


    private Integer prize29Count;


    private Integer prize30Count;


    private Integer processStatus;


    @Override
    public int compareTo(LttoNewWinningRetrieval o) {
        return this.getProvinceId().compareTo(o.getProvinceId());
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {return true;}
        if (!(o instanceof LttoNewWinningRetrieval)) {
            return false;
        }
        LttoNewWinningRetrieval user = (LttoNewWinningRetrieval) o;
        return provinceId == user.provinceId &&
                Objects.equals(gameCode, user.gameCode) &&
                Objects.equals(periodNum, user.periodNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameCode, periodNum, provinceId);
    }

}