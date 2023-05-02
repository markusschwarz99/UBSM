package com.jku.dke.bac.ubsm.factory;


import com.jku.dke.bac.ubsm.model.au.AggressiveAirspaceUser;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;

public class AggressiveAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate(String name, double credits) {
        return new AggressiveAirspaceUser(name, credits);
    }
}
