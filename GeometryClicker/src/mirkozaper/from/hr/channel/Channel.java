/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.channel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import javafx.application.Platform;
import mirkozaper.from.hr.controller.MainController;
import mirkozaper.from.hr.repository.Repository;
import mirkozaper.from.hr.utils.ByteUtils;

/**
 *
 * @author mirko
 */
public class Channel implements Runnable {

    private final MainController controller;

    public Channel(MainController controller) {
        this.controller = controller;
    }

    private DatagramSocket socket;
    private boolean running;
    private int port;

    public void bind(int port) throws SocketException {
        this.port=port;
        socket = new DatagramSocket(port);
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
        socket.close();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        running = true;
        while (running) {
            try {
                socket.receive(packet);

                String msg = new String(buffer, 0, packet.getLength());
                System.out.println(msg);

                if ("REQUEST".equals(msg)) {
                    Platform.runLater(() -> {
                        controller.startMpAsFirstPlayer();
                    });
                    
                }
                else if ("OTHER".equals(msg)) {
                    
                    
                    byte[] numberOfBufferBytes = new byte[4];
                    packet = new DatagramPacket(numberOfBufferBytes, numberOfBufferBytes.length);
                    socket.receive(packet);
                    int length = ByteUtils.byteArrayToInt(numberOfBufferBytes);
                    

                    byte[] bufferRepo = new byte[length];
                    packet = new DatagramPacket(bufferRepo, bufferRepo.length);
                    socket.receive(packet);
                    
                    
                    Platform.runLater(() -> {
                        controller.startMpAsOtherPlayer(bufferRepo);
                    });
                    
                    System.out.println(length);
                    
                    packet = new DatagramPacket(buffer, buffer.length);
                    
                }
                else if ("GAMEOVER".equals(msg)) {                    
                    Platform.runLater(() -> {
                        controller.FinishTheGame();
                    });

                    //primanje pobjednik informacija
                    //ispis pobjednika
                    stop();
                   System.out.println("kraj igre");
                }
                else if("REFRESHREPO".equals(msg)){
                    
                   
                    byte[] numberOfBufferBytes = new byte[4];
                    packet = new DatagramPacket(numberOfBufferBytes, numberOfBufferBytes.length);
                    socket.receive(packet);
                    int length = ByteUtils.byteArrayToInt(numberOfBufferBytes);
                    

                    byte[] bufferRepo = new byte[length];
                    packet = new DatagramPacket(bufferRepo, bufferRepo.length);
                    socket.receive(packet);
                    
                    
                    Platform.runLater(() -> {
                        controller.refreshRepo(bufferRepo);
                    });
                    
                    System.out.println(length);
                    
                    packet = new DatagramPacket(buffer, buffer.length);
                }
                

            } catch (IOException e) {
                break;
            }
        }
    }

    
    
    public void sendRepoTo(InetSocketAddress address, Repository instance) throws IOException {
        byte[] buffer;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(instance);
            oos.flush();
            buffer = baos.toByteArray();
        }
        
        byte[] numberOfBufferBytes=ByteUtils.intToByteArray(buffer.length);
        
        DatagramPacket packet = new DatagramPacket(numberOfBufferBytes, numberOfBufferBytes.length);
        packet.setSocketAddress(address);
        socket.send(packet);
        
        packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(address);
        socket.send(packet);
    }
    
    
    public void sendMessageTo(InetSocketAddress address, String msg) throws IOException {
        byte[] buffer = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(address);

        socket.send(packet);
    }

    public void sendRepoToWithPort(InetSocketAddress address, Repository instance) throws IOException {
        
        //slanje porta
        byte[] portNumberBuffer=ByteUtils.intToByteArray(port);
        
        DatagramPacket packet = new DatagramPacket(portNumberBuffer, portNumberBuffer.length);
        packet.setSocketAddress(address);
        socket.send(packet);
        
        sendRepoTo(address,instance);
    }





}
