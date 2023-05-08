package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.service.AirspaceUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class AirspaceUserController {
    private final AirspaceUserService airspaceUserService;
    private Random random;

    public AirspaceUserController(AirspaceUserService airspaceUserService) {
        this.airspaceUserService = airspaceUserService;
    }

    @ApiOperation(value = "Get all AirspaceUser", response = AirspaceUser[].class, produces = "application/json")
    @GetMapping(path = "/airspaceUsers", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    public ResponseEntity<AirspaceUser[]> getAirspaceUsers() {
        AirspaceUser[] airspaceUsers = airspaceUserService.getAirspaceUsers();
        return new ResponseEntity<>(airspaceUsers, HttpStatus.OK);
    }

    @ApiOperation(value = "Create new AirspaceUsers", response = AirspaceUser[].class, produces = "application/json", consumes = "application/json")
    @PostMapping(path = "/airspaceUsers", produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<AirspaceUser[]> newAirspaceUsers(@RequestBody AirspaceUser[] airspaceUsers) {
        ResponseEntity<AirspaceUser[]> response;
        try {
            AirspaceUser[] aus = airspaceUserService.createNewAirspaceUsers(airspaceUsers);
            response = new ResponseEntity<>(aus, HttpStatus.OK);
        } catch (Exception e) {
            response = new ResponseEntity<>(airspaceUsers, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ApiOperation(value = "Delete AirspaceUser")
    @DeleteMapping(path = "airspaceUsers/{name}")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<Void> deleteAirspaceUser(@PathVariable @ApiParam(value = "the AirspaceUser's' name") String name) {
        ResponseEntity<Void> response;
        try {
            if (airspaceUserService.deleteAirspaceUser(name)) response = new ResponseEntity<>(HttpStatus.OK);
            else response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ApiOperation(value = "Delete all AirspaceUsers")
    @DeleteMapping(path = "airspaceUsers")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<Void> deleteAirspaceUser() {
        airspaceUserService.deleteAllAirspaceUser();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
