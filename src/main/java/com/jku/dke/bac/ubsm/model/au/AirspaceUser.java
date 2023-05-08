package com.jku.dke.bac.ubsm.model.au;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.FlightType;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = NeutralAirspaceUser.class, name = "neutral"), @JsonSubTypes.Type(value = AggressiveAirspaceUser.class, name = "aggressive"), @JsonSubTypes.Type(value = PassiveAirspaceUser.class, name = "passive")})
public abstract class AirspaceUser {
    private final Random random = new Random();
    private String name;
    private double credits;
    private int[] priorityDistribution;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public int[] getPriorityDistribution() {
        return priorityDistribution;
    }

    public void setPriorityDistribution(int[] priorityDistribution) {
        this.priorityDistribution = priorityDistribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AirspaceUser that = (AirspaceUser) o;

        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return "AU: " + this.name + " | " + this.credits + " DC";
    }

    public void updateCredits(double d) {
        this.credits += d;
    }


    public abstract Map<Slot, Double> generateWeightMap(Flight flight);

    public void generateFlightAttributes(Flight flight, List<Slot> possibleSlots) {
        flight.setFlightType(generateFlightType(flight.getAirspaceUser().getPriorityDistribution()));
        LocalTime[] localTimes = getTimes(flight, possibleSlots);
        flight.setNotBefore(localTimes[0]);
        flight.setWishedTime(localTimes[1]);
        flight.setNotAfter(localTimes[2]);
    }

    public FlightType generateFlightType(int[] priorityDistribution) {
        FlightType flightType;
        int r = random.nextInt(100);
        if (r < priorityDistribution[0]) {
            flightType = FlightType.FLEXIBLE;
        } else if (r > priorityDistribution[1]) {
            flightType = FlightType.PRIORITY;
        } else {
            flightType = FlightType.FLEXIBLEWITHPRIORITY;
        }
        return flightType;
    }

    protected LocalTime[] getTimes(Flight flight, List<Slot> possibleSlots) {
        LocalTime[] localTimes = new LocalTime[3];

        if (flight.getFlightType() == FlightType.PRIORITY) {
            localTimes[0] = flight.getInitialTime();
            Slot bestPossibleSlot = null;
            int slotIndex = 0;

            while (slotIndex < possibleSlots.size() && bestPossibleSlot == null) {
                if (possibleSlots.get(slotIndex).getDepartureTime().isAfter(flight.getInitialTime()) && possibleSlots.get(slotIndex).getDepartureTime().isBefore(flight.getScheduledTime())) {
                    bestPossibleSlot = possibleSlots.get(slotIndex);
                }
                slotIndex++;
            }

            if (bestPossibleSlot != null) {
                localTimes[1] = bestPossibleSlot.getDepartureTime();
            } else {
                localTimes[1] = flight.getScheduledTime();
            }

            localTimes[2] = localTimes[1].plusMinutes(30);

        } else if (flight.getFlightType() == FlightType.FLEXIBLE) {
            localTimes[0] = flight.getScheduledTime();
            Slot lastSlot = possibleSlots.get(possibleSlots.size() - 1);
            Duration duration = Duration.between(flight.getScheduledTime(), lastSlot.getDepartureTime());
            long seconds = (long) ((duration.toSeconds() * 0.75) + flight.getScheduledTime().toSecondOfDay());
            localTimes[2] = LocalTime.ofSecondOfDay(seconds - (seconds % 60));
            duration = Duration.between(localTimes[0], localTimes[2]);
            seconds = (long) ((duration.toSeconds() * 0.75) + localTimes[0].toSecondOfDay());
            localTimes[1] = LocalTime.ofSecondOfDay(seconds - (seconds % 60));
        } else if (flight.getFlightType() == FlightType.FLEXIBLEWITHPRIORITY) {
            localTimes[0] = flight.getInitialTime();
            Slot lastSlot = possibleSlots.get(possibleSlots.size() - 1);
            Duration duration = Duration.between(flight.getScheduledTime(), lastSlot.getDepartureTime());
            long seconds = (long) ((duration.toSeconds() * 0.50) + flight.getScheduledTime().toSecondOfDay());
            localTimes[2] = LocalTime.ofSecondOfDay(seconds - (seconds % 60));
            duration = Duration.between(localTimes[0], localTimes[2]);
            seconds = (long) ((duration.toSeconds() * 0.50) + localTimes[0].toSecondOfDay());
            localTimes[1] = LocalTime.ofSecondOfDay(seconds - (seconds % 60));
        }
        return localTimes;
    }
}
