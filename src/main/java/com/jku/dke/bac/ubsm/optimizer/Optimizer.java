package com.jku.dke.bac.ubsm.optimizer;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.Map;

public abstract class Optimizer {
    abstract Map<Slot, Flight> optimize(Map<Slot, Flight> flightList);
}
