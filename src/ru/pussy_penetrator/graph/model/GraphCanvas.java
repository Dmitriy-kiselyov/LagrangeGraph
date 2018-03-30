package ru.pussy_penetrator.graph.model;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Pussy_penetrator on 20.09.2016.
 */
public class GraphCanvas extends Canvas {

    private final int           N                  = 1500;
    private final Font          TEXT_FONT          = new Font(16);
    private final Font          COORD_FONT         = new Font(12);
    private final double        MIN_PRECISION      = 0.0001d;
    private final DecimalFormat COORD_FORMAT       =
            new DecimalFormat("#.####", DecimalFormatSymbols.getInstance(Locale.US));
    private final DecimalFormat COORD_FORMAT_SCIEN =
            new DecimalFormat("#.##E0", DecimalFormatSymbols.getInstance(Locale.US));

    private LagrangeFunc mLagrangeFunc;
    private NewtonFunc   mNewtonFunc;
    private Screen       mScreen;

    private double mLastPressX = -1, mLastPressY = -1;
    private double mLastMoveX = Double.NaN, mLastMoveY = Double.NaN;

    private double[] mLX, mX, mY, mLY, mNY;
    private double mMinY, mMaxY;

    public GraphCanvas() {
        super();
        setupListeners();
    }

    public GraphCanvas(double width, double height) {
        super(width, height);
        setupListeners();
    }

