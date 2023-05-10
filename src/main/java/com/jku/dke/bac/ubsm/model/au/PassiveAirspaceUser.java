package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.FlightType;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.List;

public class PassiveAirspaceUser extends AirspaceUser {

    @Override
    public String toString() {
        return super.toString() + " - Passive";
    }

    @Override
    public void generateWeightMap(Flight flight, List<Slot> possibleSlots) {

    }

    @Override
    protected void generateTimes(Flight flight, List<Slot> possibleSlots) {
        if (flight.getFlightType() == FlightType.PRIORITY) {
            generatePriorityTimes(flight, possibleSlots, 90);
        } else if (flight.getFlightType() == FlightType.FLEXIBLE) {
            generateFlexibleTimes(flight, possibleSlots, 0.25, 0.45, 0.90);
        } else if (flight.getFlightType() == FlightType.FLEXIBLEWITHPRIORITY) {
            generateFlexibleWithPriorityTimes(flight, possibleSlots, 0.35, 0.55);
        }
    }
}
