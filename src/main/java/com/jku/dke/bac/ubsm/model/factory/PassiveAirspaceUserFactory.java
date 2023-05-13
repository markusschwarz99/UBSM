package com.jku.dke.bac.ubsm.model.factory;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.PassiveAirspaceUser;

public class PassiveAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate() {
        return new PassiveAirspaceUser();
    }
}
