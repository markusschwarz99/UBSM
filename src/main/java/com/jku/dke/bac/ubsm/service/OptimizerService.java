package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.optimizer.HungarianAlgorithm;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OptimizerService {
    private final FlightListService flightListService;

    public OptimizerService(final FlightListService flightListService) {
        this.flightListService = flightListService;
    }


    public Map<Slot, Flight> optimizeFlightList() {
        if (flightListService.getFlightLists().size() == 0)
            throw new IllegalArgumentException("There are 0 flightLists");

        Map<Slot, Flight> optimizedFlightList = new HashMap<>();
        Map<Slot, Flight> initialFlightList = flightListService.getFlightLists().get(flightListService.getFlightLists().size() - 1);

        Map<Slot, Flight> validFlightList = new LinkedHashMap<>();

        List<Map<Slot, Flight>> check = new ArrayList<>();
        check.add(initialFlightList);
        check.add(validFlightList);

        boolean isValid = false;
        while (!isValid) {
            check = determineFeasibility(check.get(0), check.get(1));
            isValid = isFullyImproved(check.get(0));
        }


        int length = check.get(0).size();
        double[][] input = new double[length][length];
        AtomicInteger i = new AtomicInteger();
        check.get(0).forEach((slot, flight) -> input[i.getAndIncrement()] = flight.getWeightMap().values().stream()
                .mapToDouble(value -> value * -1)
                .toArray());

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(input);
        int[] result = hungarianAlgorithm.execute();
        System.out.println(Arrays.toString(result));

        return optimizedFlightList;
    }

    private boolean isFullyImproved(Map<Slot, Flight> slotFlightMap) {
        for (Flight flight : slotFlightMap.values()) {
            if (isInvalidWeightMap(flight.getWeightMap())) return false;
        }
        return true;
    }

    private List<Map<Slot, Flight>> determineFeasibility(Map<Slot, Flight> toCheck, Map<Slot, Flight> notFeasibleFlights) {
        List<Slot> slotsToRemove = toCheck.entrySet().stream()
                .filter(entry -> isInvalidWeightMap(entry.getValue().getWeightMap()))
                .map(Map.Entry::getKey)
                .toList();

        slotsToRemove.forEach(slot -> {
            notFeasibleFlights.put(slot, toCheck.remove(slot));
        });

        toCheck.values().forEach(flight -> {
            Map<Slot, Double> currWeightMap = flight.getWeightMap();
            currWeightMap.keySet().removeAll(slotsToRemove);
        });

        return List.of(toCheck, notFeasibleFlights);
    }

    private boolean isInvalidWeightMap(Map<Slot, Double> weightMap) {
        return weightMap.values().stream().anyMatch(value -> Double.isNaN(value))
                || weightMap.values().stream().allMatch(value -> value == -Double.MAX_VALUE)
                || weightMap.values().stream().filter(value -> value != -Double.MAX_VALUE).count() <= 1;
    }

    public Map<Slot, Flight> optimizeFlightList(int index) {
        if (index < 0 || index >= flightListService.getFlightLists().size())
            throw new IllegalArgumentException("Invalid index");

        Map<Slot, Flight> optimizedFlightList = new HashMap<>();
        Map<Slot, Flight> initialFlightList = flightListService.getFlightLists().get(index);

        int length = initialFlightList.size();
        double[][] input = new double[length][length];
        int i = 0;
        initialFlightList.forEach((slot, flight) -> {
            List<Double> weights = flight.getWeightMap().values().stream().toList();
            double[] weightMap = weights.stream().mapToDouble(Double::doubleValue).toArray();
            for (int j = 0; j < weightMap.length; j++) {
                weightMap[j] *= -1;
            }
            input[i] = weightMap;

        });

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(input);
        int[] result = hungarianAlgorithm.execute();
        System.out.println(Arrays.toString(result));

        return optimizedFlightList;
    }


}
