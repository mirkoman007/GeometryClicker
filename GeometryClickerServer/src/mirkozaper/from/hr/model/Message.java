/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.model;

/**
 *
 * @author mirko
 */
public class Message {

    public Message(String name, String message) {
        this.name = name;
        this.message = message;
    }
    
    String name;
    String message;

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String username) {
        this.name = username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "(" + name + ") " + message;
    }
    
    
}
