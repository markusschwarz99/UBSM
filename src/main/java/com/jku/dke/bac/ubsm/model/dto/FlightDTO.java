package com.jku.dke.bac.ubsm.model.dto;

import com.jku.dke.bac.ubsm.model.flightlist.FlightType;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.model.weightMapFunction.WeightMap;

import java.time.LocalTime;
import java.util.Map;
import java.util.function.Function;

public class FlightDTO {
    private String airspaceUser;
    private LocalTime initialTime;
    private LocalTime scheduledTime;
    private LocalTime wishedTime;
    private LocalTime notBefore;
    private LocalTime notAfter;
    private int priority;
    private FlightType flightType;
    private Function<Double, Double> weightMapFunction;
    private Map<Slot, Double> weightMap;

    public FlightDTO() {
    }

    public FlightDTO(String airspaceUser, LocalTime initialTime, LocalTime scheduledTime, LocalTime wishedTime, LocalTime notBefore, LocalTime notAfter, int priority, FlightType flightType, Function<Double, Double> weightMapFunction, Map<Slot, Double> weightMap) {
        this.airspaceUser = airspaceUser;
        this.initialTime = initialTime;
        this.scheduledTime = scheduledTime;
        this.wishedTime = wishedTime;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.priority = priority;
        this.flightType = flightType;
        this.weightMapFunction = weightMapFunction;
        this.weightMap = weightMap;
    }

    public String getAirspaceUser() {
        return airspaceUser;
    }

    public void setAirspaceUser(String airspaceUser) {
        this.airspaceUser = airspaceUser;
    }

    public LocalTime getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(LocalTime initialTime) {
        this.initialTime = initialTime;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalTime getWishedTime() {
        return wishedTime;
    }

    public void setWishedTime(LocalTime wishedTime) {
        this.wishedTime = wishedTime;
    }

    public LocalTime getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(LocalTime notBefore) {
        this.notBefore = notBefore;
    }

    public LocalTime getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(LocalTime notAfter) {
        this.notAfter = notAfter;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public Function<Double, Double> getWeightMapFunction() {
        return weightMapFunction;
    }

    public void setWeightMapFunction(Function<Double, Double> weightMapFunction) {
        this.weightMapFunction = weightMapFunction;
    }

    public Map<Slot, Double> getWeightMap() {
        return weightMap;
    }

    public void setWeightMap(Map<Slot, Double> weightMap) {
        this.weightMap = weightMap;
    }
}
