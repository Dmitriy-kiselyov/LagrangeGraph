package ru.pussy_penetrator.graph.view;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Created by Pussy_penetrator on 23.09.2016.
 */
public class AiderkaController {

    private Stage   mStage;
    private boolean mHorseStolen;

    public void setDialogStage(Stage stage) {
        mStage = stage;
    }

    @FXML
    private void handleSteelHorse() {
        mHorseStolen = true;
        mStage.close();
    }

    public boolean isHorseStolen() {
        return mHorseStolen;
    }
}
