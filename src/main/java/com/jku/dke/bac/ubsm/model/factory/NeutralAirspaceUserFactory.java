package com.jku.dke.bac.ubsm.model.factory;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.NeutralAirspaceUser;

public class NeutralAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate(String name, double credits) {
        AirspaceUser airspaceUser = new NeutralAirspaceUser();
        airspaceUser.setName(name);
        airspaceUser.setCredits(credits);
        return airspaceUser;
    }
}
