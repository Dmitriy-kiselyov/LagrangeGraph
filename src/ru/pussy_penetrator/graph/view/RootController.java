package ru.pussy_penetrator.graph.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import ru.pussy_penetrator.graph.MainApp;
import ru.pussy_penetrator.graph.model.*;

/**
 * Created by Pussy_penetrator on 20.09.2016.
 */
public class RootController {

    @FXML
    GridPane mParent;

    @FXML
    GraphCanvas mCanvas;

    @FXML
    Pane mCanvasPane;

    @FXML
    ComboBox<Func> mFuncComboBox;

    @FXML
    TextField mFromXField;

    @FXML
    TextField mToXField;

    @FXML
    Label mNodeLabel;

    @FXML
    Slider mNodeSlider;

    @FXML
    ComboBox<InterpolateFunc> mInterpolateComboBox;

    @FXML
    CheckBox mNewtonCheck;

    @FXML
    Button mApplyButton;

    private MainApp mMainApp;

    private Graphic mGraphic;

    @FXML
    private void initialize() {
        mCanvas.widthProperty().bind(mCanvasPane.widthProperty());
        mCanvas.heightProperty().bind(mCanvasPane.heightProperty());
    }

    public void setMainApp(MainApp mainApp) {
        mMainApp = mainApp;

        mGraphic = mMainApp.getGraphic();
        mFromXField.setText("" + mGraphic.getFromX());
        mToXField.setText("" + mGraphic.getToX());
        disableApplyButtonIfNeeded();
        setupListeners();

        mFuncComboBox.setItems(mMainApp.getFuncList());
        mInterpolateComboBox.setItems(mMainApp.getInterFuncList());
    }

    private void setupListeners() {
        mFromXField.textProperty().addListener((observable, oldValue, newValue) -> checkFromXField(newValue, true));
        mToXField.textProperty().addListener((observable, oldValue, newValue) -> checkToXField(newValue, true));

        mGraphic.nodeCountProperty().bindBidirectional(mNodeSlider.valueProperty());
        mNodeLabel.textProperty().bind(mGraphic.nodeCountProperty().asString());
        mNewtonCheck.selectedProperty().bindBidirectional(mGraphic.showNewtonProperty());

        mFuncComboBox.getSelectionModel().selectedIndexProperty()
                     .addListener((observable, oldValue, newValue) -> disableApplyButtonIfNeeded());
        mInterpolateComboBox.getSelectionModel().selectedIndexProperty()
                            .addListener((observable, oldValue, newValue) -> disableApplyButtonIfNeeded());
    }

    private void disableApplyButtonIfNeeded() {
        if (mFuncComboBox.getSelectionModel().isEmpty() || mInterpolateComboBox.getSelectionModel().isEmpty())
            mApplyButton.setDisable(true);
        else
            mApplyButton.setDisable(false);
    }

    private void checkFromXField(String newValue, boolean checkOther) {
        try {
            double val = Double.parseDouble(newValue);
            mGraphic.setFromX(val);
            mFromXField.setStyle("");

            if (checkOther)
                checkToXField(mToXField.getText(), false);
        }
        catch (NumberFormatException e) {
            mFromXField.setStyle("-fx-background-color: #FF9999;");
        }
        catch (IllegalArgumentException e) {
            mFromXField.setStyle("-fx-background-color: #FF9999;");
            mToXField.setStyle("-fx-background-color: #FF9999;");
        }
    }

    private void checkToXField(String newValue, boolean checkOther) {
        try {
            double val = Double.parseDouble(newValue);
            mGraphic.setToX(val);
            mToXField.setStyle("");

            if (checkOther)
                checkFromXField(mFromXField.getText(), false);
        }
        catch (NumberFormatException e) {
            mToXField.setStyle("-fx-background-color: #FF9999;");
        }
        catch (IllegalArgumentException e) {
            mFromXField.setStyle("-fx-background-color: #FF9999;");
            mToXField.setStyle("-fx-background-color: #FF9999;");
        }
    }

    @FXML
    private void selectFunc() {
        mGraphic.setFunction(mFuncComboBox.getValue());
    }

    @FXML
    private void selectInterpolateFunc() {
        mGraphic.setInterpolateFunc(mInterpolateComboBox.getValue());
    }

    @FXML
    private void handleApply() {
        mGraphic.apply();
        LagrangeFunc func = mGraphic.getLagrangeFunc();

        //easter egg
        if (mNodeSlider.valueProperty().intValue() == 228 && mCanvasPane.getChildren().indexOf(mCanvas) != -1) {
            boolean horseStolen = mMainApp.showAiderka();
            if (horseStolen) {
                mCanvasPane.getChildren().clear();
                mCanvasPane.getStyleClass().add("canvas_easter");
            }
        }

        if (func != null) {
            mFromXField.setText("" + mGraphic.getFromX());
            mToXField.setText("" + mGraphic.getToX());
            mNodeSlider.setValue(mGraphic.getNodeCount());

            //another one
            if (mFromXField.getText().trim().equals("NaN") || mToXField.getText().trim().equals("NaN"))
                mParent.getStyleClass().add("parent_easter");
        }

        mCanvas.setLagrangeFunc(func);
        mCanvas.setNewtonFunc(mGraphic.getNewtonFunc());
    }

}
