package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Entity
@Component
public class AggressiveAirspaceUser extends AirspaceUser {
    public AggressiveAirspaceUser(String s) {
        super(s);
    }

    public AggressiveAirspaceUser(String s, double d) {
        super(s, d);
    }

    public AggressiveAirspaceUser() {

    }

    @Override
    public String toString() {
        return super.toString() + " - Aggressive";
    }

    @Override
    public AirspaceUserType getType() {
        return AirspaceUserType.AGGRESSIVE;
    }

    @Override
    public Map<Slot, Double> generateWeightMap(Flight flight) {
        return null;
    }

    @Override
    public void generateFlightAttributes(Flight flight, List<Slot> possibleSlots) {

    }
}
