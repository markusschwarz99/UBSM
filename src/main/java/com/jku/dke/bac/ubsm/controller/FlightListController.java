package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import com.jku.dke.bac.ubsm.service.FlightListService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class FlightListController {

    private final FlightListService flightListService;

    public FlightListController(final FlightListService flightListService) {
        this.flightListService = flightListService;
    }

    @ApiOperation(value = "Get all FlightLists", response = List.class, produces = "application/json")
    @GetMapping(path = {"/flightLists/{index}", "/flightLists"}, produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<List<Map<Slot, Flight>>> getAirspaceUsers(@PathVariable(required = false) Integer index) {
        ResponseEntity<List<Map<Slot, Flight>>> response;
        List<Map<Slot, Flight>> flightLists;
        if (index != null) {
            try {
                flightLists = flightListService.getFlightListByIndex(index);
                response = new ResponseEntity<>(flightLists, HttpStatus.OK);
            } catch (Exception e) {
                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            flightLists = flightListService.getFlightLists();
            response = new ResponseEntity<>(flightLists, HttpStatus.OK);
        }
        return response;
    }

    @ApiOperation(value = "Create a new FlightList", response = Map.class, produces = "application/json", consumes = "application/json")
    @PostMapping(path = "/flightLists", produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<Map<Slot, Flight>> newFlightList(@RequestBody Map<String, Integer> flightDistribution) {
        ResponseEntity<Map<Slot, Flight>> response;
        try {
            Map<Slot, Flight> flightList = flightListService.generateFlightList(flightDistribution);
            response = new ResponseEntity<>(flightList, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ApiOperation(value = "Delete AirspaceUser")
    @DeleteMapping(path = {"/flightLists", "/flightLists/{index}"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<Void> deleteFlightList(@PathVariable(required = false) Integer index) {
        ResponseEntity<Void> response;
        if (index != null) {
            try {
                flightListService.deleteFlightListByIndex(index);
                response = new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            flightListService.deleteAllFlightLists();
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        return response;
    }

}