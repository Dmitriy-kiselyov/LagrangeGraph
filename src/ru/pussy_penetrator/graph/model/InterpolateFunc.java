package ru.pussy_penetrator.graph.model;

/**
 * Created by Pussy_penetrator on 20.09.2016.
 */
public abstract class InterpolateFunc {

    private String mName;

    public InterpolateFunc(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public abstract double get(double a, double b, int n, int i);

    @Override
    public String toString() {
        return mName;
    }

}
