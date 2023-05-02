package com.jku.dke.bac.ubsm.model.au;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;
import java.util.Map;

@Entity
public abstract class AirspaceUser {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "credits", nullable = false)
    private double credits;

    public AirspaceUser() {
    }

    public AirspaceUser(String name) {
        this.name = name;
    }

    public AirspaceUser(String name, double credits) {
        this.name = name;
        this.credits = credits;
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

    @Override
    public String toString() {
        return "AU: " + this.name + " | " + this.credits + " DC";
    }

    public void updateCredits(double d) {
        this.credits += d;
    }

    public abstract AirspaceUserType getType();

    public abstract Map<Slot, Double> generateWeightMap(Flight flight);

    public abstract void generateFlightAttributes(Flight flight, List<Slot> possibleSlots);
}
