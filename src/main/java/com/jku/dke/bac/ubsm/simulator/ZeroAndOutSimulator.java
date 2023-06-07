package com.jku.dke.bac.ubsm.simulator;

import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.Map;

public class ZeroAndOutSimulator extends Simulator{
    @Override
    public void clearing(Map<Slot, Flight> optimizedFlightList, double utilityIncrease) {
        optimizedFlightList.values().forEach(flight -> {
            if (flight.getOptimizedUtility() != null) {
                Logger.log(Double.toString(flight.getOptimizedUtility() - flight.getInitialUtility() * (1 + utilityIncrease)));
                flight.getAirspaceUser().updateCredits(flight.getOptimizedUtility() - flight.getInitialUtility() * (1 + utilityIncrease));
            }
        });
    }
}
