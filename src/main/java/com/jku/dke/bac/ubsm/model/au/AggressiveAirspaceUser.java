package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.FlightType;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AggressiveAirspaceUser extends AirspaceUser {

    @Override
    public String toString() {
        return super.toString() + " - Aggressive";
    }

    @Override
    public void generateWeightMap(Flight flight, List<Slot> possibleSlots) {

    }

    @Override
    protected void generateTimes(Flight flight, List<Slot> possibleSlots) {
        if (flight.getFlightType() == FlightType.PRIORITY) {
            generatePriorityTimes(flight, possibleSlots, 30);
        } else if (flight.getFlightType() == FlightType.FLEXIBLE) {
            generateFlexibleTimes(flight, possibleSlots, 0.15, 0.30, 0.60);
        } else if (flight.getFlightType() == FlightType.FLEXIBLEWITHPRIORITY) {
            generateFlexibleWithPriorityTimes(flight, possibleSlots, 0.15, 0.30);
        }
    }
}
