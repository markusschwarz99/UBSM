package com.jku.dke.bac.ubsm.model.au.weightMapFunction;

import java.util.function.Function;

public abstract class WeightMap implements Function<Double,Double> {
    public WeightMap() {
    }

    @Override
    public abstract Double apply(Double aDouble);

}
