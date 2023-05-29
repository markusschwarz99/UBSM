package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.model.dto.FlightListDTO;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.model.mapper.Mapper;
import com.jku.dke.bac.ubsm.optimizer.HungarianAlgorithm;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OptimizerService {
    private final FlightListService flightListService;

    public OptimizerService(final FlightListService flightListService) {
        this.flightListService = flightListService;
    }

    public FlightListDTO optimizeFlightList() {
        if (flightListService.getFlightLists().size() == 0)
            throw new IllegalArgumentException("There are 0 flightLists");

        Map<Slot, Flight> initialFlightList = flightListService.getFlightLists().get(flightListService.getFlightLists().size() - 1);

        return getOptimizedFlightList(initialFlightList);
    }

    public FlightListDTO optimizeFlightList(int index) {
        if (index < 0 || index >= flightListService.getFlightLists().size())
            throw new IllegalArgumentException("Invalid index");

        Map<Slot, Flight> initialFlightList = flightListService.getFlightLists().get(index);

        return getOptimizedFlightList(initialFlightList);
    }

    private FlightListDTO getOptimizedFlightList(Map<Slot, Flight> initialFlightList) {
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
        System.out.println(Arrays.toString(result));

        Map<Slot, Flight> optimizedFlightList = new LinkedHashMap<>(feasibleFlightLists.get(1));
        Slot[] optimizedSlots = feasibleFlightLists.get(0).keySet().toArray(new Slot[0]);
        AtomicInteger j = new AtomicInteger();
        AtomicReference<Double> optimalUtility = new AtomicReference<>((double) 0);
        feasibleFlightLists.get(0).forEach((initialSlot, flight) -> {
            Slot[] slots= flight.getWeightMap().keySet().toArray(new Slot[0]);
            Slot grantedSlot = slots[result[j.getAndIncrement()]];
            optimalUtility.set(flight.getWeightMap().get(grantedSlot) + optimalUtility.get());
            System.out.println("weight:" + flight.getWeightMap().get(grantedSlot));
            optimizedFlightList.put(grantedSlot, flight);
        });

        System.out.println(optimalUtility);
        Map<Slot, Flight> sortedOptimizedFlightList = optimizedFlightList.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getDepartureTime().toSecondOfDay()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (slot, flight) -> slot, LinkedHashMap::new));
        return Mapper.mapFlightListToFlightListDTO(0, sortedOptimizedFlightList);
    }

    private List<Map<Slot, Flight>> getFeasibleFlightList(Map<Slot, Flight> toCheck, Map<Slot, Flight> notFeasibleFlights) {
        if (isFullyImproved(toCheck)) return List.of(toCheck, notFeasibleFlights);
        List<Slot> slotsToRemove = toCheck.entrySet().stream()
                .filter(entry -> isInvalidWeightMap(entry.getValue().getWeightMap()))
                .map(Map.Entry::getKey)
                .toList();

        slotsToRemove.forEach(slot -> notFeasibleFlights.put(slot, toCheck.remove(slot)));

        toCheck.values().forEach(flight -> {
            Map<Slot, Double> currWeightMap = flight.getWeightMap();
            slotsToRemove.forEach(currWeightMap.keySet()::remove);
        });
        return getFeasibleFlightList(toCheck, notFeasibleFlights);
    }

    private boolean isFullyImproved(Map<Slot, Flight> slotFlightMap) {
        for (Flight flight : slotFlightMap.values()) {
            if (isInvalidWeightMap(flight.getWeightMap())) return false;
        }
        return true;
    }

    private boolean isInvalidWeightMap(Map<Slot, Double> weightMap) {
        return weightMap.values().stream().anyMatch(value -> Double.isNaN(value))
                || weightMap.values().stream().allMatch(value -> value == -Double.MAX_VALUE)
                || weightMap.values().stream().filter(value -> value != -Double.MAX_VALUE).count() <= 1;
    }
}
