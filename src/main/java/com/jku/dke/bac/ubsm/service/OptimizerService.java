package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.model.dto.FlightListDTO;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.model.mapper.Mapper;
import com.jku.dke.bac.ubsm.optimizer.HeuristicOptimizer;
import com.jku.dke.bac.ubsm.optimizer.HungarianOptimizer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OptimizerService {
    private final FlightListService flightListService;
    private final HungarianOptimizer hungarianOptimizer;
    private final HeuristicOptimizer heuristicOptimizer;

    public OptimizerService(final FlightListService flightListService, final HungarianOptimizer hungarianOptimizer, final HeuristicOptimizer heuristicOptimizer) {
        this.flightListService = flightListService;
        this.hungarianOptimizer = hungarianOptimizer;
        this.heuristicOptimizer = heuristicOptimizer;
    }

    public FlightListDTO optimizeFlightList() {
        if (flightListService.getFlightLists().size() == 0)
            throw new IllegalArgumentException("There are 0 flightLists");

        Map<Slot, Flight> initialFlightList = flightListService.getFlightLists().get(flightListService.getFlightLists().size() - 1);
        Map<Slot, Flight> optimizedFlightList = hungarianOptimizer.optimize(initialFlightList);

        return Mapper.mapFlightListToFlightListDTO(flightListService.getFlightLists().size() - 1, optimizedFlightList);
    }

    public FlightListDTO optimizeFlightList(int index) {
        if (index < 0 || index >= flightListService.getFlightLists().size())
            throw new IllegalArgumentException("Invalid index");

        Map<Slot, Flight> initialFlightList = flightListService.getFlightLists().get(index);
        Map<Slot, Flight> optimizedFlightList = hungarianOptimizer.optimize(initialFlightList);

        return Mapper.mapFlightListToFlightListDTO(index, optimizedFlightList);
    }
}
