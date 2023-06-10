package com.jku.dke.bac.ubsm.model.dto.statisticsDTO;


import java.util.Map;

public class OverviewDTO {
    private int totalRuns;
    private String optimizer;
    private String simulator;
    private Map<Integer, AuBalanceDTO> balances;

    public OverviewDTO() {
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public Map<Integer, AuBalanceDTO> getBalances() {
        return balances;
    }

    public void setBalances(Map<Integer, AuBalanceDTO> balances) {
        this.balances = balances;
    }

    public String getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public String getSimulator() {
        return simulator;
    }

    public void setSimulator(String simulator) {
        this.simulator = simulator;
    }
}
