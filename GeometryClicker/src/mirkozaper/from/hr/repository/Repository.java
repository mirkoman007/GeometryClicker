/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import mirkozaper.from.hr.model.SerializableCircle;
import mirkozaper.from.hr.model.SerializableRectangle;

/**
 *
 * @author mirko
 */
public class Repository implements Serializable {

    private static final long serialVersionUID = 1L;

    private Repository() {
    }

    private static final Repository INSTANCE = new Repository();

    public static Repository getINSTANCE() {
        return INSTANCE;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    private int score;
    private final ObservableList<Rectangle> rectangles = FXCollections.observableArrayList();
    private final ObservableList<Circle> circles = FXCollections.observableArrayList();
    private final ObservableList<Rectangle> rectangleTraps = FXCollections.observableArrayList();
    private final ObservableList<Circle> circleTraps = FXCollections.observableArrayList();

    public ObservableList<Rectangle> getRectangles() {
        return rectangles;
    }

    public ObservableList<Circle> getCircles() {
        return circles;
    }

    public ObservableList<Rectangle> getRectangleTraps() {
        return rectangleTraps;
    }

    public ObservableList<Circle> getCircleTraps() {
        return circleTraps;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        
        ArrayList<SerializableRectangle> serializedRectangles=new ArrayList<>();
        ArrayList<SerializableCircle> serializedCircles=new ArrayList<>();
        ArrayList<SerializableRectangle> serializedRectangleTraps=new ArrayList<>();
        ArrayList<SerializableCircle> serializedCircleTraps=new ArrayList<>();
        
        
        rectangles.forEach((r) -> {
            serializedRectangles.add(new SerializableRectangle(r));
        });
        circles.forEach((c) -> {
            serializedCircles.add(new SerializableCircle(c));
        });
        rectangleTraps.forEach((r) -> {
            serializedRectangleTraps.add(new SerializableRectangle(r));
        });
        circleTraps.forEach((c) -> {
            serializedCircleTraps.add(new SerializableCircle(c));
        });
        
        oos.writeInt(score);
        oos.writeObject(new ArrayList<>(serializedRectangles));
        oos.writeObject(new ArrayList<>(serializedCircles));
        oos.writeObject(new ArrayList<>(serializedRectangleTraps));
        oos.writeObject(new ArrayList<>(serializedCircleTraps));
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        INSTANCE.score=ois.readInt();
        List<Rectangle> serializedRectangles = (List<Rectangle>) ois.readObject();
        List<Circle> serializedCircles = (List<Circle>) ois.readObject();
        List<Rectangle> serializedRectangleTraps = (List<Rectangle>) ois.readObject();
        List<Circle> serializedCircleTraps = (List<Circle>) ois.readObject();

        INSTANCE.rectangles.clear();
        INSTANCE.circles.clear();
        INSTANCE.rectangleTraps.clear();
        INSTANCE.circleTraps.clear();


        INSTANCE.rectangles.addAll(serializedRectangles);
        INSTANCE.circles.addAll(serializedCircles);
        INSTANCE.rectangleTraps.addAll(serializedRectangleTraps);
        INSTANCE.circleTraps.addAll(serializedCircleTraps);
    }

    private Object readResolve() {
        return INSTANCE;
    }

}
