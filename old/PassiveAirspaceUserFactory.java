package com.jku.dke.bac.ubsm.factory;


import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.PassiveAirspaceUser;

public class PassiveAirspaceUserFactory implements AirspaceUserFactory{
    @Override
    public AirspaceUser generate(String name, double credits) {
        return new PassiveAirspaceUser(name, credits);
    }
}
