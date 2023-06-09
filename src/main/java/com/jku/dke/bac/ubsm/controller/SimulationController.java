package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.dto.statisticsDTO.OverviewDTO;
import com.jku.dke.bac.ubsm.model.dto.statisticsDTO.RunStatisticDTO;
import com.jku.dke.bac.ubsm.service.SimulationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(final SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @ApiOperation(value = "Start a simulation")
    @PostMapping(path = "start/{optimizer}/{simulator}")
    @ApiResponses({@ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<Void> start(@PathVariable String optimizer, @PathVariable String simulator) {
        Logger.log("SimulationController - REST: start simulation ...");
        ResponseEntity<Void> response;
        try {
            simulationService.startSimulation(optimizer, simulator);
            Logger.log("SimulationController - Simulation started with " + optimizer + " and/or " + simulator + " ...");
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Logger.log("SimulationController - Unable to start simulation with " + optimizer + " and/or " + simulator + " ...");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ApiOperation(value = "Start a iteration", response = RunStatisticDTO.class, produces = "application/json", consumes = "application/json")
    @PostMapping(path = "/run/{delayInMinutes}", produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<RunStatisticDTO> run(@RequestBody Map<String, Integer> flightDistribution, @PathVariable Integer delayInMinutes) {
        Logger.log("SimulationController - REST: start iteration ...");
        ResponseEntity<RunStatisticDTO> response;
        try {
            RunStatisticDTO runStatisticDTO = simulationService.runIteration(flightDistribution, delayInMinutes);
            Logger.log("SimulationController - Iteration done ...");
            response = new ResponseEntity<>(runStatisticDTO, HttpStatus.OK);
        } catch (Exception e) {
            Logger.log("SimulationController - Iteration Error with flightDistribution " + flightDistribution.toString() + " or with the delayInMinutes " + delayInMinutes + " ...");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ApiOperation(value = "End simulation", response = OverviewDTO.class, produces = "application/json")
    @DeleteMapping(path = "/end", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<OverviewDTO> end() {
        Logger.log("SimulationController - REST: ending the simulation ...");
        ResponseEntity<OverviewDTO> response;
        try {
            OverviewDTO overviewDTO = simulationService.endSimulation();
            Logger.log("SimulationController - Simulation ended successfully ...");
            response = new ResponseEntity<>(overviewDTO, HttpStatus.OK);
        } catch (Exception e) {
            Logger.log("SimulationController - Error when ending the simulation ...");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}
