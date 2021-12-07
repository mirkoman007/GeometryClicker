/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mirkozaper.from.hr.reflection.Documentation;

/**
 *
 * @author mirko
 */
public class Main extends Application {

    

    @Override
    public void start(Stage primaryStage) throws IOException {

        Pane root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));

        Scene scene = new Scene(root, 1280, 720);

        primaryStage.setTitle("Geometry clicker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Documentation.Generate();

        launch(args);

    }

}
