package com.jku.dke.bac.ubsm.simulator;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.Map;

public class ZeroAndOutSimulator extends Simulator {
    @Override
    public void clearing(Map<Slot, Flight> optimizedFlightList, double utilityIncrease) {
        optimizedFlightList.values().forEach(flight -> {
            if (flight.getOptimizedUtility() != null) {
                double compensation = flight.getOptimizedUtility() - (flight.getInitialUtility() + (flight.getInitialUtility() * utilityIncrease));
                flight.setCost(compensation);
                flight.getAirspaceUser().updateCredits(compensation);
            }
        });
    }
}
