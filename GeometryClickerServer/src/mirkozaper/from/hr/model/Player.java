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
public class Player {
    int port;
    String username;

    public Player(int port, String username) {
        this.port = port;
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username+"["+port+"]";
    }
    
    
        
    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
}
