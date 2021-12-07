/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.file;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mirko
 */
public class ThreadsDemo {
    
    public static boolean isReserved=false;
    public static String path = "WordsFile.txt";
    public static String[] words = {"headquarters", "lip", "leaf", "twist", "get", "apology", "grief", "hit",
        "amuse","yearn","mosaic","assembly","strain","referee","bloody","sweet","crew","incongruous"};

    
    public ThreadsDemo() {
        deleteFile();
        
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {   
            executorService.execute(new FileWriterDemo());    
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadsDemo.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(!executorService.isTerminated()){
                executorService.shutdownNow();
            }
            System.err.println("The threads job is done !!!");
        }
    }
    
    
    private void deleteFile() {
        File myFile = new File(path);

        if (myFile.exists()) {
            myFile.delete();
        }
    }

}
