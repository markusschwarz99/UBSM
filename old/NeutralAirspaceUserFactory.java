package com.jku.dke.bac.ubsm.factory;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.NeutralAirspaceUser;

public class NeutralAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate(String name, double credits) {
        return new NeutralAirspaceUser(name, credits);
    }
}
