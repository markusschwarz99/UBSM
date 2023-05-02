package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AirspaceUserService {
    private List<AirspaceUser> airspaceUsers;

    public AirspaceUserService() {
        this.airspaceUsers = new ArrayList<>();
    }

    public AirspaceUser[] getAirspaceUsers() {
        return this.airspaceUsers.toArray(new AirspaceUser[0]);
    }

    public AirspaceUser createNewAirspaceUser(AirspaceUser airspaceUser){
        this.airspaceUsers.add(airspaceUser);
        return airspaceUser;
    }

    public AirspaceUser[] createNewAirspaceUsers(AirspaceUser[] airspaceUsers) {
        this.airspaceUsers.addAll(Arrays.stream(airspaceUsers).toList());
        return airspaceUsers;
    }

    public boolean deleteAirspaceUser(String name) {
        return this.airspaceUsers.removeIf(airspaceUser -> airspaceUser.getName().equals(name));
    }

    public void deleteAllAirspaceUser(){
        this.airspaceUsers = new ArrayList<>();
    }

}
