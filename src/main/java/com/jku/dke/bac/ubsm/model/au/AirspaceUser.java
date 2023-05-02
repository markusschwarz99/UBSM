package com.jku.dke.bac.ubsm.model.au;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NeutralAirspaceUser.class, name = "neutral"),
        @JsonSubTypes.Type(value = AggressiveAirspaceUser.class, name = "aggressive"),
        @JsonSubTypes.Type(value = PassiveAirspaceUser.class, name = "passive")
})
public abstract class AirspaceUser {
    private String name;
    private double credits;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AirspaceUser that = (AirspaceUser) o;

        return getName().equals(that.getName());
    }

    //@Override
    //public int hashCode() {
    //    return getName().hashCode();
    //}

    @Override
    public String toString() {
        return "AU: " + this.name + " | " + this.credits + " DC";
    }

    public void updateCredits(double d) {
        this.credits += d;
    }


    public abstract Map<Slot, Double> generateWeightMap(Flight flight);

    public abstract void generateFlightAttributes(Flight flight, List<Slot> possibleSlots);
}