    private void setupListeners() {
        widthProperty().addListener((observable, oldValue, newValue) -> {
            if (mScreen != null) {
                double w_diff =
                        ((double) newValue - (double) oldValue) / ((double) oldValue / mScreen.width()); //fromPix
                mScreen.left -= w_diff / 2;
                mScreen.right += w_diff / 2;
            }
            redraw();
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {
            if (mScreen != null) {
                double h_diff = fromPix((double) newValue - (double) oldValue);
                mScreen.bottom -= h_diff / 2;
                mScreen.top += h_diff / 2;
            }
            redraw();
        });

        setOnMousePressed(event -> {
            mLastPressX = event.getX();
            mLastPressY = event.getY();
        });

        setOnMouseReleased(event -> {
            mLastPressX = -1;
            mLastPressY = -1;
        });

        setOnMouseDragged(event -> {
            if (mLastPressX == -1)
                return;

            mScreen.translate(fromPix(mLastPressX - event.getX()), fromPix(event.getY() - mLastPressY));

            mLastPressX = event.getX();
            mLastPressY = event.getY();

            redraw();
        });

        setOnScroll(event -> {
            double scroll = event.getDeltaY();

            if (scroll < 0)
                mScreen.scale(0.9, toCx(event.getX()), toCy(event.getY()));
            if (scroll > 0)
                mScreen.scale(1.1, toCx(event.getX()), toCy(event.getY()));

            redraw();
        });

        setOnMouseEntered(event -> {
            if (mLagrangeFunc != null) {
                mLastMoveX = toCx(event.getX());
                mLastMoveY = toCy(event.getY());
                redraw();
            }
        });

        setOnMouseMoved(event -> {
            if (mLagrangeFunc != null) {
                mLastMoveX = toCx(event.getX());
                mLastMoveY = toCy(event.getY());
                redraw();
            }
        });

        setOnMouseExited(event -> {
            mLastMoveX = mLastMoveY = Double.NaN;
            redraw();
        });
    }

    public void setLagrangeFunc(LagrangeFunc lagrangeFunc) {
        mLagrangeFunc = lagrangeFunc;

        if (lagrangeFunc != null) {
            mX = new double[N + 1];
            mY = new double[N + 1];
            mLY = new double[N + 1];

            mMinY = Integer.MAX_VALUE;
            mMaxY = Integer.MIN_VALUE;

            for (int i = 0; i <= N; i++) {
                mX[i] = mLagrangeFunc.a + mLagrangeFunc.getWidth() / N * i;
                mY[i] = mLagrangeFunc.get(mX[i]);
                mLY[i] = mLagrangeFunc.getApprox(mX[i]);

                mMinY = Math.min(mMinY, mY[i]);
                mMaxY = Math.max(mMaxY, mY[i]);
            }
            mLX = Arrays.copyOf(mX, mX.length);
        } else {
            mLX = mX = mY = mLY = null;
        }

        reset();
    }

    public void setNewtonFunc(NewtonFunc newtonFunc) {
        mNewtonFunc = newtonFunc;

        mNY = new double[N + 1];
        reset();
    }

    private void reset() {
        if (mLagrangeFunc != null) {
            double left = mLagrangeFunc.a - mLagrangeFunc.getWidth() * 0.1;
            double right = mLagrangeFunc.b + mLagrangeFunc.getWidth() * 0.1;
            double funcH = mMaxY - mMinY;
            double top = mMaxY + funcH * 0.05;
            double bottom = mMinY - funcH * 0.05;

            double w = right - left;
            double h = top - bottom;
            if (w / h > getWidth() / getHeight()) {
                double h_diff = w * getHeight() / getWidth() - h;
                top += h_diff / 2;
                bottom -= h_diff / 2;
            } else {
                double w_diff = h * getWidth() / getHeight() - w;
                left -= w_diff / 2;
                right += w_diff / 2;
            }

            mScreen = new Screen(left, top, right, bottom);
        }

        redraw();
    }

    private void redraw() {
        clear();
        if (mLagrangeFunc == null || Double.isNaN(mLagrangeFunc.a) || Double.isNaN(mLagrangeFunc.b))
            return;

        //draw coordinates
        drawCoordinates();

        //draw charts
        drawCharts();

        //draw text info
        drawTextInfo();
    }

    private void drawCoordinates() {
        GraphicsContext context = this.getGraphicsContext2D();

        context.setFill(Color.BLACK);
        context.setStroke(Color.BLACK);
        context.setLineWidth(0.5);

        //draw ox coordinates
        double y = Math.max(Math.min(toPy(0), getHeight()), 0);
        context.strokeLine(0, y, getWidth(), y);

        double precision = countPrecision(getWidth(), mScreen.width());

        context.setFont(COORD_FONT);
        context.setTextAlign(TextAlignment.CENTER);

        double textY;
        if (getHeight() - y < 30) {
            context.setTextBaseline(VPos.BOTTOM);
            textY = y - 6;
        } else {
            context.setTextBaseline(VPos.TOP);
            textY = y + 6;
        }

        for (int i = (int) (mScreen.left / precision + 0.9999) - 1; ; i++) {
            if (i == 0)
                continue;

            double x = precision * i;
            if (x > mScreen.right)
                break;

            //draw
            double px = toPx(x);
            context.strokeLine(px, y - 4, px, y + 4);

            if (precision >= MIN_PRECISION)
                context.fillText(COORD_FORMAT.format(x), px, textY);
            else
                context.fillText(COORD_FORMAT_SCIEN.format(x), px, textY);
        }

        //draw oy coordinates
        double x = Math.max(Math.min(toPx(0), getWidth()), 0);
        context.strokeLine(x, 0, x, getHeight());

        precision = countPrecision(getHeight(), mScreen.height());

        context.setTextBaseline(VPos.CENTER);

        double textX;
        if (x < 50) {
            context.setTextAlign(TextAlignment.LEFT);
            textX = x + 6;
        } else {
            context.setTextAlign(TextAlignment.RIGHT);
            textX = x - 6;
        }

        for (int i = (int) (mScreen.bottom / precision + 0.9999) - 1; ; i++) {
            if (i == 0)
                continue;

            y = precision * i;
            if (y > mScreen.top)
                break;

            //draw
            double py = toPy(y);
            context.strokeLine(x - 4, py, x + 4, py);

            if (precision >= MIN_PRECISION)
                context.fillText(COORD_FORMAT.format(y), textX, py);
            else
                context.fillText(COORD_FORMAT_SCIEN.format(y), textX, py);
        }
    }

    private double countPrecision(double pixSize, double coordSize) {
        double precision = coordSize * 100 / pixSize;
        double mn = 1;

        if (precision >= 1) {
            while (precision >= 10) {
                precision /= 10;
                mn *= 10;
            }
        } else {
            while (precision < 1) {
                precision *= 10;
                mn /= 10;
            }
        }

        if (precision >= 5)
            precision = 5;
        else if (precision >= 2.5)
            precision = 2.5;
        else if (precision >= 2)
            precision = 2;
        else precision = 1;
        precision *= mn;

        return precision;
    }

    private void drawCharts() {
        GraphicsContext context = this.getGraphicsContext2D();

        //recalculate charts
        if (mScreen.right < mLagrangeFunc.a || mScreen.left > mLagrangeFunc.b)
            return;

        double left, width, right;
        if (mLagrangeFunc.a > mScreen.left) {
            left = mLagrangeFunc.a;
            width = Math.min(mScreen.right - left, mLagrangeFunc.getWidth());
        } else {
            left = mScreen.left;
            width = Math.min(mScreen.width(), mLagrangeFunc.b - left);
        }
        right = left + width;

        for (int i = 0; i <= N; i++) {
            mX[i] = left + width / N * i;
            mY[i] = mLagrangeFunc.get(mX[i]);

            if (mNewtonFunc != null)
                mNY[i] = mNewtonFunc.getApprox(mX[i]);
        }

        //draw main chart
        context.setLineWidth(1.5);
        context.beginPath();
        context.moveTo(toPx(mX[0]), toPy(mY[0]));
        for (int i = 1; i <= N; i++)
            context.lineTo(toPx(mX[i]), toPy(mY[i]));
        context.stroke();

        //draw lagrange chart
        context.setLineWidth(1);
        context.setStroke(Color.RED);
        context.beginPath();

        //optimization
        int leftIndex = 0;
        while (mLX[leftIndex] < left)
            leftIndex++;
        leftIndex = Math.max(leftIndex - 1, 0);

        int rightIndex = mLX.length - 1;
        while (mLX[rightIndex] > right)
            rightIndex--;
        rightIndex = Math.min(rightIndex + 1, mLX.length - 1);

        context.moveTo(toPx(mLX[leftIndex]), toPy(mLY[leftIndex]));
        for (int i = leftIndex + 1; i <= rightIndex; i++)
            context.lineTo(toPx(mLX[i]), toPy(mLY[i]));
        context.stroke();

        //draw newton chart in asked
        if (mNewtonFunc == null)
            return;

        context.setLineWidth(1);
        context.setStroke(Color.GREEN);
        context.beginPath();

        context.moveTo(toPx(mX[0]), toPy(mNY[0]));
        for (int i = 1; i <= N; i++)
            context.lineTo(toPx(mX[i]), toPy(mNY[i]));
        context.stroke();
    }

    private void drawTextInfo() {
        if (Double.isNaN(mLastMoveX))
            return;

        GraphicsContext context = getGraphicsContext2D();
        context.setTextBaseline(VPos.TOP);
        context.setTextAlign(TextAlignment.LEFT);
        context.setFont(TEXT_FONT);

        if (mLastMoveX < mLagrangeFunc.a || mLastMoveX > mLagrangeFunc.b) {
            //draw background
            context.setFill(Color.WHITE);
            String xText = String.format(Locale.US, "X: %.8f", mLastMoveX);
            context.fillRect(0, 0, xText.length() * 8.5, 30);

            //draw x info
            context.setFill(Color.BLACK);
            context.fillText(xText, 10, 5);
        } else {
            //calculate info
            String xText = String.format(Locale.US, "X: %.8f", mLastMoveX);
            int yTextPos = xText.length() * 9 + 5;

            double yF = mLagrangeFunc.get(mLastMoveX);
            double yLF = mLagrangeFunc.getApprox(mLastMoveX);
            double yNF = mNewtonFunc != null ? mNewtonFunc.getApprox(mLastMoveX) : Double.NaN;
            String yTextF = String.format(Locale.US, "Y: %.8f", yF);
            String yTextLF = String.format(Locale.US, "Y: %.8f", yLF);
            String yTextNF = String.format(Locale.US, "Y: %.8f", yNF);

            //draw func dots
            context.setFill(Color.BLACK);
            context.fillOval(toPx(mLastMoveX) - 4, toPy(yF) - 4, 8, 8);
            context.setFill(Color.RED);
            context.fillOval(toPx(mLastMoveX) - 4, toPy(yLF) - 4, 8, 8);
            if (mNewtonFunc != null) {
                context.setFill(Color.GREEN);
                context.fillOval(toPx(mLastMoveX) - 4, toPy(yNF) - 4, 8, 8);
            }

            //draw background
            context.setFill(Color.WHITE);
            if (mNewtonFunc == null)
                context.fillRect(0, 0, yTextPos + Math.max(yTextF.length(), yTextLF.length()) * 8.5, 55);
            else
                context.fillRect(0, 0, yTextPos +
                                       Math.max(Math.max(yTextF.length(), yTextLF.length()), yTextNF.length()) * 8.5,
                                 75);

            //draw x info
            context.setFill(Color.BLACK);
            context.fillText(xText, 10, 5);

            //draw func info
            context.fillText(yTextF, yTextPos, 5);

            //draw lagrange func info
            context.setFill(Color.RED);
            context.fillText(yTextLF, yTextPos, 25);

            //draw newton func
            if (mNewtonFunc != null) {
                context.setFill(Color.GREEN);
                context.fillText(yTextNF, yTextPos, 45);
            }
        }
    }

    private double toPx(double x) {
        return getWidth() / mScreen.width() * (x - mScreen.left);
    }

    private double toPy(double y) {
        return getHeight() - (getHeight() / mScreen.height() * (y - mScreen.bottom));
    }

    private double toCx(double px) {
        return px * mScreen.width() / getWidth() + mScreen.left;
    }

    private double toCy(double py) {
        return (getHeight() - py) * mScreen.height() / getHeight() + mScreen.bottom;
    }

    private double fromPix(double p) {
        return p / (getWidth() / mScreen.width());
    }

    private void clear() {
        GraphicsContext context = getGraphicsContext2D();
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, getWidth(), getHeight());
    }

    private class Screen {
        double left, top, right, bottom;

        Screen(double left, double top, double right, double bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        void translate(double x, double y) {
            left += x;
            right += x;
            top += y;
            bottom += y;
        }

        void scale(double scale) {
            scale = 1 - scale;
            double width = mScreen.width();
            double height = mScreen.height();

            left -= width * scale / 2;
            right += width * scale / 2;
            top += height * scale / 2;
            bottom -= height * scale / 2;
        }

        void scale(double scale, double x, double y) {
            scale = 1 - scale;

            left -= scale * (x - left);
            right += scale * (right - x);
            top += scale * (top - y);
            bottom -= scale * (y - bottom);
        }

        double width() {
            return right - left;
        }

        double height() {
            return top - bottom;
        }

    }

}
