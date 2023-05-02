package com.jku.dke.bac.ubsm.factory;


import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.AirspaceUserType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;

@Component
public class AirspaceUserFactory {

    @Autowired
    private List<AirspaceUser> airspaceUsers;

    private EnumMap<AirspaceUserType, AirspaceUser> airspaceUserEnumMap;

    @PostConstruct
    public void initAirspaceUserFactory() {
        this.airspaceUserEnumMap = new EnumMap<>(AirspaceUserType.class);
        for (AirspaceUser airspaceUser : airspaceUsers) {
            this.airspaceUserEnumMap.put(airspaceUser.getType(), airspaceUser);
        }
    }

    public AirspaceUser generate(AirspaceUserType airspaceUserType) {
        return this.airspaceUserEnumMap.get(airspaceUserType);
    }
}
