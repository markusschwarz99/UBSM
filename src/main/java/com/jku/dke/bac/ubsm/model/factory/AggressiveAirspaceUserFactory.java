package com.jku.dke.bac.ubsm.model.factory;

import com.jku.dke.bac.ubsm.model.au.AggressiveAirspaceUser;
import com.jku.dke.bac.ubsm.model.au.AirspaceUser;

public class AggressiveAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate() {
        return new AggressiveAirspaceUser();
    }
}
