package com.jku.dke.bac.ubsm.simulator;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InflationSimulator extends Simulator {
    private final double inflation;
    private final double initialCredits;

    public InflationSimulator(double inflation, double initialCredits) {
        this.inflation = inflation;
        this.initialCredits = initialCredits;
    }

    @Override
    public void clearing(Map<Slot, Flight> optimizedFlightList, double utilityIncrease) {
        optimizedFlightList.values().forEach(flight -> {
            if (flight.isInOptimizationRun()) {
                double compensation = flight.getOptimizedUtility() - (flight.getInitialUtility() + (flight.getInitialUtility() * utilityIncrease));
                flight.setCost(-compensation);
                flight.getAirspaceUser().updateCredits(-compensation);
            }
        });
        List<AirspaceUser> airspaceUsers = new ArrayList<>(new HashSet<>(optimizedFlightList.values()
                .stream()
                .map(Flight::getAirspaceUser)
                .collect(Collectors.toList())));
        inflate(airspaceUsers);
    }

    private void inflate(List<AirspaceUser> airspaceUsers) {
        airspaceUsers.forEach(airspaceUser -> airspaceUser.updateCredits(initialCredits * inflation));
    }
}
