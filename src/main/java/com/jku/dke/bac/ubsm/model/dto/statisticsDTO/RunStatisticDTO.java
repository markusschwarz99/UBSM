package com.jku.dke.bac.ubsm.model.dto.statisticsDTO;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunStatisticDTO {
    private int runId;
    private double totalInitialUtility;
    private double totalOptimizedUtility;
    private double utilityIncrease;
    private List<InitialFlightDTO> initialFlightList;
    private List<OptimizedFlightDTO> optimizedFlightList;

    public RunStatisticDTO(int runId) {
        this.runId = runId;
        this.initialFlightList = new ArrayList<>();
        this.optimizedFlightList = new ArrayList<>();
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public List<InitialFlightDTO> getInitialFlightList() {
        return initialFlightList;
    }

    public void setInitialFlightList(Map<Slot, Flight> initialFlightList) {
        initialFlightList.forEach((slot, flight) -> this.initialFlightList.add(new InitialFlightDTO(slot.getDepartureTime(), flight.getAirspaceUser().getName(), flight.getId())));
    }

    public List<OptimizedFlightDTO> getOptimizedFlightList() {
        return optimizedFlightList;
    }

    public void setOptimizedFlightList(Map<Slot, Flight> optimizedFlightList) {
        optimizedFlightList.forEach((slot, flight) -> {
            this.optimizedFlightList.add(new OptimizedFlightDTO(slot.getDepartureTime(), flight.getAirspaceUser().getName(), flight.getId(), flight.getInitialUtility(), flight.getOptimizedUtility(), flight.getCost() ,flight.isInOptimizationRun()));
        });
    }

    public double getTotalInitialUtility() {
        return totalInitialUtility;
    }

    public void setTotalInitialUtility(double totalInitialUtility) {
        this.totalInitialUtility = totalInitialUtility;
    }

    public double getTotalOptimizedUtility() {
        return totalOptimizedUtility;
    }

    public void setTotalOptimizedUtility(double totalOptimizedUtility) {
        this.totalOptimizedUtility = totalOptimizedUtility;
    }

    public double getUtilityIncrease() {
        return utilityIncrease;
    }

    public void setUtilityIncrease(double utilityIncrease) {
        this.utilityIncrease = utilityIncrease;
    }
}
