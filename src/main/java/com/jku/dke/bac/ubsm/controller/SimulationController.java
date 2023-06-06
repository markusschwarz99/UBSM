package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.Logger;
import com.jku.dke.bac.ubsm.model.dto.OverviewDTO;
import com.jku.dke.bac.ubsm.model.dto.StatisticDTO;
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
            Logger.log("SimulationController - Simulation started with " + optimizer + " ...");
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Logger.log("SimulationController - Unable to start simulation with " + optimizer + " ...");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ApiOperation(value = "Start a iteration", response = Map.class, produces = "application/json", consumes = "application/json")
    @PostMapping(path = "/run", produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<Void> run(@RequestBody Map<String, Integer> flightDistribution) {
        Logger.log("SimulationController - REST: start iteration ...");
        ResponseEntity<Void> response;
        try {
            simulationService.runIteration(flightDistribution);
            Logger.log("SimulationController - Iteration done ...");
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Logger.log("SimulationController - Iteration Error with flightDistribution " + flightDistribution.toString() + " ...");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ApiOperation(value = "Get all FlightLists", response = StatisticDTO.class, produces = "application/json")
    @GetMapping(path = "/statistic/{runId}", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<StatisticDTO> getRunStatistic(@PathVariable int runId) {
        Logger.log("SimulationController - REST: start getting statistic for run " + runId + " ...");
        ResponseEntity<StatisticDTO> response;
        try {
            StatisticDTO statisticDTO = simulationService.getStatistic(runId);
            Logger.log("SimulationController - Statistic created ...");
            response = new ResponseEntity<>(statisticDTO, HttpStatus.OK);
        } catch (Exception e) {
            Logger.log("SimulationController - Error when creating a statistic for run " + runId + " ...");
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
