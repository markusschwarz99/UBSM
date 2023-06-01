package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.dto.OverviewDTO;
import com.jku.dke.bac.ubsm.model.dto.StatisticDTO;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.optimizer.HeuristicOptimizer;
import com.jku.dke.bac.ubsm.optimizer.HungarianOptimizer;
import com.jku.dke.bac.ubsm.optimizer.Optimizer;
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
    @Value("${maxTimeAfterInitialTime}")
    private int maxTimeAfterInitialTime;
    @Value("${stdScheduledTime}")
    private int stdScheduledTime;
    private Optimizer optimizer;
    private List<Map<Slot, Flight>> initialFlightLists;
    private List<Map<Slot, Flight>> optimizedFlightLists;
    private List<StatisticDTO> statistics;
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
            int k = (int) Math.max(startTimeInSeconds, Math.min(maxTimeInSeconds, (int) mean + r.nextGaussian() * std));
            k = k - (k % 60);
            LocalTime ltt = LocalTime.ofSecondOfDay(k);
            if (!lt.contains(ltt)) {
                lt.add(LocalTime.ofSecondOfDay(k));
            } else i--;
        }
        return lt;
    }

    private static LocalTime getRandomTime(int startTimeInSeconds, int maxTimeInSeconds, int mean, int std) {
        Random r = new Random();
        int k = (int) Math.max(startTimeInSeconds, Math.min(maxTimeInSeconds, (int) mean + r.nextGaussian() * std));
        k = k - (k % 60);
        return LocalTime.ofSecondOfDay(k);
    }

    private static boolean checkIfLocalTimeExists(LocalTime localTime, Map<Slot, Flight> flightList) {
        for (Slot slot : flightList.keySet()) {
            if (slot.getDepartureTime().equals(localTime)) {
                return true;
            }
        }
        return false;
    }

    public void startSimulation(String opt) throws IllegalArgumentException {
        if (opt.equals(HungarianOptimizer.class.getSimpleName())) {
            optimizer = new HungarianOptimizer();
            Logger.log("SimulationService - start simulation with " + optimizer.getClass().getSimpleName() + " ...");
        } else if (opt.equals(HeuristicOptimizer.class.getSimpleName())) {
            optimizer = new HeuristicOptimizer();
            Logger.log("SimulationService - start simulation with " + optimizer.getClass().getSimpleName() + " ...");
        } else {
            Logger.log("SimulationService - invalid optimizer (" + opt + ") ...");
            throw new IllegalArgumentException("Optimizer " + opt + "not recognised");
        }
    }

    public void runIteration(Map<String, Integer> flightDistribution) {
        Logger.log("SimulationService - starting run ...");
        createInitialFlightList(flightDistribution);
        this.optimizedFlightLists.add(optimizer.optimize(this.initialFlightLists.get(this.initialFlightLists.size() - 1)));
        this.statistics.add(new StatisticDTO(this.initialFlightLists.size() - 1));
        clearing(this.initialFlightLists.size() - 1);
        Logger.log("SimulationService - run done ...");
    }

    public StatisticDTO getStatistic(int runId) {
        Logger.log("SimulationService - starting creating statistics ...");
        if (runId < 0 || runId >= this.statistics.size()) {
            Logger.log("SimulationService - Error: This runId: " + runId + " does not exist");
            throw new IllegalArgumentException("The runId: " + runId + " does not exist.");
        }

        StatisticDTO statisticDTO = this.statistics.get(runId);
        statisticDTO.setInitialFlightList(this.initialFlightLists.get(runId));
        statisticDTO.setOptimizedFlightList(this.optimizedFlightLists.get(runId));

        Logger.log("SimulationService - statistics created ...");

        return statisticDTO;
    }

    public OverviewDTO endSimulation() {
        this.optimizer = null;
        this.initialFlightLists = new ArrayList<>();
        this.optimizedFlightLists = new ArrayList<>();
        this.statistics = new ArrayList<>();
        return this.overviewDTO;
    }

    private void clearing(int runId) {
        Logger.log("SimulationService - starting clearing ...");
        Map<Slot, Flight> optimizedFlightList = this.optimizedFlightLists.get(runId);
        final double totalInitialUtility = optimizedFlightList.values().stream()
                .filter(flight -> flight.getOptimizedUtility() == null)
                .mapToDouble(Flight::getInitialUtility).sum();
        final double totalOptimizedUtility = optimizedFlightList.values().stream()
                .filter(flight -> flight.getOptimizedUtility() != null)
                .mapToDouble(Flight::getOptimizedUtility).sum();
        double utilityIncrease = (totalOptimizedUtility - totalInitialUtility) / totalInitialUtility;

        this.statistics.get(runId).setInitialUtility(totalInitialUtility);
        this.statistics.get(runId).setOptimizedUtility(totalOptimizedUtility);
        this.statistics.get(runId).setUtilityIncrease(utilityIncrease);

        Map<String, Double> airspaceUserBalance = new HashMap<>();
        Arrays.stream(this.airspaceUserService.getAirspaceUsers()).forEach(airspaceUser -> airspaceUserBalance.put("balanceBefore" + airspaceUser.getName(), airspaceUser.getCredits()));

        optimizedFlightList.values().forEach(flight -> {
            if (flight.getOptimizedUtility()!= null) {
                Logger.log(Double.toString(flight.getOptimizedUtility() - flight.getInitialUtility() * (1 + utilityIncrease)));
                flight.getAirspaceUser().updateCredits(flight.getOptimizedUtility() - flight.getInitialUtility() * (1 + utilityIncrease));
            }
        });

        Arrays.stream(this.airspaceUserService.getAirspaceUsers()).forEach(airspaceUser -> airspaceUserBalance.put("balanceAfter" + airspaceUser.getName(), airspaceUser.getCredits()));

        this.statistics.get(runId).setAirspaceUserBalance(airspaceUserBalance);

        Logger.log("SimulationService - clearing done ...");
    }

    private void createInitialFlightList(Map<String, Integer> flightDistribution) {
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
        actualAirspaceUsers.forEach(airspaceUser -> {
            if (flightDistribution.containsKey(airspaceUser.getName())) {
                for (int i = 0; i < flightDistribution.get(airspaceUser.getName()); i++) {
                    Flight flight = new Flight(airspaceUser, initialLocalTimeList.get(initialLocalTimeListIndex.getAndIncrement()));
                    LocalTime scheduledTime = getRandomTime(
                            flight.getInitialTime().toSecondOfDay(),
                            flight.getInitialTime().toSecondOfDay() + maxTimeAfterInitialTime,
                            (2 * flight.getInitialTime().toSecondOfDay() + maxTimeAfterInitialTime) / 2,
                            stdScheduledTime);
                    while (checkIfLocalTimeExists(scheduledTime, flightList)) {
                        scheduledTime = getRandomTime(
                                flight.getInitialTime().toSecondOfDay(),
                                flight.getInitialTime().toSecondOfDay() + maxTimeAfterInitialTime,
                                (flight.getInitialTime().toSecondOfDay() + maxTimeAfterInitialTime + flight.getInitialTime().toSecondOfDay()) / 2,
                                stdScheduledTime);
                    }
                    flight.setScheduledTime(scheduledTime);
                    Slot slot = new Slot(scheduledTime);
                    flightList.put(slot, flight);
                }
            }
        });

        Map<Slot, Flight> sortedFlightList = flightList.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getDepartureTime().toSecondOfDay()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (slot, flight) -> slot, LinkedHashMap::new));

        List<Slot> possibleSlots = sortedFlightList.keySet().stream().toList();
        sortedFlightList.forEach((key, flight) -> flight.getAirspaceUser().generateFlightAttributes(flight, possibleSlots));

        this.initialFlightLists.add(sortedFlightList);
        Logger.log("SimulationService - initial flightList created ...");
    }
}
