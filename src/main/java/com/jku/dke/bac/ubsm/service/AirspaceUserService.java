package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirspaceUserService {
    private List<AirspaceUser> airspaceUsers;

    public AirspaceUserService() {
        this.airspaceUsers = new ArrayList<>();
    }

    public AirspaceUser[] getAirspaceUsers() {
        return this.airspaceUsers.toArray(new AirspaceUser[0]);
    }

    public AirspaceUser[] createNewAirspaceUsers(AirspaceUser[] airspaceUsers) throws IllegalArgumentException {
        Arrays.stream(airspaceUsers).forEach(airspaceUser -> this.airspaceUsers.forEach(airspaceUser1 -> {
            if (airspaceUser1.equals(airspaceUser))
                throw new IllegalArgumentException("The AirspaceUser with the name " + airspaceUser.getName() + "already exists");
        }));

        this.airspaceUsers.addAll(Arrays.stream(airspaceUsers).toList());
        return airspaceUsers;
    }

    public boolean deleteAirspaceUser(String name) {
        return this.airspaceUsers.removeIf(airspaceUser -> airspaceUser.getName().equals(name));
    }

    public void deleteAllAirspaceUser() {
        this.airspaceUsers = new ArrayList<>();
    }

    protected List<String> getAirspaceUserNames() {
        return this.airspaceUsers.stream().map(AirspaceUser::getName).collect(Collectors.toList());
    }
}
