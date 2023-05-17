package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.service.OptimizerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OptimizerController {
    private final OptimizerService optimizerService;

    public OptimizerController(OptimizerService optimizerService) {
        this.optimizerService = optimizerService;
    }

    @ApiOperation(value = "Optimize a FlightList", response = Map.class, produces = "application/json")
    @PostMapping(path = {"/optimize", "/optimize/{index}"}, produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<Map<Slot, Flight>> optimize(@PathVariable(required = false) Integer index) {
        ResponseEntity<Map<Slot, Flight>> response;
        try {
            Map<Slot, Flight> optimizedFlightList;
            if (index == null) optimizedFlightList = optimizerService.optimizeFlightList();
            else optimizedFlightList = optimizerService.optimizeFlightList(index);
            response = new ResponseEntity<>(optimizedFlightList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}
