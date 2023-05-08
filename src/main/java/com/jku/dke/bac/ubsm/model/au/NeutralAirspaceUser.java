package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.Map;
import java.util.Random;

public class NeutralAirspaceUser extends AirspaceUser {

    private final Random random = new Random();

    @Override
    public String toString() {
        return super.toString() + " - Neutral";
    }

    @Override
    public Map<Slot, Double> generateWeightMap(Flight flight) {
        return null;
    }

}
