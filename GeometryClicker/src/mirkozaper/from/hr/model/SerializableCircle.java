/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.model;

import java.io.Serializable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author mirko
 */
public class SerializableCircle implements Serializable {

    private final int color;
    private final double radius;
    private final double translateX;
    private final double translateY;

    public Color getColor() {
        return Color.rgb(
                color & 0xFF,
                (color >>> 010) & 0xFF,
                (color >>> 020) & 0xFF,
                (color >>> 030) / 255d);
    }

    public double getRadius() {
        return radius;
    }

    public double getTranslateX() {
        return translateX;
    }

    public double getTranslateY() {
        return translateY;
    }

    public SerializableCircle(Circle circ) {
        this.radius = circ.getRadius();
        this.translateX=circ.getTranslateX();
        this.translateY=circ.getTranslateY();
        Color color = (Color) circ.getFill();
        this.color = (int) (color.getRed() * 0xFF)
                | ((int) (color.getGreen() * 0xFF)) << 010
                | ((int) (color.getBlue() * 0xFF)) << 020
                | ((int) (color.getOpacity() * 0xFF)) << 030;
    }

    private Object readResolve() {
        Circle circ = new Circle(radius);
        circ.setTranslateX(translateX);
        circ.setTranslateY(translateY);
        circ.setFill(Color.rgb(
                color & 0xFF,
                (color >>> 010) & 0xFF,
                (color >>> 020) & 0xFF,
                (color >>> 030) / 255d));
        return circ;
    }

}
