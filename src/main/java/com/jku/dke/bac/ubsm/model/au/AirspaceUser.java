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
import java.util.List;
import java.util.Random;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NeutralAirspaceUser.class, name = "neutral"),
        @JsonSubTypes.Type(value = AggressiveAirspaceUser.class, name = "aggressive"),
        @JsonSubTypes.Type(value = PassiveAirspaceUser.class, name = "passive")})
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

    public abstract void generateWeightMap(Flight flight, List<Slot> possibleSlots);

    public void generateFlightAttributes(Flight flight, List<Slot> possibleSlots) {
        flight.setFlightType(generateFlightType(flight.getAirspaceUser().getPriorityDistribution()));
        generateTimes(flight, possibleSlots);
        generateWeightMap(flight, possibleSlots);
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

    protected abstract void generateTimes(Flight flight, List<Slot> possibleSlots);

    protected void generatePriorityTimes(Flight flight, List<Slot> possibleSlots, int minutesToAdd) {
        flight.setNotBefore(flight.getInitialTime());
        Slot bestPossibleSlot = null;
        int slotIndex = 0;

        while (slotIndex < possibleSlots.size() && bestPossibleSlot == null) {
            if (possibleSlots.get(slotIndex).getDepartureTime().isAfter(flight.getInitialTime()) && possibleSlots.get(slotIndex).getDepartureTime().isBefore(flight.getScheduledTime())) {
                bestPossibleSlot = possibleSlots.get(slotIndex);
            }
            slotIndex++;
        }

        if (bestPossibleSlot != null) {
            flight.setWishedTime(bestPossibleSlot.getDepartureTime());
        } else {
            flight.setWishedTime(flight.getScheduledTime());
        }
        flight.setNotAfter(flight.getWishedTime().plusMinutes(minutesToAdd));
    }

    protected void generateFlexibleTimes(Flight flight, List<Slot> possibleSlots, double notBeforePercent, double wishedPercent, double notAfterPercent) {
        long durationInSeconds;
        Duration duration = Duration.between(flight.getScheduledTime(), possibleSlots.get(possibleSlots.size() - 1).getDepartureTime());
        durationInSeconds = (long) ((duration.toSeconds() * notBeforePercent) + flight.getScheduledTime().toSecondOfDay());
        flight.setNotBefore(LocalTime.ofSecondOfDay(durationInSeconds - (durationInSeconds % 60)));
        durationInSeconds = (long) ((duration.toSeconds() * wishedPercent) + flight.getNotBefore().toSecondOfDay());
        flight.setWishedTime(LocalTime.ofSecondOfDay(durationInSeconds - (durationInSeconds % 60)));
        durationInSeconds = (long) ((duration.toSeconds() * notAfterPercent) + flight.getNotBefore().toSecondOfDay());
        flight.setNotAfter(LocalTime.ofSecondOfDay(durationInSeconds - (durationInSeconds % 60)));
    }

    protected void generateFlexibleWithPriorityTimes(Flight flight, List<Slot> possibleSlots, double wishedPercent, double notAfterPercent) {
        long durationInSeconds;
        Duration duration = Duration.between(flight.getScheduledTime(), possibleSlots.get(possibleSlots.size() - 1).getDepartureTime());
        flight.setNotBefore(flight.getScheduledTime());
        durationInSeconds = (long) ((duration.toSeconds() * wishedPercent) + flight.getNotBefore().toSecondOfDay());
        flight.setWishedTime(LocalTime.ofSecondOfDay(durationInSeconds - (durationInSeconds % 60)));
        durationInSeconds = (long) ((duration.toSeconds() * notAfterPercent) + flight.getNotBefore().toSecondOfDay());
        flight.setNotAfter(LocalTime.ofSecondOfDay(durationInSeconds - (durationInSeconds % 60)));
    }
}
