package com.jku.dke.bac.ubsm.optimizer;


import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class HungarianOptimizer extends Optimizer {
    public HungarianOptimizer() {
        super();
    }

    @Override
    public Map<Slot, Flight> optimize(Map<Slot, Flight> initialFlightList) {
        Logger.log("HungarianOptimizer - start optimization ...");
        List<Map<Slot, Flight>> feasibleFlightLists = getFeasibleFlightList(initialFlightList, new LinkedHashMap<>());

        Logger.log("HungarianOptimizer - feasibleFlights: " + feasibleFlightLists.get(0).size() + " ...");
        Logger.log("HungarianOptimizer - not feasibleFlights: " + feasibleFlightLists.get(1).size() + " ...");

        if (feasibleFlightLists.get(0).size() == 0){
            return null;
        }

        int length = feasibleFlightLists.get(0).size();
        double[][] input = new double[length][length];
        AtomicInteger i = new AtomicInteger();
        feasibleFlightLists.get(0).forEach((slot, flight) ->
                input[i.getAndIncrement()] = flight.getWeightMap().values().stream()
                        .mapToDouble(value -> value * -1)
                        .toArray());

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(input);
        int[] result = hungarianAlgorithm.execute();

        Map<Slot, Flight> optimizedFlightList = new LinkedHashMap<>(feasibleFlightLists.get(1));
        AtomicInteger j = new AtomicInteger();
        feasibleFlightLists.get(0).forEach((initialSlot, flight) -> {
            Slot[] slots = flight.getWeightMap().keySet().toArray(new Slot[0]);
            Slot grantedSlot = slots[result[j.getAndIncrement()]];
            flight.setInitialUtility(flight.getWeightMap().get(initialSlot));
            flight.setOptimizedUtility(flight.getWeightMap().get(grantedSlot));
            optimizedFlightList.put(grantedSlot, flight);
        });

        Logger.log("HungarianOptimizer - optimization finished ...");
        return optimizedFlightList.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getDepartureTime().toSecondOfDay()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (slot, flight) -> slot, LinkedHashMap::new));
    }
}
