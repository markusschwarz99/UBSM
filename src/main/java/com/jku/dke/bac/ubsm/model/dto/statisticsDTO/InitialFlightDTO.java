package com.jku.dke.bac.ubsm.model.dto.statisticsDTO;

import java.time.LocalTime;

public class InitialFlightDTO {
    private LocalTime departureTime;
    private String au;
    private int flightId;

    public InitialFlightDTO(LocalTime departureTime, String au, int flightId) {
        this.departureTime = departureTime;
        this.au = au;
        this.flightId = flightId;
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
}
