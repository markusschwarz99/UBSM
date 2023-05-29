package com.jku.dke.bac.ubsm.optimizer;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class HungarianOptimizer extends Optimizer {
    @Override
    public Map<Slot, Flight> optimize(Map<Slot, Flight> initialFlightList) {
        List<Map<Slot, Flight>> feasibleFlightLists = getFeasibleFlightList(initialFlightList, new LinkedHashMap<>());

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
        AtomicReference<Double> initialUtility = new AtomicReference<>((double) 0);
        AtomicReference<Double> optimalUtility = new AtomicReference<>((double) 0);
        feasibleFlightLists.get(0).forEach((initialSlot, flight) -> {
            Slot[] slots = flight.getWeightMap().keySet().toArray(new Slot[0]);
            Slot grantedSlot = slots[result[j.getAndIncrement()]];

            double valueToAdd;
            if (flight.getWeightMap().get(initialSlot) == -Double.MAX_VALUE) {
                valueToAdd = -1000 * flight.getPriority();
            } else {
                valueToAdd = flight.getWeightMap().get(initialSlot);
            }

            double finalValueToAdd = valueToAdd;
            initialUtility.updateAndGet(currValue -> currValue + finalValueToAdd);
            System.out.println("initialutility = " + initialUtility.get() + " + " + valueToAdd);

            if (flight.getWeightMap().get(grantedSlot) == -Double.MAX_VALUE) {
                valueToAdd = -1000 * flight.getPriority();
            } else {
                valueToAdd = flight.getWeightMap().get(grantedSlot);
            }
            double finalValueToAdd1 = valueToAdd;
            optimalUtility.getAndUpdate(currValue -> currValue + finalValueToAdd1);
            System.out.println("optimalUtility = " + optimalUtility.get() + " + " + valueToAdd);

            System.out.println("initialUtility: " + initialUtility.get());
            System.out.println("optimalUtility: " + optimalUtility.get());
            optimizedFlightList.put(grantedSlot, flight);
        });
        System.out.println("---------------------------");
        System.out.println("initialUtility: " + initialUtility.get());
        System.out.println("optimalUtility: " + optimalUtility.get());

        return optimizedFlightList.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getDepartureTime().toSecondOfDay()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (slot, flight) -> slot, LinkedHashMap::new));
    }
}
