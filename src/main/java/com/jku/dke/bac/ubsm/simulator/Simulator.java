package com.jku.dke.bac.ubsm.simulator;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.Map;

public abstract class Simulator {
    public abstract void clearing(Map<Slot, Flight> optimizedFlightList, double utilityIncrease);
}
