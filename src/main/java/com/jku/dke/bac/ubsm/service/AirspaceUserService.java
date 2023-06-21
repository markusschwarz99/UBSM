package com.jku.dke.bac.ubsm.service;

import com.jku.dke.bac.ubsm.Logger;
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
        Logger.log("AirspaceUserService - creating AUs of " + airspaceUsersDTO.length + " AU DTOs");
        Arrays.stream(airspaceUsersDTO).forEach(airspaceUserDTO -> {
            // check that name of AU is always unique
            this.airspaceUsers.forEach(airspaceUser -> {
                if (airspaceUser.getName().equals(airspaceUserDTO.getName())) {
                    Logger.log("AirspaceUserService - The AirspaceUser with the name " + airspaceUserDTO.getName() + "already exists");
                    throw new IllegalArgumentException("The AirspaceUser with the name " + airspaceUserDTO.getName() + "already exists");
                }
            });
            AirspaceUser au = null;
            if (airspaceUserDTO.getClass().equals(AggressiveAirspaceUserDTO.class)) {
                au = aggressiveAirspaceUserFactory.generate();
                Logger.log("AirspaceUserService - New aggressive AU created");
            } else if (airspaceUserDTO.getClass().equals(NeutralAirspaceUserDTO.class)) {
                au = neutralAirspaceUserFactory.generate();
                Logger.log("AirspaceUserService - New neutral AU created");
            } else if (airspaceUserDTO.getClass().equals(PassiveAirspaceUserDTO.class)) {
                au = passiveAirspaceUserFactory.generate();
                Logger.log("AirspaceUserService - New passive AU created");
            }
            if (au == null) {
                Logger.log("AirspaceUserService - Invalid presetting");
                throw new IllegalArgumentException("AirspaceUser is null");
            }

            au.setName(airspaceUserDTO.getName());
            Logger.log("AirspaceUserService - Name set to " + au.getName());

            if (airspaceUserDTO.getCredits() == null || airspaceUserDTO.getCredits() < 0) {
                au.setCredits(initialCredits);
            } else {
                au.setCredits(airspaceUserDTO.getCredits());
            }

            Logger.log("AirspaceUserService - Credits set to " + au.getCredits());

            // priority Distribution
            if (airspaceUserDTO.getProbabilityPriorityFlight() == null && airspaceUserDTO.getProbabilityFlexibleFlight() == null && airspaceUserDTO.getProbabilityFlexibleWithPriorityFlight() == null) {
                switch (airspaceUserDTO.getClass().getSimpleName()) {
                    case "AggressiveAirspaceUserDTO" ->
                            au.setPriorityDistribution(standardPriorityDistributionAggressive);
                    case "NeutralAirspaceUserDTO" -> au.setPriorityDistribution(standardPriorityDistributionNeutral);
                    case "PassiveAirspaceUserDTO" -> au.setPriorityDistribution(standardPriorityDistributionPassive);
                }
                Logger.log("AirspaceUserService - Using the default priority distribution");
            } else if (isValidPriorityDistribution(airspaceUserDTO)) {
                au.setPriorityDistribution(calculatePriorityDistribution(airspaceUserDTO));
                Logger.log("AirspaceUserService - Used custom priority distribution");
            } else {
                Logger.log("AirspaceUserService - Invalid priority distribution");
                throw new IllegalArgumentException("Invalid priority distribution");
            }

            // priority minutes to add
            if (airspaceUserDTO.getPriorityFlightMinutesToAdd() == null) {
                switch (airspaceUserDTO.getClass().getSimpleName()) {
                    case "AggressiveAirspaceUserDTO" -> au.setPriorityFlightMinutesToAdd(priorityTimeToAddAggressive);
                    case "NeutralAirspaceUserDTO" -> au.setPriorityFlightMinutesToAdd(priorityTimeToAddNeutral);
                    case "PassiveAirspaceUserDTO" -> au.setPriorityFlightMinutesToAdd(priorityTimeToAddPassive);
                }
                Logger.log("AirspaceUserService - Using default priorityFlightMinutesToAdd");
            } else if (Arrays.stream(airspaceUserDTO.getPriorityFlightMinutesToAdd()).anyMatch(value -> value < 0)) {
                Logger.log("AirspaceUserService - Invalid priorityFlightMinutesToAdd");
                throw new IllegalArgumentException("All values in priorityFlightMinutesToAdd must be > 0");
            } else {
                au.setPriorityFlightMinutesToAdd(airspaceUserDTO.getPriorityFlightMinutesToAdd());
                Logger.log("AirspaceUserService - Using custom priorityFlightMinutesToAdd");
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
                Logger.log("AirspaceUserService - Using default flexibleFlightPercentages");
            } else if (Arrays.stream(airspaceUserDTO.getFlexibleFlightPercentages()).anyMatch(margin -> margin.getLowerBound() < 0 || margin.getUpperBound() < 0 || margin.getLowerBound() > 1 || margin.getUpperBound() > 1)) {
                Logger.log("AirspaceUserService - Invalid flexibleFlightPercentages");
                throw new IllegalArgumentException("lowerBound in FlexibleFlightPercentages must be < 0 and upperBound must be > 1 ");
            } else {
                au.setFlexibleFlightPercentages(airspaceUserDTO.getFlexibleFlightPercentages());
                Logger.log("AirspaceUserService - Using custom flexibleFlightPercentages");
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
                Logger.log("AirspaceUserService - Using default flexibleFlightWithPriorityPercentages");
            } else if (Arrays.stream(airspaceUserDTO.getFlexibleFlightWithPriorityPercentages()).anyMatch(margin -> margin.getLowerBound() < 0 || margin.getUpperBound() < 0 || margin.getLowerBound() > 1 || margin.getUpperBound() > 1)) {
                Logger.log("AirspaceUserService - Invalid flexibleFlightWithPriorityPercentages");
                throw new IllegalArgumentException("lowerBound in FlexibleFlightWithPriorityPercentages must be < 0 and upperBound must be > 1 ");
            } else {
                au.setFlexibleFlightWithPriorityPercentages(airspaceUserDTO.getFlexibleFlightPercentages());
                Logger.log("AirspaceUserService - Using custom flexibleFlightWithPriorityPercentages");
            }

            // weightMap function
            if (airspaceUserDTO.getWeightMapFunction() == null) {
                airspaceUserDTO.setWeightMapFunction(weightMapFunction);
                au.setWeightMapFunction(new DefaultWeightMapFunction());
                Logger.log("AirspaceUserService - Using default weightMapFunction");
            } else if (airspaceUserDTO.getWeightMapFunction().equals("DefaultWeightMapFunction")) {
                au.setWeightMapFunction(new DefaultWeightMapFunction());
                Logger.log("AirspaceUserService - Using custom " + au.getWeightMapFunction().getClass().getSimpleName());
            } else {
                Logger.log("AirspaceUserService - Invalid wightMapFunction");
                throw new IllegalArgumentException("Weight map function is not implemented");
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
