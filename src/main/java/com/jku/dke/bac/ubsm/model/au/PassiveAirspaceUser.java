package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Entity
@Component
public class PassiveAirspaceUser extends AirspaceUser {
    public PassiveAirspaceUser(String s) {
        super(s);
    }

    public PassiveAirspaceUser(String s, double d) {
        super(s, d);
    }

    public PassiveAirspaceUser() {

    }

    @Override
    public String toString() {
        return super.toString() + " - Passive";
    }

    @Override
    public AirspaceUserType getType() {
        return AirspaceUserType.PASSIVE;
    }

    @Override
    public Map<Slot, Double> generateWeightMap(Flight flight) {
        return null;
    }

    @Override
    public void generateFlightAttributes(Flight flight, List<Slot> possibleSlots) {

    }
}
