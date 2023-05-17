package com.jku.dke.bac.ubsm.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.lang.Nullable;

import java.util.Arrays;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NeutralAirspaceUserDTO.class, name = "neutral"),
        @JsonSubTypes.Type(value = AggressiveAirspaceUserDTO.class, name = "aggressive"),
        @JsonSubTypes.Type(value = PassiveAirspaceUserDTO.class, name = "passive")})
public abstract class AirspaceUserDTO {
    private String name;

    private double credits;

    @Nullable
    private Integer probabilityFlexibleFlight;

    @Nullable
    private Integer probabilityPriorityFlight;

    @Nullable
    private Integer probabilityFlexibleWithPriorityFlight;

    @Nullable
    private int[] priorityFlightMinutesToAdd;

    @Nullable
    private MarginDTO[] flexibleFlightPercentages;

    @Nullable
    private MarginDTO[] flexibleFlightWithPriorityPercentages;

    @Nullable
    private String weightMapFunction;

    public AirspaceUserDTO() {
    }

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

    @Nullable
    public Integer getProbabilityFlexibleFlight() {
        return probabilityFlexibleFlight;
    }

    public void setProbabilityFlexibleFlight(@Nullable Integer probabilityFlexibleFlight) {
        this.probabilityFlexibleFlight = probabilityFlexibleFlight;
    }

    @Nullable
    public Integer getProbabilityPriorityFlight() {
        return probabilityPriorityFlight;
    }

    public void setProbabilityPriorityFlight(@Nullable Integer probabilityPriorityFlight) {
        this.probabilityPriorityFlight = probabilityPriorityFlight;
    }

    @Nullable
    public Integer getProbabilityFlexibleWithPriorityFlight() {
        return probabilityFlexibleWithPriorityFlight;
    }

    public void setProbabilityFlexibleWithPriorityFlight(@Nullable Integer probabilityFlexibleWithPriorityFlight) {
        this.probabilityFlexibleWithPriorityFlight = probabilityFlexibleWithPriorityFlight;
    }

    @Nullable
    public int[] getPriorityFlightMinutesToAdd() {
        return priorityFlightMinutesToAdd;
    }

    public void setPriorityFlightMinutesToAdd(@Nullable int[] priorityFlightMinutesToAdd) {
        this.priorityFlightMinutesToAdd = priorityFlightMinutesToAdd;
    }

    @Nullable
    public MarginDTO[] getFlexibleFlightWithPriorityPercentages() {
        return flexibleFlightWithPriorityPercentages;
    }

    public void setFlexibleFlightWithPriorityPercentages(@Nullable MarginDTO[] flexibleFlightWithPriorityPercentages) {
        this.flexibleFlightWithPriorityPercentages = flexibleFlightWithPriorityPercentages;
    }

    @Nullable
    public MarginDTO[] getFlexibleFlightPercentages() {
        return flexibleFlightPercentages;
    }

    public void setFlexibleFlightPercentages(@Nullable MarginDTO[] flexibleFlightPercentages) {
        this.flexibleFlightPercentages = flexibleFlightPercentages;
    }

    @Nullable
    public String getWeightMapFunction() {
        return weightMapFunction;
    }

    public void setWeightMapFunction(@Nullable String weightMapFunction) {
        this.weightMapFunction = weightMapFunction;
    }

    @Override
    public String toString() {
        return "AirspaceUserDTO{" +
                "name='" + name + '\'' +
                ", credits=" + credits +
                ", probabilityFlexibleFlight=" + probabilityFlexibleFlight +
                ", probabilityPriorityFlight=" + probabilityPriorityFlight +
                ", probabilityFlexibleWithPriorityFlight=" + probabilityFlexibleWithPriorityFlight +
                ", priorityFlightMinutesToAdd=" + Arrays.toString(priorityFlightMinutesToAdd) +
                ", flexibleFlightWithPriorityPercentages=" + Arrays.toString(flexibleFlightWithPriorityPercentages) +
                ", flexibleFlightPercentages=" + Arrays.toString(flexibleFlightPercentages) +
                ", weightMapFunction='" + weightMapFunction + '\'' +
                '}';
    }
}