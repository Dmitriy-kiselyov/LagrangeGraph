package ru.pussy_penetrator.graph.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by Pussy_penetrator on 20.09.2016.
 */
public class Graphic {

    private Func            mFunction        = null;
    private InterpolateFunc mInterpolateFunc = null;
    private LagrangeFunc    mLagrangeFunc    = null;
    private NewtonFunc      mNewtonFunc      = null;

    private double mFromX;
    private double mToX;

    private IntegerProperty mNodeCount;
    private BooleanProperty mShowNewton;

    public Graphic() {
        mFromX = 0;
        mToX = 1;
        mNodeCount = new SimpleIntegerProperty(10);
        mShowNewton = new SimpleBooleanProperty(false);
    }

    public Func getFunction() {
        return mFunction;
    }

    public void setFunction(Func function) {
        mFunction = function;
    }

    public InterpolateFunc getInterpolateFunc() {
        return mInterpolateFunc;
    }

    public void setInterpolateFunc(InterpolateFunc interpolateFunc) {
        mInterpolateFunc = interpolateFunc;
    }

    public double getFromX() {
        return mFromX;
    }

    public void setFromX(double fromX) throws IllegalArgumentException {
        if (fromX >= mToX || Math.abs(fromX) > 1e+18)
            throw new IllegalArgumentException();
        mFromX = fromX;
    }

    public double getToX() {
        return mToX;
    }

    public void setToX(double toX) throws IllegalArgumentException {
        if (mFromX >= toX || Math.abs(toX) > 1e+18)
            throw new IllegalArgumentException();
        mToX = toX;
    }

    public int getNodeCount() {
        return mNodeCount.get();
    }

    public void setNodeCount(int nodeCount) {
        this.mNodeCount.set(nodeCount);
    }

    public IntegerProperty nodeCountProperty() {
        return mNodeCount;
    }

    public boolean isShowNewton() {
        return mShowNewton.get();
    }

    public void setShowNewton(boolean showNewton) {
        this.mShowNewton.set(showNewton);
    }

    public BooleanProperty showNewtonProperty() {
        return mShowNewton;
    }

    public void apply() {
        if (mFunction == null || mInterpolateFunc == null) {
            mLagrangeFunc = null;
            mNewtonFunc = null;
            return;
        }

        mLagrangeFunc = new LagrangeFunc(mFromX, mToX, mNodeCount.intValue()) {
            private Func func;
            private InterpolateFunc iFunc;

            @Override
            protected double interpolate(int i) {
                if (iFunc == null)
                    iFunc = mInterpolateFunc;
                return iFunc.get(mFromX, mToX, mNodeCount.intValue(), i);
            }

            @Override
            public double get(double x) {
                if (func == null)
                    func = mFunction;
                return func.get(x);
            }
        };

        if (mShowNewton.getValue()) {
            mNewtonFunc = new NewtonFunc(mFromX, mToX, 10) {
                private Func func;

                @Override
                public double get(double x) {
                    if (func == null)
                        func = mFunction;
                    return func.get(x);
                }
            };
        } else mNewtonFunc = null;
    }

    public LagrangeFunc getLagrangeFunc() {
        return mLagrangeFunc;
    }

    public NewtonFunc getNewtonFunc() {
        return mNewtonFunc;
    }
}
