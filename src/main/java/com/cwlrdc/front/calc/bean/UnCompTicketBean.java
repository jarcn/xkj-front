package com.cwlrdc.front.calc.bean;

import lombok.Data;

import java.util.Objects;

/**
 * Created by chenjia on 2017/5/6.
 */
@Data
public class UnCompTicketBean implements Comparable<UnCompTicketBean>{

    private String gameCode;
    private String periodNum;
    private String provinceId;

    private Integer uncomp1Count;
    private Integer uncomp2Count;
    private Integer uncomp3Count;
    private Integer uncomp4Count;
    private Integer uncomp5Count;
    private Integer uncomp6Count;
    private Integer uncomp7Count;
    private Integer uncomp8Count;
    private Integer uncomp9Count;
    private Integer uncomp10Count;

    @Override
    public int compareTo(UnCompTicketBean o) {
        return this.getProvinceId().compareTo(o.getProvinceId());
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {return true;}
        if (!(o instanceof UnCompTicketBean)) {
            return false;
        }
        UnCompTicketBean user = (UnCompTicketBean) o;
        return provinceId == user.provinceId &&
                Objects.equals(gameCode, user.gameCode) &&
                Objects.equals(periodNum, user.periodNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameCode, periodNum, provinceId);
    }
}
