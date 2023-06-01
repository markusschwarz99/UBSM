package com.jku.dke.bac.ubsm.optimizer;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HeuristicOptimizer extends Optimizer{
    public HeuristicOptimizer() {
        super();
    }

    @Override
    public Map<Slot, Flight> optimize(Map<Slot, Flight> flightList) {
        return null;
    }
}
