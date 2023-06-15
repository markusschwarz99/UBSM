package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.Margin;
import com.jku.dke.bac.ubsm.model.au.weightMapFunction.DefaultWeightMapFunction;
import com.jku.dke.bac.ubsm.model.dto.auDTO.AggressiveAirspaceUserDTO;
import com.jku.dke.bac.ubsm.model.dto.auDTO.AirspaceUserDTO;
import com.jku.dke.bac.ubsm.model.dto.auDTO.NeutralAirspaceUserDTO;
import com.jku.dke.bac.ubsm.model.dto.auDTO.PassiveAirspaceUserDTO;
import com.jku.dke.bac.ubsm.model.factory.AggressiveAirspaceUserFactory;
import com.jku.dke.bac.ubsm.model.factory.NeutralAirspaceUserFactory;
import com.jku.dke.bac.ubsm.model.factory.PassiveAirspaceUserFactory;
import com.jku.dke.bac.ubsm.model.mapper.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AirspaceUserService {
    private final AggressiveAirspaceUserFactory aggressiveAirspaceUserFactory = new AggressiveAirspaceUserFactory();
    private final NeutralAirspaceUserFactory neutralAirspaceUserFactory = new NeutralAirspaceUserFactory();
    private final PassiveAirspaceUserFactory passiveAirspaceUserFactory = new PassiveAirspaceUserFactory();
    @Value("${initialCredits}")
    private double initialCredits;
    @Value("${aggressive.priorityDistribution}")
    private int[] standardPriorityDistributionAggressive;
    @Value("${aggressive.priority.timeToAdd}")
    private int[] priorityTimeToAddAggressive;
    @Value("${aggressive.flexible.notBefore}")
    private double[] flexibleNotBeforeAggressive;
    @Value("${aggressive.flexible.wishedTime}")
    private double[] flexibleWishedTimeAggressive;
    @Value("${aggressive.flexible.notAfter}")
    private double[] flexibleNotAfterAggressive;
    @Value("${aggressive.flexibleWithPriority.notBefore}")
    private double[] flexibleWithPriorityNotBeforeAggressive;
    @Value("${aggressive.flexibleWithPriority.wishedTime}")
    private double[] flexibleWithPriorityWishedTimeAggressive;
    @Value("${aggressive.flexibleWithPriority.notAfter}")
    private double[] flexibleWithPriorityNotAfterAggressive;


    @Value("${neutral.priorityDistribution}")
    private int[] standardPriorityDistributionNeutral;
    @Value("${neutral.priority.timeToAdd}")
    private int[] priorityTimeToAddNeutral;
    @Value("${neutral.flexible.notBefore}")
    private double[] flexibleNotBeforeNeutral;
    @Value("${neutral.flexible.wishedTime}")
    private double[] flexibleWishedTimeNeutral;
    @Value("${neutral.flexible.notAfter}")
    private double[] flexibleNotAfterNeutral;
    @Value("${neutral.flexibleWithPriority.notBefore}")
    private double[] flexibleWithPriorityNotBeforeNeutral;
    @Value("${neutral.flexibleWithPriority.wishedTime}")
    private double[] flexibleWithPriorityWishedTimeNeutral;
    @Value("${neutral.flexibleWithPriority.notAfter}")
    private double[] flexibleWithPriorityNotAfterNeutral;

    @Value("${passive.priorityDistribution}")
    private int[] standardPriorityDistributionPassive;
    @Value("${passive.priority.timeToAdd}")
    private int[] priorityTimeToAddPassive;
    @Value("${passive.flexible.notBefore}")
    private double[] flexibleNotBeforePassive;
    @Value("${passive.flexible.wishedTime}")
    private double[] flexibleWishedTimePassive;
    @Value("${passive.flexible.notAfter}")
    private double[] flexibleNotAfterPassive;
    @Value("${passive.flexibleWithPriority.notBefore}")
    private double[] flexibleWithPriorityNotBeforePassive;
    @Value("${passive.flexibleWithPriority.wishedTime}")
    private double[] flexibleWithPriorityWishedTimePassive;
    @Value("${passive.flexibleWithPriority.notAfter}")
    private double[] flexibleWithPriorityNotAfterPassive;

    @Value("${weightMapFunction}")
    private String weightMapFunction;


    private List<AirspaceUser> airspaceUsers;

    public AirspaceUserService() {
        this.airspaceUsers = new ArrayList<>();
    }

    public AirspaceUserDTO[] getAirspaceUsersDTO() {
        return Mapper.mapAirspaceUserToAirspaceUserDTO(airspaceUsers.toArray(new AirspaceUser[0]));
    }

    protected AirspaceUser[] getAirspaceUsers() {
        return airspaceUsers.toArray(new AirspaceUser[0]);
    }

    public AirspaceUserDTO[] createNewAirspaceUsers(AirspaceUserDTO[] airspaceUsersDTO) throws IllegalArgumentException {
        Arrays.stream(airspaceUsersDTO).forEach(airspaceUserDTO -> {
            // check that name of AU is always unique
            this.airspaceUsers.forEach(airspaceUser -> {
                if (airspaceUser.getName().equals(airspaceUserDTO.getName()))
                    throw new IllegalArgumentException("The AirspaceUser with the name " + airspaceUserDTO.getName() + "already exists");
            });
            AirspaceUser au = null;
            if (airspaceUserDTO.getClass().equals(AggressiveAirspaceUserDTO.class)) {
                au = aggressiveAirspaceUserFactory.generate();
            } else if (airspaceUserDTO.getClass().equals(NeutralAirspaceUserDTO.class)) {
                au = neutralAirspaceUserFactory.generate();
            } else if (airspaceUserDTO.getClass().equals(PassiveAirspaceUserDTO.class)) {
                au = passiveAirspaceUserFactory.generate();
            }
            if (au == null) throw new IllegalArgumentException("AirspaceUser is null");

            au.setName(airspaceUserDTO.getName());

            if (airspaceUserDTO.getCredits() == 0.0) {
                au.setCredits(initialCredits);
            } else {
                au.setCredits(airspaceUserDTO.getCredits());
            }

            // priority Distribution
            if (isValidPriorityDistribution(airspaceUserDTO)) {
                au.setPriorityDistribution(calculatePriorityDistribution(airspaceUserDTO));
            } else {
                switch (airspaceUserDTO.getClass().getSimpleName()) {
                    case "AggressiveAirspaceUserDTO" ->
                            au.setPriorityDistribution(standardPriorityDistributionAggressive);
                    case "NeutralAirspaceUserDTO" -> au.setPriorityDistribution(standardPriorityDistributionNeutral);
                    case "PassiveAirspaceUserDTO" -> au.setPriorityDistribution(standardPriorityDistributionPassive);
                }
            }

            // priority minutes to add
            if (airspaceUserDTO.getPriorityFlightMinutesToAdd() == null) {
                switch (airspaceUserDTO.getClass().getSimpleName()) {
                    case "AggressiveAirspaceUserDTO" -> au.setPriorityFlightMinutesToAdd(priorityTimeToAddAggressive);
                    case "NeutralAirspaceUserDTO" -> au.setPriorityFlightMinutesToAdd(priorityTimeToAddNeutral);
                    case "PassiveAirspaceUserDTO" -> au.setPriorityFlightMinutesToAdd(priorityTimeToAddPassive);
                }
            } else {
                au.setPriorityFlightMinutesToAdd(airspaceUserDTO.getPriorityFlightMinutesToAdd());
            }

            // flexible
            if (airspaceUserDTO.getFlexibleFlightPercentages() == null) {
                switch (airspaceUserDTO.getClass().getSimpleName()) {
                    case "AggressiveAirspaceUserDTO" ->
                            au.setFlexibleFlightPercentages(new Margin[]{new Margin(flexibleNotBeforeAggressive[0], flexibleNotBeforeAggressive[1]), new Margin(flexibleWishedTimeAggressive[0], flexibleWishedTimeAggressive[1]), new Margin(flexibleNotAfterAggressive[0], flexibleNotAfterAggressive[1])});
                    case "NeutralAirspaceUserDTO" ->
                            au.setFlexibleFlightPercentages(new Margin[]{new Margin(flexibleNotBeforeNeutral[0], flexibleNotBeforeNeutral[1]), new Margin(flexibleWishedTimeNeutral[0], flexibleWishedTimeNeutral[1]), new Margin(flexibleNotAfterNeutral[0], flexibleNotAfterNeutral[1])});
                    case "PassiveAirspaceUserDTO" ->
                            au.setFlexibleFlightPercentages(new Margin[]{new Margin(flexibleNotBeforePassive[0], flexibleNotBeforePassive[1]), new Margin(flexibleWishedTimePassive[0], flexibleWishedTimePassive[1]), new Margin(flexibleNotAfterPassive[0], flexibleNotAfterPassive[1])});
                }
            } else {
                au.setFlexibleFlightPercentages(airspaceUserDTO.getFlexibleFlightPercentages());
            }

            // flexible with priority
            if (airspaceUserDTO.getFlexibleFlightWithPriorityPercentages() == null) {
                switch (airspaceUserDTO.getClass().getSimpleName()) {
                    case "AggressiveAirspaceUserDTO" ->
                            au.setFlexibleFlightWithPriorityPercentages(new Margin[]{new Margin(flexibleWithPriorityNotBeforeAggressive[0], flexibleWithPriorityNotBeforeAggressive[1]), new Margin(flexibleWithPriorityWishedTimeAggressive[0], flexibleWithPriorityWishedTimeAggressive[1]), new Margin(flexibleWithPriorityNotAfterAggressive[0], flexibleWithPriorityNotAfterAggressive[1])});
                    case "NeutralAirspaceUserDTO" ->
                            au.setFlexibleFlightWithPriorityPercentages(new Margin[]{new Margin(flexibleWithPriorityNotBeforeNeutral[0], flexibleWithPriorityNotBeforeNeutral[1]), new Margin(flexibleWithPriorityWishedTimeNeutral[0], flexibleWithPriorityWishedTimeNeutral[1]), new Margin(flexibleWithPriorityNotAfterNeutral[0], flexibleWithPriorityNotAfterNeutral[1])});
                    case "PassiveAirspaceUserDTO" ->
                            au.setFlexibleFlightWithPriorityPercentages(new Margin[]{new Margin(flexibleWithPriorityNotBeforePassive[0], flexibleWithPriorityNotBeforePassive[1]), new Margin(flexibleWithPriorityWishedTimePassive[0], flexibleWithPriorityWishedTimePassive[1]), new Margin(flexibleWithPriorityNotAfterPassive[0], flexibleWithPriorityNotAfterPassive[1])});
                }
            } else {
                au.setFlexibleFlightWithPriorityPercentages(airspaceUserDTO.getFlexibleFlightPercentages());
            }

            // weightMap function
            if (airspaceUserDTO.getWeightMapFunction() == null) {
                airspaceUserDTO.setWeightMapFunction(weightMapFunction);
            }

            switch (airspaceUserDTO.getWeightMapFunction()) {
                case ("DefaultWeightMapFunction"):
                    au.setWeightMapFunction(new DefaultWeightMapFunction());
                default:
                    au.setWeightMapFunction(new DefaultWeightMapFunction());
            }
            this.airspaceUsers.add(au);
        });
        return Mapper.mapAirspaceUserToAirspaceUserDTO(this.airspaceUsers.subList(this.airspaceUsers.size() - airspaceUsersDTO.length, this.airspaceUsers.size()).toArray(new AirspaceUser[airspaceUsersDTO.length]));
    }

    public boolean deleteAirspaceUser(String name) {
        return this.airspaceUsers.removeIf(airspaceUser -> airspaceUser.getName().equals(name));
    }

    public void deleteAllAirspaceUser() {
        this.airspaceUsers = new ArrayList<>();
    }

    private boolean isValidPriorityDistribution(AirspaceUserDTO airspaceUserDTO) {
        Integer priorityFlight = airspaceUserDTO.getProbabilityPriorityFlight();
        Integer flexiblePriorityFlight = airspaceUserDTO.getProbabilityFlexibleWithPriorityFlight();
        Integer flexibleFlight = airspaceUserDTO.getProbabilityFlexibleFlight();
        if (priorityFlight != null && flexiblePriorityFlight != null && flexibleFlight != null) {
            return priorityFlight + flexiblePriorityFlight + flexibleFlight == 100;
        } else if (priorityFlight != null && flexiblePriorityFlight != null) {
            return priorityFlight + flexiblePriorityFlight == 100;
        } else if (priorityFlight != null && flexibleFlight != null) {
            return priorityFlight + flexibleFlight == 100;
        } else if (priorityFlight == null && flexiblePriorityFlight != null && flexibleFlight != null) {
            return flexiblePriorityFlight + flexibleFlight == 100;
        }
        return false;
    }

    private int[] calculatePriorityDistribution(AirspaceUserDTO airspaceUserDTO) {
        int priorityFlight = airspaceUserDTO.getProbabilityPriorityFlight() != null ? airspaceUserDTO.getProbabilityPriorityFlight() : 0;
        int flexiblePriorityFlight = airspaceUserDTO.getProbabilityFlexibleWithPriorityFlight() != null ? airspaceUserDTO.getProbabilityFlexibleWithPriorityFlight() : 0;
        int flexibleFlight = airspaceUserDTO.getProbabilityFlexibleFlight() != null ? airspaceUserDTO.getProbabilityFlexibleFlight() : 0;
        if (flexibleFlight == 0) {
            return new int[]{100 - priorityFlight - flexiblePriorityFlight, 100 - priorityFlight};
        }
        return new int[]{flexibleFlight, 100 - priorityFlight};
    }

    protected double getInitialCredits() {
        return initialCredits;
    }
}
