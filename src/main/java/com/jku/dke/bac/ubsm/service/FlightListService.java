package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.dto.FlightListDTO;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.model.mapper.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FlightListService {
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
    private List<Map<Slot, Flight>> flightLists;

    public FlightListService(final AirspaceUserService airspaceUserService) {
        this.airspaceUserService = airspaceUserService;
        this.flightLists = new ArrayList<>();
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

    public List<FlightListDTO> getFlightListsDTO() {
        List<FlightListDTO> flightListDTOS = new ArrayList<>();
        AtomicInteger idx = new AtomicInteger();
        this.flightLists.forEach(slotFlightMap -> {
            flightListDTOS.add(Mapper.mapFlightListToFlightListDTO(idx.get(), slotFlightMap));
            idx.getAndIncrement();
        });
        return flightListDTOS;
    }

    public List<Map<Slot, Flight>> getFlightLists() {
        return this.flightLists;
    }

    public List<FlightListDTO> getFlightListByIndex(int index) throws IllegalArgumentException {
        if (index < 0 || index >= this.flightLists.size())
            throw new IllegalArgumentException("List index does not exist.");
        FlightListDTO flightListDTO = Mapper.mapFlightListToFlightListDTO(index, this.flightLists.get(index));

        return new ArrayList<>(Collections.singletonList(flightListDTO));
    }

    public void deleteAllFlightLists() {
        this.flightLists = new ArrayList<>();
    }

    public void deleteFlightListByIndex(int index) throws IllegalArgumentException {
        if (index < 0 || index >= this.flightLists.size())
            throw new IllegalArgumentException("List index does not exist.");
        this.flightLists.remove(index);
    }

    public Map<Slot, Flight> generateFlightList(Map<String, Integer> flightDistribution) throws IllegalArgumentException {
        List<AirspaceUser> actualAirspaceUsers = Arrays.stream(airspaceUserService.getAirspaceUsers()).toList();

        if (flightDistribution.keySet().stream().toList().stream().anyMatch(name -> actualAirspaceUsers.stream().noneMatch(user -> user.getName().equals(name))))
            throw new IllegalArgumentException("There is at least one name in flightDistribution where no AirspaceUser exists.");

        if (flightDistribution.values().stream().anyMatch(value -> value == null || value < 0))
            throw new IllegalArgumentException("There is at least one value null or < 0 in flightDistribution.");


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

        Map<Slot, Flight> sortedSlotAllocationMap = flightList.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getDepartureTime().toSecondOfDay()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (slot, flight) -> slot, LinkedHashMap::new));

        List<Slot> possibleSlots = sortedSlotAllocationMap.keySet().stream().toList();
        sortedSlotAllocationMap.forEach((key, flight) -> flight.getAirspaceUser().generateFlightAttributes(flight, possibleSlots));

        this.flightLists.add(sortedSlotAllocationMap);
        return sortedSlotAllocationMap;
    }
}
