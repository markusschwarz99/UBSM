package com.jku.dke.bac.ubsm.model.factory;

import com.jku.dke.bac.ubsm.model.au.AggressiveAirspaceUser;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;

public class AggressiveAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate(String name, double credits) {
        AirspaceUser airspaceUser = new AggressiveAirspaceUser();
        airspaceUser.setName(name);
        airspaceUser.setCredits(credits);
        return airspaceUser;
    }
}
