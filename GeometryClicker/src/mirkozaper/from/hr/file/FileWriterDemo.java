/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static mirkozaper.from.hr.file.ThreadsDemo.path;
import static mirkozaper.from.hr.file.ThreadsDemo.isReserved;
import static mirkozaper.from.hr.file.ThreadsDemo.words;

/**
 *
 * @author mirko
 */
public class FileWriterDemo implements Runnable {


    @Override
    public void run() {
        

        for (String fruit : words) {

            Random random = new Random();

            
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException ex) {
                Logger.getLogger(FileWriterDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            long threadId = Thread.currentThread().getId();

            write(fruit + " _> " + String.valueOf(threadId));
        }

    }

    private synchronized void write(String word) {
        while (isReserved == true) {
            try {
                System.err.println("Thread with id "+String.valueOf(Thread.currentThread().getId())+" goes on wait.");
                wait(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(FileWriterDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        isReserved = true;

        try {
            FileWriter writer = new FileWriter(path, true);
            writer.write(word);
            System.out.println(word);
            writer.write("\n");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileWriterDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

        isReserved = false;
        notifyAll();
    }

}
