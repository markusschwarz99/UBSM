package com.jku.dke.bac.ubsm.model.factory;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.PassiveAirspaceUser;

public class PassiveAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate(String name, double credits) {
        AirspaceUser airspaceUser = new PassiveAirspaceUser();
        airspaceUser.setName(name);
        airspaceUser.setCredits(credits);
        return airspaceUser;
    }
}
