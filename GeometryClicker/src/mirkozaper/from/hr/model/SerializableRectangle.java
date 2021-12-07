/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.model;

import java.io.Serializable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author mirko
 */
public class SerializableRectangle implements Serializable {

    private final int color;
    private final double width;
    private final double height;
    private final double translateX;
    private final double translateY;

    public Color getColor() {
        return Color.rgb(
                color & 0xFF,
                (color >>> 010) & 0xFF,
                (color >>> 020) & 0xFF,
                (color >>> 030) / 255d);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getTranslateX() {
        return translateX;
    }

    public double getTranslateY() {
        return translateY;
    }
    
    public SerializableRectangle(Rectangle rect) {
        this.width = rect.getWidth();
        this.height = rect.getHeight();
        this.translateX=rect.getTranslateX();
        this.translateY=rect.getTranslateY();
        Color color = (Color) rect.getFill();
        this.color = (int) (color.getRed() * 0xFF)
                | ((int) (color.getGreen() * 0xFF)) << 010
                | ((int) (color.getBlue() * 0xFF)) << 020
                | ((int) (color.getOpacity() * 0xFF)) << 030;
    }

    private Object readResolve() {
        Rectangle rect = new Rectangle(width, height);
        rect.setTranslateX(translateX);
        rect.setTranslateY(translateY);
        rect.setFill(Color.rgb(
                color & 0xFF,
                (color >>> 010) & 0xFF,
                (color >>> 020) & 0xFF,
                (color >>> 030) / 255d));
        return rect;
    }

}
