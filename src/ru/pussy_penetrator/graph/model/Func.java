package ru.pussy_penetrator.graph.model;

/**
 * Created by Pussy_penetrator on 20.09.2016.
 */
public abstract class Func {

    private String mName;

    public Func(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public abstract double get(double x);

    @Override
    public String toString() {
        return mName;
    }
}
