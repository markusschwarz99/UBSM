package com.jku.dke.bac.ubsm.model.au;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.FlightType;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.model.weightMapFunction.DefaultWeightMapFunction;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;

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
    private int[] priorityFlightMinutesToAdd;
    private Margin[] flexibleFlightPercentages;
    private Margin[] flexibleFlightWithPriorityPercentages;
    private Function<Double, Double> weightMapFunction;


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

    public int[] getPriorityFlightMinutesToAdd() {
        return priorityFlightMinutesToAdd;
    }

    public void setPriorityFlightMinutesToAdd(int[] priorityFlightMinutesToAdd) {
        this.priorityFlightMinutesToAdd = priorityFlightMinutesToAdd;
    }

    public Margin[] getFlexibleFlightPercentages() {
        return flexibleFlightPercentages;
    }

    public void setFlexibleFlightPercentages(Margin[] flexibleFlightPercentages) {
        this.flexibleFlightPercentages = flexibleFlightPercentages;
    }

    public Margin[] getFlexibleFlightWithPriorityPercentages() {
        return flexibleFlightWithPriorityPercentages;
    }

    public void setFlexibleFlightWithPriorityPercentages(Margin[] flexibleFlightWithPriorityPercentages) {
        this.flexibleFlightWithPriorityPercentages = flexibleFlightWithPriorityPercentages;
    }

    public Function<Double, Double> getWeightMapFunction() {
        return weightMapFunction;
    }

    public void setWeightMapFunction(Function<Double, Double> weightMapFunction) {
        this.weightMapFunction = weightMapFunction;
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
        return "AirspaceUser{" +
                "random=" + random +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", priorityDistribution=" + Arrays.toString(priorityDistribution) +
                ", priorityFlightMinutesToAdd=" + Arrays.toString(priorityFlightMinutesToAdd) +
                ", flexibleFlightPercentages=" + Arrays.toString(flexibleFlightPercentages) +
                ", flexibleFlightWithPriorityPercentages=" + Arrays.toString(flexibleFlightWithPriorityPercentages) +
                ", weightMapFunction=" + weightMapFunction +
                '}';
    }

    public void updateCredits(double d) {
        this.credits += d;
    }

    private void generateWeightMap(Flight flight, List<Slot> possibleSlots) {
        switch (this.weightMapFunction.getClass().getSimpleName()) {
            case "DefaultWeightMapFunction" -> this.weightMapFunction = new DefaultWeightMapFunction(1000,
                    flight.getNotBefore().toSecondOfDay(),
                    0,
                    flight.getWishedTime().toSecondOfDay(),
                    1,
                    flight.getWishedTime().toSecondOfDay(),
                    1,
                    flight.getNotAfter().toSecondOfDay(),
                    0,
                    flight.getWishedTime().toSecondOfDay(),
                    flight.getPriority());
        }

        Map<Slot, Double> weightMap = new LinkedHashMap<>();
        possibleSlots.forEach(slot -> weightMap.put(slot, this.weightMapFunction.apply((double) slot.getDepartureTime().toSecondOfDay())));
        flight.setWeightMap(weightMap);
    }

    public void generateFlightAttributes(Flight flight, List<Slot> possibleSlots) {
        flight.setPriority(generatePriority());
        flight.setFlightType(generateFlightType());
        generateTimes(flight, possibleSlots);
        generateWeightMap(flight, possibleSlots);
    }

    private int generatePriority() {
        return random.nextInt(10) + 1;
    }

    private FlightType generateFlightType() {
        FlightType flightType;
        int r = random.nextInt(100);
        if (r < this.priorityDistribution[0]) {
            flightType = FlightType.FLEXIBLE;
        } else if (r > this.priorityDistribution[1]) {
            flightType = FlightType.PRIORITY;
        } else {
            flightType = FlightType.FLEXIBLEWITHPRIORITY;
        }
        return flightType;
    }

    private void generateTimes(Flight flight, List<Slot> possibleSlots) {
        LocalTime[] localTimes = new LocalTime[3];
        if (flight.getFlightType() == FlightType.FLEXIBLE) {
            localTimes = generateFlexPercentTimes(flight, possibleSlots, this.flexibleFlightPercentages);
        } else if (flight.getFlightType() == FlightType.FLEXIBLEWITHPRIORITY) {
            localTimes = generateFlexPercentTimes(flight, possibleSlots, this.flexibleFlightWithPriorityPercentages);
        } else if (flight.getFlightType() == FlightType.PRIORITY) {
            localTimes = generatePriorityTimes(flight, possibleSlots);
        }
        flight.setNotBefore(localTimes[0]);
        flight.setWishedTime(localTimes[1]);
        flight.setNotAfter(localTimes[2]);
    }

    private LocalTime[] generatePriorityTimes(Flight flight, List<Slot> possibleSlots) {
        LocalTime[] localTimes = new LocalTime[3];
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
        localTimes[2] = localTimes[1].plusMinutes(random.nextInt(priorityFlightMinutesToAdd[1] - priorityFlightMinutesToAdd[0]) + priorityFlightMinutesToAdd[0]);
        return localTimes;
    }

    private LocalTime[] generateFlexPercentTimes(Flight flight, List<Slot> possibleSlots, Margin[] margins) {
        LocalTime[] localTimes = new LocalTime[3];
        long generatedTimeInSeconds;
        long durationInSeconds = Duration.between(flight.getScheduledTime(), possibleSlots.get(possibleSlots.size() - 1).getDepartureTime()).toSeconds();
        generatedTimeInSeconds = (long) ((durationInSeconds * random.nextDouble(margins[0].getUpperBound() - margins[0].getLowerBound()) + margins[0].getLowerBound()) + flight.getScheduledTime().toSecondOfDay());
        localTimes[0] = LocalTime.ofSecondOfDay(generatedTimeInSeconds - (generatedTimeInSeconds % 60));
        generatedTimeInSeconds = (long) ((durationInSeconds * random.nextDouble(margins[1].getUpperBound() - margins[1].getLowerBound()) + margins[1].getLowerBound()) + localTimes[0].toSecondOfDay());
        localTimes[1] = LocalTime.ofSecondOfDay(generatedTimeInSeconds - (generatedTimeInSeconds % 60));
        generatedTimeInSeconds = (long) ((durationInSeconds * random.nextDouble(margins[2].getUpperBound() - margins[2].getLowerBound()) + margins[2].getLowerBound()) + localTimes[0].toSecondOfDay());
        localTimes[2] = LocalTime.ofSecondOfDay(generatedTimeInSeconds - (generatedTimeInSeconds % 60));
        return localTimes;
    }
}
