package com.jku.dke.bac.ubsm.model.dto.statisticsDTO;

import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.time.LocalTime;
import java.util.Map;

public class OptimizedFlightDTO {
    private LocalTime departureTime;
    private String au;
    private int flightId;
    private double initialUtility;
    private Double optimizedUtility;
    private double cost;
    private boolean isOptimized;
    private Map<Slot, Double> weightMap;

    public OptimizedFlightDTO(LocalTime departureTime, String au, int flightId, double initialUtility, Double optimizedUtility,double cost ,boolean isOptimized, Map<Slot, Double> weightMap) {
        this.departureTime = departureTime;
        this.au = au;
        this.flightId = flightId;
        this.initialUtility = initialUtility;
        this.optimizedUtility = optimizedUtility;
        this.cost = cost;
        this.isOptimized = isOptimized;
        this.weightMap = weightMap;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public String getAu() {
        return au;
    }

    public void setAu(String au) {
        this.au = au;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public double getInitialUtility() {
        return initialUtility;
    }

    public void setInitialUtility(double initialUtility) {
        this.initialUtility = initialUtility;
    }

    public Double getOptimizedUtility() {
        return optimizedUtility;
    }

    public void setOptimizedUtility(Double optimizedUtility) {
        this.optimizedUtility = optimizedUtility;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isOptimized() {
        return isOptimized;
    }

    public void setOptimized(boolean optimized) {
        isOptimized = optimized;
    }

    public Map<Slot, Double> getWeightMap() {
        return weightMap;
    }

    public void setWeightMap(Map<Slot, Double> weightMap) {
        this.weightMap = weightMap;
    }
}

