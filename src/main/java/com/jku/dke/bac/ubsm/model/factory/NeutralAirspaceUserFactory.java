package com.jku.dke.bac.ubsm.model.factory;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.NeutralAirspaceUser;

public class NeutralAirspaceUserFactory implements AirspaceUserFactory {
    @Override
    public AirspaceUser generate() {
        return new NeutralAirspaceUser();
    }
}
