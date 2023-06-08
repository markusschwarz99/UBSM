package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.dto.statisticsDTO.OverviewDTO;
import com.jku.dke.bac.ubsm.model.dto.statisticsDTO.RunStatisticDTO;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.optimizer.HeuristicOptimizer;
import com.jku.dke.bac.ubsm.optimizer.HungarianOptimizer;
import com.jku.dke.bac.ubsm.optimizer.Optimizer;
import com.jku.dke.bac.ubsm.simulator.InflationSimulator;
import com.jku.dke.bac.ubsm.simulator.OnlyOfferSimulator;
import com.jku.dke.bac.ubsm.simulator.Simulator;
import com.jku.dke.bac.ubsm.simulator.ZeroAndOutSimulator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SimulationService {
    private final AirspaceUserService airspaceUserService;
    @Value("${startTimeInSeconds}")
    private int startTimeInSeconds;
    @Value("${endTimeInSeconds}")
    private int endTimeInSeconds;
    @Value("${initialTimeMean}")
    private int initialTimeMean;
    @Value("${initialTimeStd}")
    private int initialTimeStd;
    private Optimizer optimizer;
    private Simulator simulator;
    private List<Map<Slot, Flight>> initialFlightLists;
    private List<Map<Slot, Flight>> optimizedFlightLists;
    private List<RunStatisticDTO> statistics;
    private Map<Slot, Flight> currInitialFlightList;
    private Map<Slot, Flight> currOptimizedFlightList;
    private RunStatisticDTO currStatistic;
    private OverviewDTO overviewDTO;

    public SimulationService(final AirspaceUserService airspaceUserService) {
        this.airspaceUserService = airspaceUserService;
        this.initialFlightLists = new ArrayList<>();
        this.optimizedFlightLists = new ArrayList<>();
        this.statistics = new ArrayList<>();
    }

    private static List<LocalTime> getRandomTimes(int n, int startTimeInSeconds, int maxTimeInSeconds, int mean, int std) {
        List<LocalTime> lt = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Random r = new Random();
            int k = (int) Math.max(startTimeInSeconds, Math.min(maxTimeInSeconds, mean + r.nextGaussian() * std));
            k = k - (k % 60);
            LocalTime ltt = LocalTime.ofSecondOfDay(k);
            if (!lt.contains(ltt)) {
                lt.add(LocalTime.ofSecondOfDay(k));
            } else i--;
        }
        return lt;
    }

    public void startSimulation(String opt, String sim) throws IllegalArgumentException {
        if (opt.equals(HungarianOptimizer.class.getSimpleName())) {
            optimizer = new HungarianOptimizer();
        } else if (opt.equals(HeuristicOptimizer.class.getSimpleName())) {
            optimizer = new HeuristicOptimizer();
        } else {
            Logger.log("SimulationService - invalid optimizer (" + opt + ") ...");
            throw new IllegalArgumentException("Optimizer " + opt + "not recognised");
        }
        Logger.log("SimulationService - start simulation with " + optimizer.getClass().getSimpleName() + " ...");

        if (sim.equals(ZeroAndOutSimulator.class.getSimpleName())) {
            this.simulator = new ZeroAndOutSimulator();
        } else if (sim.equals(OnlyOfferSimulator.class.getSimpleName())) {
            this.simulator = new OnlyOfferSimulator();
        } else if (sim.equals(InflationSimulator.class.getSimpleName())) {
            this.simulator = new InflationSimulator();
        } else {
            throw new IllegalArgumentException("Simulation " + sim + "not recognised");
        }
        Logger.log("SimulationService - start simulation with " + simulator.getClass().getSimpleName() + " ...");

    }

    public RunStatisticDTO runIteration(Map<String, Integer> flightDistribution, Integer delayInMinutes) {
        Logger.log("SimulationService - starting run ...");
        currStatistic = new RunStatisticDTO(this.initialFlightLists.size());
        this.initialFlightLists.add(createInitialFlightList(flightDistribution, delayInMinutes));
        currStatistic.setInitialFlightList(this.initialFlightLists.get(this.initialFlightLists.size() - 1));
        this.optimizedFlightLists.add(optimizer.optimize(this.initialFlightLists.get(this.initialFlightLists.size() - 1)));
        initiateClearing(this.optimizedFlightLists.get(this.optimizedFlightLists.size() - 1));
        currStatistic.setOptimizedFlightList(this.optimizedFlightLists.get(this.optimizedFlightLists.size() - 1));
        this.statistics.add(currStatistic);
        Logger.log("SimulationService - run done ...");
        return currStatistic;
    }

    public OverviewDTO endSimulation() {
        this.optimizer = null;
        this.simulator = null;
        this.initialFlightLists = new ArrayList<>();
        this.optimizedFlightLists = new ArrayList<>();
        this.statistics = new ArrayList<>();
        return this.overviewDTO;
    }

    private void initiateClearing(Map<Slot, Flight> optimizedFlightList) {
        Logger.log("SimulationService - starting clearing ...");
        final double totalInitialUtility = optimizedFlightList.values().stream()
                .filter(flight -> flight.getOptimizedUtility() != null)
                .mapToDouble(Flight::getInitialUtility).sum();
        final double totalOptimizedUtility = optimizedFlightList.values().stream()
                .filter(flight -> flight.getOptimizedUtility() != null)
                .mapToDouble(Flight::getOptimizedUtility).sum();
        double utilityIncrease = (totalOptimizedUtility - totalInitialUtility) / totalInitialUtility;
        this.currStatistic.setTotalInitialUtility(totalInitialUtility);
        this.currStatistic.setTotalOptimizedUtility(totalOptimizedUtility);
        this.currStatistic.setUtilityIncrease(utilityIncrease);
        this.simulator.clearing(optimizedFlightList, utilityIncrease);
        Logger.log("SimulationService - clearing done ...");
    }

    private Map<Slot, Flight> createInitialFlightList(Map<String, Integer> flightDistribution, Integer delayInMinutes) {
        Logger.log("SimulationService - start generating initial flightList ...");
        List<AirspaceUser> actualAirspaceUsers = Arrays.stream(airspaceUserService.getAirspaceUsers()).toList();

        if (flightDistribution.keySet().stream().toList().stream().anyMatch(name -> actualAirspaceUsers.stream().noneMatch(user -> user.getName().equals(name)))) {
            Logger.log("SimulationService - Error: There is at least one name in flightDistribution where no AirspaceUser exists");
            throw new IllegalArgumentException("There is at least one name in flightDistribution where no AirspaceUser exists.");
        }

        if (flightDistribution.values().stream().anyMatch(value -> value == null || value < 0)) {
            Logger.log("SimulationService - Error: There is at least one value null or < 0 in flightDistribution");
            throw new IllegalArgumentException("There is at least one value null or < 0 in flightDistribution.");
        }

        Map<Slot, Flight> flightList = new HashMap<>();
        List<LocalTime> initialLocalTimeList = getRandomTimes(flightDistribution.values().stream().mapToInt(Integer::intValue).sum(), startTimeInSeconds, endTimeInSeconds, initialTimeMean, initialTimeStd);
        AtomicInteger initialLocalTimeListIndex = new AtomicInteger();
        AtomicInteger flightIndex = new AtomicInteger();
        actualAirspaceUsers.forEach(airspaceUser -> {
            if (flightDistribution.containsKey(airspaceUser.getName())) {
                for (int i = 0; i < flightDistribution.get(airspaceUser.getName()); i++) {
                    Flight flight = new Flight(flightIndex.getAndIncrement(), airspaceUser, initialLocalTimeList.get(initialLocalTimeListIndex.getAndIncrement()));
                    flight.setScheduledTime(flight.getInitialTime().plusMinutes(delayInMinutes));
                    Slot slot = new Slot(flight.getScheduledTime());
                    flightList.put(slot, flight);
                }
            }
        });

        Map<Slot, Flight> sortedFlightList = flightList.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getDepartureTime().toSecondOfDay()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (slot, flight) -> slot, LinkedHashMap::new));

        List<Slot> possibleSlots = sortedFlightList.keySet().stream().toList();
        sortedFlightList.forEach((key, flight) -> flight.getAirspaceUser().generateFlightAttributes(flight, possibleSlots));

        Logger.log("SimulationService - initial flightList created ...");
        return sortedFlightList;
    }
}
