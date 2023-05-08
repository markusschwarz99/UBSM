package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class AggressiveAirspaceUser extends AirspaceUser {

    private final Random random = new Random();

    @Override
    public String toString() {
        return super.toString() + " - Aggressive";
    }


    @Override
    public Map<Slot, Double> generateWeightMap(Flight flight) {
        return null;
    }

}
