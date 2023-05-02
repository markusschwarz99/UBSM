package com.jku.dke.bac.ubsm.controller;

import com.jku.dke.bac.ubsm.factory.AirspaceUserFactory;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.AirspaceUserType;
import com.jku.dke.bac.ubsm.repos.AirspaceUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AirspaceUserController {
    @Value("${initialCredits}")
    private double initialCredits;
    private AirspaceUserRepository repository;

    @Autowired
    private AirspaceUserFactory airspaceUserFactory;

    public AirspaceUserController(final AirspaceUserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/airspaceUsers")
    public List<AirspaceUser> getAirspaceUsers() {
        return repository.findAll();
    }

    @PostMapping("/newNeutralAirspaceUser")
    public ResponseEntity<AirspaceUser> newNeutralAirspaceUser(String name) {
        if (name.isBlank()) return ResponseEntity.badRequest().build();
        if (repository.findById(name).isPresent()) return new ResponseEntity<>(HttpStatus.CONFLICT);
        AirspaceUser airspaceUser = airspaceUserFactory.generate(AirspaceUserType.NEUTRAL);
        airspaceUser.setName(name);
        airspaceUser.setCredits(initialCredits);
        repository.save(airspaceUser);
        return ResponseEntity.ok(airspaceUser);
    }

    @PostMapping("/newPassiveAirspaceUser")
    public ResponseEntity<AirspaceUser> newPassiveAirspaceUser(String name) {
        if (name.isBlank()) return ResponseEntity.badRequest().build();
        if (repository.findById(name).isPresent()) return new ResponseEntity<>(HttpStatus.CONFLICT);
        AirspaceUser airspaceUser = airspaceUserFactory.generate(AirspaceUserType.PASSIVE);
        airspaceUser.setName(name);
        airspaceUser.setCredits(initialCredits);
        repository.save(airspaceUser);
        return ResponseEntity.ok(airspaceUser);
    }

    @PostMapping("/newAggressiveAirspaceUser")
    public ResponseEntity<AirspaceUser> newAggressiveAirspaceUser(String name) {
        if (name.isBlank()) return ResponseEntity.badRequest().build();
        if (repository.findById(name).isPresent()) return new ResponseEntity<>(HttpStatus.CONFLICT);
        AirspaceUser airspaceUser = airspaceUserFactory.generate(AirspaceUserType.AGGRESSIVE);
        airspaceUser.setName(name);
        airspaceUser.setCredits(initialCredits);
        repository.save(airspaceUser);
        return ResponseEntity.ok(airspaceUser);
    }

    @PutMapping("/addCredits")
    public ResponseEntity<AirspaceUser> addCredits(String name, double credits) {
        if (name.isBlank()) return ResponseEntity.badRequest().build();
        if (repository.findById(name).isEmpty()) return ResponseEntity.badRequest().build();
        if (repository.findById(name).get().getCredits() < credits) return ResponseEntity.badRequest().build();
        AirspaceUser airspaceUser = repository.findById(name).get();
        airspaceUser.updateCredits(credits);
        repository.save(airspaceUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAirspaceUser")
    public ResponseEntity<Object> deleteAirspaceUser(String name) {
        if (name.isBlank()) return ResponseEntity.badRequest().build();
        if (repository.findById(name).isEmpty()) return ResponseEntity.badRequest().build();
        repository.delete(repository.findById(name).get());
        return ResponseEntity.ok().build();
    }


}
