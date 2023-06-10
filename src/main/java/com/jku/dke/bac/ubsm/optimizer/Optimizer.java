package com.jku.dke.bac.ubsm.optimizer;


import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class Optimizer {
    public Optimizer() {
    }

    public abstract Map<Slot, Flight> optimize(Map<Slot, Flight> flightList);

    protected List<Map<Slot, Flight>> getFeasibleFlightList(Map<Slot, Flight> toCheck, Map<Slot, Flight> notFeasibleFlights) {
        Logger.log("Optimizer - checking feasibility ...");
        if (isFullyImproved(toCheck)) {
            Logger.log("Optimizer - flightList is fully improved ...");
            toCheck.forEach((slot, flight) -> flight.setInOptimizationRun(true));
            notFeasibleFlights.forEach((slot, flight) -> flight.setInOptimizationRun(false));
            return List.of(toCheck, notFeasibleFlights);
        }
        Logger.log("Optimizer - flightList is not fully improved, starting a new iteration ...");

        List<Slot> slotsToRemove = toCheck.entrySet().stream()
                .filter(entry -> isInvalidWeightMap(entry.getValue().getWeightMap()) || !entry.getValue().isInOptimizationRun())
                .map(Map.Entry::getKey)
                .toList();
        slotsToRemove.forEach(slot -> notFeasibleFlights.put(slot, toCheck.remove(slot)));

        toCheck.values().forEach(flight -> {
            Map<Slot, Double> currWeightMap = flight.getWeightMap();
            slotsToRemove.forEach(currWeightMap.keySet()::remove);
        });

        return getFeasibleFlightList(toCheck, notFeasibleFlights);
    }

    protected boolean isFullyImproved(Map<Slot, Flight> slotFlightMap) {
        for (Flight flight : slotFlightMap.values()) {
            if (isInvalidWeightMap(flight.getWeightMap()) || !flight.isInOptimizationRun()) return false;
        }
        return true;
    }

    protected boolean isInvalidWeightMap(Map<Slot, Double> weightMap) {
        return weightMap.values().stream().filter(value -> value > 1).count() <= 1;
    }
}
