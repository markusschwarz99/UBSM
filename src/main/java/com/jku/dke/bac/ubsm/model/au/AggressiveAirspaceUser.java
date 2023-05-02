package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.List;
import java.util.Map;

public class AggressiveAirspaceUser extends AirspaceUser {
    //@JsonProperty("type")
    //private final String airspaceUserType = "Aggressive";

    @Override
    public String toString() {
        return super.toString() + " - Aggressive";
    }


    @Override
    public Map<Slot, Double> generateWeightMap(Flight flight) {
        return null;
    }

    @Override
    public void generateFlightAttributes(Flight flight, List<Slot> possibleSlots) {

    }
}
