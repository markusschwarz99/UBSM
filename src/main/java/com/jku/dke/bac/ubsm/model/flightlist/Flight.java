package com.jku.dke.bac.ubsm.model.flightlist;


import com.jku.dke.bac.ubsm.model.au.AirspaceUser;

import java.time.LocalTime;
import java.util.Map;

public class Flight {
    private AirspaceUser airspaceUser;
    private LocalTime initialTime;
    private LocalTime scheduledTime;
    private LocalTime wishedTime;
    private LocalTime notBefore;
    private LocalTime notAfter;
    private int priority;
    private FlightType flightType;
    private Map<Slot, Double> weightMap;
    private double initialUtility;
    private Double optimizedUtility;

    public Flight(AirspaceUser airspaceUser, LocalTime initialTime) {
        this.airspaceUser = airspaceUser;
        this.initialTime = initialTime;
    }

    public AirspaceUser getAirspaceUser() {
        return airspaceUser;
    }

    public void setAirspaceUser(AirspaceUser airspaceUser) {
        this.airspaceUser = airspaceUser;
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

    public LocalTime getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(LocalTime initialTime) {
        this.initialTime = initialTime;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public Map<Slot, Double> getWeightMap() {
        return weightMap;
    }

    public void setWeightMap(Map<Slot, Double> weightMap) {
        this.weightMap = weightMap;
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

    @Override
    public String toString() {
        return "Flight{" +
                "airspaceUser=" + airspaceUser +
                ", initialTime=" + initialTime +
                ", scheduledTime=" + scheduledTime +
                ", wishedTime=" + wishedTime +
                ", notBefore=" + notBefore +
                ", notAfter=" + notAfter +
                ", priority=" + priority +
                ", flightType=" + flightType +
                ", weightMap=" + weightMap +
                ", initialUtility=" + initialUtility +
                ", optimizedUtility=" + optimizedUtility +
                '}';
    }
}
