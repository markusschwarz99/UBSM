package com.jku.dke.bac.ubsm.model.dto.statisticsDTO;

import java.util.Map;

public class AuBalanceDTO {
    private Map<String, Double> balanceBefore;
    private Map<String, Double> balanceAfter;

    public AuBalanceDTO() {
    }

    public AuBalanceDTO(Map<String, Double> balanceBefore, Map<String, Double> balanceAfter) {
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
    }

    public Map<String, Double> getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Map<String, Double> balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public Map<String, Double> getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Map<String, Double> balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
}
