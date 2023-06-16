package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.dto.statisticsDTO.AuBalanceDTO;
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
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    @Value("${inflation}")
    private double inflation;
    private Optimizer optimizer;
    private Simulator simulator;
    private List<Map<Slot, Flight>> initialFlightLists;
    private List<Map<Slot, Flight>> optimizedFlightLists;
    private List<RunStatisticDTO> statistics;
    private int currRun;

    public SimulationService(final AirspaceUserService airspaceUserService) {
        this.airspaceUserService = airspaceUserService;
        initialFlightLists = new ArrayList<>();
        optimizedFlightLists = new ArrayList<>();
        statistics = new ArrayList<>();
        currRun = 0;
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
        Logger.log("SimulationService - start simulation with optimizer " + optimizer.getClass().getSimpleName() + " ...");

        if (sim.equals(ZeroAndOutSimulator.class.getSimpleName())) {
            simulator = new ZeroAndOutSimulator();
        } else if (sim.equals(OnlyOfferSimulator.class.getSimpleName())) {
            simulator = new OnlyOfferSimulator();
        } else if (sim.equals(InflationSimulator.class.getSimpleName())) {
            simulator = new InflationSimulator(inflation, airspaceUserService.getInitialCredits());
        } else {
            Logger.log("SimulationService - invalid simulator (" + sim + ") ...");
            throw new IllegalArgumentException("Simulation " + sim + "not recognised");
        }
        Logger.log("SimulationService - start simulation with " + simulator.getClass().getSimpleName() + " ...");
    }

    public RunStatisticDTO runIteration(Map<String, Integer> flightDistribution, Integer delayInMinutes) {
        Logger.log("SimulationService - starting run ...");

        if (flightDistribution.keySet().stream()
                .anyMatch(name -> Arrays.stream(airspaceUserService.getAirspaceUsers())
                        .noneMatch(user -> user.getName().equals(name)))) {
            Logger.log("SimulationService - Error: There is at least one name in flightDistribution where no AirspaceUser exists");
            throw new IllegalArgumentException("There is at least one name in flightDistribution where no AirspaceUser exists.");
        }
        if (flightDistribution.values().stream().anyMatch(value -> value == null || value < 0)) {
            Logger.log("SimulationService - Error: There is at least one value null or < 0 in flightDistribution");
            throw new IllegalArgumentException("There is at least one value null or < 0 in flightDistribution.");
        }
        if (delayInMinutes <= 0) {
            Logger.log("SimulationService - Error: delayInMinutes has to be greater than 0");
            throw new IllegalArgumentException("delayInMinutes has to be greater than 0");
        }

        Logger.log("SimulationService - Params are correct ...");

        statistics.add(new RunStatisticDTO(currRun));
        initialFlightLists.add(createInitialFlightList(flightDistribution, delayInMinutes));
        statistics.get(currRun).setInitialFlightList(initialFlightLists.get(currRun));
        Map<String, Double> balanceBefore = Arrays.stream(airspaceUserService.getAirspaceUsers())
                .collect(Collectors.toMap(
                        AirspaceUser::getName,
                        AirspaceUser::getCredits
                ));


        Map<Slot, Flight> optimizedFlightList = optimizer.optimize(initialFlightLists.get(currRun));
        if (optimizedFlightList != null) {
            optimizedFlightLists.add(optimizedFlightList);
            initiateClearing(optimizedFlightLists.get(currRun));
            Map<String, Double> balanceAfter = Arrays.stream(airspaceUserService.getAirspaceUsers())
                    .collect(Collectors.toMap(
                            AirspaceUser::getName,
                            AirspaceUser::getCredits
                    ));

            while (isInvalidClearing(balanceAfter)) {
                Logger.log("SimulationService - clearing is invalid, at least one AU has negative credits ...");
                rollbackBalances(balanceBefore);
                optimizedFlightLists.remove(currRun);
                optimizedFlightList = optimizer.optimize(initialFlightLists.get(currRun));
                if (optimizedFlightList != null) {
                    optimizedFlightLists.add(optimizedFlightList);
                    initiateClearing(optimizedFlightLists.get(currRun));
                    balanceAfter = Arrays.stream(airspaceUserService.getAirspaceUsers())
                            .collect(Collectors.toMap(
                                    AirspaceUser::getName,
                                    AirspaceUser::getCredits
                            ));
                } else {
                    optimizedFlightLists.add(new HashMap<>());
                    return statistics.get(currRun++);
                }
            }

            statistics.get(currRun).setOptimizedFlightList(optimizedFlightLists.get(currRun));
            statistics.get(currRun).setParticipationAu(optimizedFlightLists.get(currRun));
            statistics.get(currRun).setParticipationInPercent((double) optimizedFlightLists.get(currRun).values().stream().filter(Flight::isInOptimizationRun).count() / optimizedFlightLists.get(currRun).size());
            statistics.get(currRun).setAuBalances(new AuBalanceDTO(balanceBefore, balanceAfter));
        } else {
            optimizedFlightLists.add(new HashMap<>());
        }
        Logger.log("SimulationService - run done ...");
        return statistics.get(currRun++);
    }

    public OverviewDTO endSimulation() {
        OverviewDTO overviewDTO = new OverviewDTO();
        overviewDTO.setTotalRuns(currRun);
        overviewDTO.setSimulator(simulator.getClass().getSimpleName());
        overviewDTO.setOptimizer(optimizer.getClass().getSimpleName());
        Map<Integer, AuBalanceDTO> balances = new HashMap<>();
        AtomicInteger i = new AtomicInteger();
        statistics.forEach(runStatisticDTO -> balances.put(i.getAndIncrement(), runStatisticDTO.getAuBalances()));
        overviewDTO.setBalances(balances);
        optimizer = null;
        simulator = null;
        initialFlightLists = new ArrayList<>();
        optimizedFlightLists = new ArrayList<>();
        statistics = new ArrayList<>();
        currRun = 0;
        return overviewDTO;
    }

    private void initiateClearing(Map<Slot, Flight> optimizedFlightList) {
        Logger.log("SimulationService - starting clearing ...");
        final double totalInitialUtility = optimizedFlightList.values().stream()
                .filter(Flight::isInOptimizationRun)
                .mapToDouble(Flight::getInitialUtility).sum();
        final double totalOptimizedUtility = optimizedFlightList.values().stream()
                .filter(Flight::isInOptimizationRun)
                .mapToDouble(Flight::getOptimizedUtility).sum();
        double utilityIncrease = (totalOptimizedUtility - totalInitialUtility) / totalInitialUtility;
        statistics.get(currRun).setTotalInitialUtility(totalInitialUtility);
        statistics.get(currRun).setTotalOptimizedUtility(totalOptimizedUtility);
        statistics.get(currRun).setUtilityIncrease(utilityIncrease);
        simulator.clearing(optimizedFlightList, utilityIncrease);
        Logger.log("SimulationService - clearing done ...");
    }

    private boolean isInvalidClearing(Map<String, Double> balanceAfter) {
        Logger.log("SimulationService - checking clearing ...");
        AtomicBoolean returner = new AtomicBoolean(false);
        balanceAfter.forEach((auName, credits) -> {
            if (credits < 0) {
                returner.set(true);

                initialFlightLists.get(currRun).values().forEach(flight -> {
                    if (flight.getAirspaceUser().getName().equals(auName)) {
                        if (optimizer.getClass().getSimpleName().equals(OnlyOfferSimulator.class.getSimpleName())) {
                            if (flight.getCost() < 0) {
                                flight.setInOptimizationRun(false);
                            }
                        } else {
                            flight.setInOptimizationRun(false);
                        }
                    }
                });
            }

        });
        return returner.get();
    }

    private void rollbackBalances(Map<String, Double> balanceBefore) {
        Logger.log("SimulationService - starting rollback ...");
        Arrays.stream(airspaceUserService.getAirspaceUsers()).forEach(airspaceUser -> airspaceUser.setCredits(balanceBefore.get(airspaceUser.getName())));
        Logger.log("SimulationService - rollback done ...");
    }

    private Map<Slot, Flight> createInitialFlightList(Map<String, Integer> flightDistribution, Integer delayInMinutes) {
        Logger.log("SimulationService - start generating initial flightList ...");
        List<AirspaceUser> actualAirspaceUsers = Arrays.stream(airspaceUserService.getAirspaceUsers()).toList();
        Map<Slot, Flight> flightList = new HashMap<>();
        List<LocalTime> initialLocalTimeList = getRandomTimes(flightDistribution.values().stream().mapToInt(Integer::intValue).sum(), startTimeInSeconds, endTimeInSeconds, initialTimeMean, initialTimeStd);
        AtomicInteger initialLocalTimeListIndex = new AtomicInteger();
        AtomicInteger flightIndex = new AtomicInteger();
        actualAirspaceUsers.forEach(airspaceUser -> {
            if (flightDistribution.containsKey(airspaceUser.getName())) {
                for (int i = 0; i < flightDistribution.get(airspaceUser.getName()); i++) {
                    Flight flight = new Flight(flightIndex.getAndIncrement(), airspaceUser, initialLocalTimeList.get(initialLocalTimeListIndex.getAndIncrement()));
                    flight.setScheduledTime(flight.getInitialTime().plusMinutes(delayInMinutes));
                    flight.setInOptimizationRun(true);
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
