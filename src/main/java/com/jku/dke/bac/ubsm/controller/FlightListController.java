package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.Util;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class FlightListController {
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

    @Autowired
    private AirspaceUserController airspaceUserController;

    //@PostMapping("/newFlightList")
    //public ResponseEntity<Map<Slot, Flight>> newFlightList(@RequestParam("flightDistribution") List<Integer> flightDistribution) {
    //    if (flightDistribution.size() != airspaceUserController.getAirspaceUsers().size())
    //        return ResponseEntity.badRequest().build();
//
    //    Map<Slot, Flight> slotAllocationMap = new HashMap<>();
    //    List<AirspaceUser> aus = airspaceUserController.getAirspaceUsers();
    //    List<LocalTime> initialLocalTimeList = Util.getRandomTimes(flightDistribution.stream().mapToInt(Integer::intValue).sum(), startTimeInSeconds, endTimeInSeconds, initialTimeMean, initialTimeStd);
    //    AtomicInteger flightDistributionIndex = new AtomicInteger();
    //    AtomicInteger initialLocalTimeListIndex = new AtomicInteger();
    //    aus.forEach(airspaceUser -> {
    //        for (int i = 0; i < flightDistribution.get(flightDistributionIndex.get()); i++) {
    //            Flight flight = new Flight(airspaceUser, initialLocalTimeList.get(initialLocalTimeListIndex.getAndIncrement()));
    //            LocalTime scheduledTime = Util.getRandomTime(
    //                    flight.getInitialTime().toSecondOfDay(),
    //                    flight.getInitialTime().toSecondOfDay() + maxTimeAfterInitialTime,
    //                    (flight.getInitialTime().toSecondOfDay() + maxTimeAfterInitialTime + flight.getInitialTime().toSecondOfDay()) / 2,
    //                    stdScheduledTime);
    //            flight.setScheduledTime(scheduledTime);
    //            Slot slot = new Slot(scheduledTime);
    //            slotAllocationMap.put(slot, flight);
    //        }
    //        flightDistributionIndex.getAndIncrement();
    //    });
//
    //    Map<Slot, Flight> sortedSlotAllocationMap = slotAllocationMap.entrySet().stream()
    //            .sorted(Comparator.comparingInt(e -> e.getKey().getDepartureTime().toSecondOfDay()))
    //            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (slot, flight) -> slot, LinkedHashMap::new));
//
    //    List<Slot> possibleSlots = sortedSlotAllocationMap.keySet().stream().toList();
//
    //    sortedSlotAllocationMap.forEach((key, value) -> {
    //        value.getAirspaceUser().generateFlightAttributes(value, possibleSlots);
    //    });
//
    //    return ResponseEntity.ok(sortedSlotAllocationMap);
    //}
}