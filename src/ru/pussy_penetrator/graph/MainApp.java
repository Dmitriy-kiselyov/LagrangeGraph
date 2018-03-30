package ru.pussy_penetrator.graph;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pussy_penetrator.graph.model.Func;
import ru.pussy_penetrator.graph.model.Graphic;
import ru.pussy_penetrator.graph.model.InterpolateFunc;
import ru.pussy_penetrator.graph.view.AiderkaController;
import ru.pussy_penetrator.graph.view.RootController;

import java.io.IOException;

public class MainApp extends Application {

    private Stage mStage;

    private Graphic mGraphic;
    private ObservableList<Func>            mFuncList      = FXCollections.observableArrayList();
    private ObservableList<InterpolateFunc> mInterFuncList = FXCollections.observableArrayList();

    public MainApp() {
        mGraphic = new Graphic();

        mFuncList.add(new Func("y = |x|") {
            @Override
            public double get(double x) {
                return Math.abs(x);
            }
        });
        mFuncList.add(new Func("y = x^3") {
            @Override
            public double get(double x) {
                return x * x * x;
            }
        });
        mFuncList.add(new Func("y = cos(πx/2)") {
            @Override
            public double get(double x) {
                return Math.cos(Math.PI * x / 2);
            }
        });
        mFuncList.add(new Func("y = 1 / (1 + 15 * x^2)") {
            @Override
            public double get(double x) {
                return 1 / (1 + 15 * x * x);
            }
        });
        mFuncList.add(new Func("y = cos(x)") {
            @Override
            public double get(double x) {
                return Math.cos(x);
            }
        });
        mFuncList.add(new Func("y = x^2 - 10") {
            @Override
            public double get(double x) {
                return x * x - 10;
            }
        });
        mFuncList.add(new Func("y = cos(sin(x) + x^2)") {
            @Override
            public double get(double x) {
                return Math.cos(Math.sin(x) + x * x);
            }
        });
        mFuncList.add(new Func("y = sin(x) + cos(x) / 10 + sqrt(|x|)") {
            @Override
            public double get(double x) {
                return Math.sin(x) + Math.cos(x) / 10 + Math.sqrt(Math.abs(x));
            }
        });

        mInterFuncList.add(new InterpolateFunc("Равномерное разбиение") {
            @Override
            public double get(double a, double b, int n, int i) {
                return a + i * (b - a) / n;
            }
        });
        mInterFuncList.add(new InterpolateFunc("Многочлен Чебышева 1-го рода") {
            @Override
            public double get(double a, double b, int n, int i) {
                return (b - a) / 2 * Math.cos(Math.PI * (2 * i + 1) / (2 * n + 2)) + (b + a) / 2;
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mStage = primaryStage;

        primaryStage.setTitle("Lagrange Graph");

        initRootLayout();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("view/Root.fxml"));
            Parent root = loader.load();

            RootController controller = loader.getController();
            controller.setMainApp(this);

            mStage.setScene(new Scene(root));
            mStage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean showAiderka() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Aiderka.fxml"));
            BorderPane root = loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Помоги Айдерке украсть коня");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mStage);
            dialogStage.setResizable(false);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // контроллер
            AiderkaController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            return controller.isHorseStolen();
        }
        catch (IOException e) {
            //do nothing
        }

        return false;
    }

    public Graphic getGraphic() {
        return mGraphic;
    }

    public ObservableList<Func> getFuncList() {
        return mFuncList;
    }

    public ObservableList<InterpolateFunc> getInterFuncList() {
        return mInterFuncList;
    }

}
