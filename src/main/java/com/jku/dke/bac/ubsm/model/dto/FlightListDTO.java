package com.jku.dke.bac.ubsm.model.dto;

import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.Map;

public class FlightListDTO {
    int flightListId;
    Map<Slot, FlightDTO> flightList;

    public FlightListDTO() {
    }

    public FlightListDTO(int flightListId) {
        this.flightListId = flightListId;
    }

    public FlightListDTO(int flightListId, Map<Slot, FlightDTO> flightList) {
        this.flightListId = flightListId;
        this.flightList = flightList;
    }

    public int getFlightListId() {
        return flightListId;
    }

    public void setFlightListId(int flightListId) {
        this.flightListId = flightListId;
    }

    public Map<Slot, FlightDTO> getFlightList() {
        return flightList;
    }

    public void setFlightList(Map<Slot, FlightDTO> flightList) {
        this.flightList = flightList;
    }
}
