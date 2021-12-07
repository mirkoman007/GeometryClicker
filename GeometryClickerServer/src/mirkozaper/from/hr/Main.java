/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import mirkozaper.from.hr.model.Player;
import mirkozaper.from.hr.rmi.ChatServer;
import mirkozaper.from.hr.utils.ByteUtils;

/**
 *
 * @author mirko
 */
public class Main {

    public static void main(String[] args) throws IOException {

        int sourcePort = 1111;
        ArrayList<Player> players = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Number of players : ");
        int nPlayers = Integer.parseInt(scanner.nextLine());
        scanner.close();

        DatagramSocket socket = new DatagramSocket(sourcePort);
        System.out.println("Binded.");
        
        //chat server
        ChatServer chatServer= new ChatServer();

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        for (int i = 0; i < nPlayers; i++) {
            socket.receive(packet);
            String msg = new String(buffer, 0, packet.getLength());
            String[] split = msg.split("=");
            players.add(new Player(Integer.parseInt(split[1]), split[0]));
        }

        players.forEach(System.out::println); //kasnije maknit
        buffer = "REQUEST".getBytes();
        packet = new DatagramPacket(buffer, buffer.length);
        InetSocketAddress address = new InetSocketAddress("localhost", players.get(0).getPort());
        packet.setSocketAddress(address);
        socket.send(packet);

        
        byte[] numberOfBufferRepoBytes = new byte[4];
        packet = new DatagramPacket(numberOfBufferRepoBytes, numberOfBufferRepoBytes.length);
        socket.receive(packet);
        int length = ByteUtils.byteArrayToInt(numberOfBufferRepoBytes);

        
        byte[] bufferRepo = new byte[length];
        packet = new DatagramPacket(bufferRepo, bufferRepo.length);
        socket.receive(packet);
        
        
        for (int i = 1; i < nPlayers; i++) {
            buffer = "OTHER".getBytes();
            packet = new DatagramPacket(buffer, buffer.length);
            address = new InetSocketAddress("localhost", players.get(i).getPort());
            packet.setSocketAddress(address);
            socket.send(packet);
            
            
            packet = new DatagramPacket(numberOfBufferRepoBytes, numberOfBufferRepoBytes.length);
            packet.setSocketAddress(address);
            socket.send(packet);
            
            packet = new DatagramPacket(bufferRepo, bufferRepo.length);
            packet.setSocketAddress(address);
            socket.send(packet);
        }
        
        
        boolean gameOver=false;
        buffer = new byte[1024];
        packet = new DatagramPacket(buffer, buffer.length);
        
        while(!gameOver){
            socket.receive(packet);
            String msg = new String(buffer, 0, packet.getLength());
            
            if ("GAMEOVER".equals(msg)) {
                gameOver=true;
                System.out.println("gotovo");
                //pobjednik nickname i port
            }
            else if("REFRESHREPO".equals(msg)){
                
                
                byte[] portNumberBuffer = new byte[4];
                packet = new DatagramPacket(portNumberBuffer, portNumberBuffer.length);
                socket.receive(packet);
                int senderPort = ByteUtils.byteArrayToInt(portNumberBuffer);
                
                
                numberOfBufferRepoBytes = new byte[4];
                packet = new DatagramPacket(numberOfBufferRepoBytes, numberOfBufferRepoBytes.length);
                socket.receive(packet);
                length = ByteUtils.byteArrayToInt(numberOfBufferRepoBytes);


                bufferRepo = new byte[length];
                packet = new DatagramPacket(bufferRepo, bufferRepo.length);
                socket.receive(packet);
                
                packet = new DatagramPacket(buffer, buffer.length);
                
                for (Player p : players) 
                { 
                    if(p.getPort()!=senderPort){
                        buffer = "REFRESHREPO".getBytes();
                        packet = new DatagramPacket(buffer, buffer.length);
                        address = new InetSocketAddress("localhost", p.getPort());
                        packet.setSocketAddress(address);
                        socket.send(packet);  
                        
            
                        packet = new DatagramPacket(numberOfBufferRepoBytes, numberOfBufferRepoBytes.length);
                        packet.setSocketAddress(address);
                        socket.send(packet);

                        packet = new DatagramPacket(bufferRepo, bufferRepo.length);
                        packet.setSocketAddress(address);
                        socket.send(packet);
                    }
                }
                System.out.println(senderPort);
                
                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
        
            }
            
        }
        

        for (Player p : players) 
        { 
            buffer = "GAMEOVER".getBytes();
            packet = new DatagramPacket(buffer, buffer.length);
            address = new InetSocketAddress("localhost", p.getPort());
            packet.setSocketAddress(address);
            socket.send(packet);  
            
            //pobjednik podaci
        }

        
        socket.close();

        System.out.println("Closed.");
        
        System.exit(0);
    }
}
