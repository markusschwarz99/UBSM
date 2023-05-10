package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OptimizerController {

    @PostMapping("/optimize")
    public ResponseEntity<Map<Slot, Flight>> optimize() {

        return null;
    }
}
