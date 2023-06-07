package com.jku.dke.bac.ubsm.model.dto;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.Map;

public class StatisticDTO {
    private int runId;
    Map<String, Double> airspaceUserBalance;
    private double initialUtility;
    private double optimizedUtility;
    private double utilityIncrease;
    private Map<Slot, Flight> initialFlightList;
    private Map<Slot, Flight> optimizedFlightList;

    public StatisticDTO(int runId) {
        this.runId = runId;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public Map<String, Double> getAirspaceUserBalance() {
        return airspaceUserBalance;
    }

    public void setAirspaceUserBalance(Map<String, Double> airspaceUserBalance) {
        this.airspaceUserBalance = airspaceUserBalance;
    }

    public double getInitialUtility() {
        return initialUtility;
    }

    public void setInitialUtility(double initialUtility) {
        this.initialUtility = initialUtility;
    }

    public double getOptimizedUtility() {
        return optimizedUtility;
    }

    public void setOptimizedUtility(double optimizedUtility) {
        this.optimizedUtility = optimizedUtility;
    }

    public double getUtilityIncrease() {
        return utilityIncrease;
    }

    public void setUtilityIncrease(double utilityIncrease) {
        this.utilityIncrease = utilityIncrease;
    }

    public Map<Slot, Flight> getInitialFlightList() {
        return initialFlightList;
    }

    public void setInitialFlightList(Map<Slot, Flight> initialFlightList) {
        this.initialFlightList = initialFlightList;
    }

    public Map<Slot, Flight> getOptimizedFlightList() {
        return optimizedFlightList;
    }

    public void setOptimizedFlightList(Map<Slot, Flight> optimizedFlightList) {
        this.optimizedFlightList = optimizedFlightList;
    }
}
