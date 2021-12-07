/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import mirkozaper.from.hr.channel.Channel;
import mirkozaper.from.hr.file.ThreadsDemo;
import mirkozaper.from.hr.model.LevelModel;
import mirkozaper.from.hr.repository.Repository;
import mirkozaper.from.hr.rmi.ChatClient;
import mirkozaper.from.hr.utils.SerializationUtils;
import mirkozaper.from.hr.xml.XMLReader;

/**
 * FXML Controller class
 *
 * @author mirko
 */
public class MainController implements Initializable {

    private static final String FILE_NAME = "repository.ser";
    private boolean mpGame;

    private String nickname="";
    
    private ChatClient chatClient;
    
    @FXML
    private AnchorPane gameScreen;

    @FXML
    private ListView<String> lvMessages;

    @FXML
    private TextField tfMessage;

    @FXML
    private Label lblScore;

    @FXML
    private Button btnStart;

    private ObservableList<String> messages;
    private int score;

    private static final int SM_SIZE = 40;
    private static final int MD_SIZE = 60;
    private static final int BG_SIZE = 80;
    private static final int SM_SCORE = 40;
    private static final int MD_SCORE = 30;
    private static final int BG_SCORE = 20;
    private static final int MAX_WIDTH = 923;
    private static final int MAX_HEIGHT = 680;
    
    //UDP
    Channel channel;
    InetSocketAddress address;
    
    @FXML
    private Button btnSend;
    @FXML
    private Button btnMp;
    @FXML
    private Label lblWaiting;
    @FXML
    private Label lblTime;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        new ThreadsDemo();

        
        lblTime.setText("Game started 00:00");
        final Timeline timeline=new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                seconds++;
                if(seconds>59){
                    minutes++;
                    seconds=0;
                }
                String minutesString=String.valueOf(minutes);
                String secondsString=String.valueOf(seconds);
                if(minutesString.length()==1){
                    minutesString="0"+minutesString;
                }
                if(secondsString.length()==1){
                    secondsString="0"+secondsString;
                }
                
                lblTime.setText("Game started "+minutesString+":"+secondsString);
            }
        }));
        
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
        initObservable();
        if (new File(FILE_NAME).exists()) {
            btnStart.setText("Resume game");
        } else {
            btnStart.setText("New game");
        }

    }

    @FXML
    private void sendMessage() {
        if (!tfMessage.getText().trim().isEmpty()) {
            
            String message=nickname+": "+tfMessage.getText();
            
            if(mpGame){
                chatClient.sendMessage(message);   
            }
            messages.add(message);
        }
        tfMessage.clear();
    }
    
    private int seconds=0;
    private int minutes=0;
    

    @FXML
    private void startGame() {
        
        
        mpGame=false;
        if (new File(FILE_NAME).exists()) {
            try {
                SerializationUtils.read(FILE_NAME);

                loadScreen();

            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            score = 0;
            LevelModel GetLevel = XMLReader.GetLevelFromXML();
            
            GenerateShapes(GetLevel.getSmallSquareShape(), GetLevel.getMediumSquareShape(), GetLevel.getBigSquareShape(), 
                    GetLevel.getSmallCircleShape(), GetLevel.getMediumCircleShape(), GetLevel.getBigCircleShape());
            GenerateTraps(GetLevel.getSmallSquareTrap(), GetLevel.getMediumSquareTrap(), GetLevel.getBigSquareTrap(), 
                    GetLevel.getSmallCircleTrap(), GetLevel.getMediumCircleTrap(), GetLevel.getBigCircleTrap());
            Serialize();
        }
        lblScore.setText("Score: " + String.valueOf(score));
        btnStart.setVisible(false);
    }

    private void initObservable() {
        messages = FXCollections.observableArrayList();
        lvMessages.setItems(messages);
    }

    private void GenerateShapes(int smallSquare, int mediumSquare, int bigSquare, int smallCircle, int mediumCircle, int bigCircle) {

        GenerateSquare(smallSquare, SM_SIZE, SM_SCORE, Color.YELLOW, false);
        GenerateSquare(mediumSquare, MD_SIZE, MD_SCORE, Color.GREEN, false);
        GenerateSquare(bigSquare, BG_SIZE, BG_SCORE, Color.BLUEVIOLET, false);

        GenerateCircle(smallCircle, SM_SIZE, SM_SCORE, Color.AQUA, false);
        GenerateCircle(mediumCircle, MD_SIZE, MD_SCORE, Color.LAWNGREEN, false);
        GenerateCircle(bigCircle, BG_SIZE, BG_SCORE, Color.ORANGE, false);
    }

    private void GenerateTraps(int smallSquare, int mediumSquare, int bigSquare, int smallCircle, int mediumCircle, int bigCircle) {

        GenerateSquare(smallSquare, SM_SIZE, SM_SCORE, Color.RED, true);
        GenerateSquare(mediumSquare, MD_SIZE, MD_SCORE, Color.RED, true);
        GenerateSquare(bigSquare, BG_SIZE, BG_SCORE, Color.RED, true);

        GenerateCircle(smallCircle, SM_SIZE, SM_SCORE, Color.RED, true);
        GenerateCircle(mediumCircle, MD_SIZE, MD_SCORE, Color.RED, true);
        GenerateCircle(bigCircle, BG_SIZE, BG_SCORE, Color.RED, true);

    }

    private void GenerateSquare(int amount, int size, int value, Color color, boolean trap) {
        Random rand = new Random();

        for (int i = 0; i < amount; i++) {
            Rectangle r = new Rectangle(size, size);
            r.setTranslateX(rand.nextInt(MAX_WIDTH - size));
            r.setTranslateY(rand.nextInt(MAX_HEIGHT - size));
            r.setFill(color);

            r.setOnMouseClicked((MouseEvent event) -> {
                if (trap) {
                    score -= value;
                    Repository.getINSTANCE().getRectangleTraps().remove(r);
                } else {
                    score += value;
                    Repository.getINSTANCE().getRectangles().remove(r);
                }
                lblScore.setText("Score: " + String.valueOf(score));
                gameScreen.getChildren().remove(r);
                Serialize();
                IsGameFinished();
                if(mpGame){
                    movePlayed();
                }

            });

            gameScreen.getChildren().add(r);
            if (trap) {
                Repository.getINSTANCE().getRectangleTraps().add(r);
            } else {
                Repository.getINSTANCE().getRectangles().add(r);
            }

        }
    }

    private void GenerateCircle(int amount, int size, int value, Color color, boolean trap) {
        Random rand = new Random();

        for (int i = 0; i < amount; i++) {
            Circle c = new Circle(size * 0.6);
            c.setTranslateX(rand.nextInt(MAX_WIDTH - size * 2) + size);
            c.setTranslateY(rand.nextInt(MAX_HEIGHT - size * 2) + size);
            c.setFill(color);

            c.setOnMouseClicked((MouseEvent event) -> {
                if (trap) {
                    score -= value;
                    Repository.getINSTANCE().getCircleTraps().remove(c);
                } else {
                    score += value;
                    Repository.getINSTANCE().getCircles().remove(c);
                }
                lblScore.setText("Score: " + String.valueOf(score));

                gameScreen.getChildren().remove(c);
                Serialize();
                IsGameFinished();
                if(mpGame){
                    movePlayed();
                }
            });

            gameScreen.getChildren().add(c);
            if (trap) {
                Repository.getINSTANCE().getCircleTraps().add(c);
            } else {
                Repository.getINSTANCE().getCircles().add(c);
            }
        }
    }

    private void IsGameFinished() {
        if (Repository.getINSTANCE().getRectangles().isEmpty() && Repository.getINSTANCE().getCircles().isEmpty()) {

            System.out.println("gotovo");
            
            if(!mpGame){
                FinishTheGame();
            }
            if(mpGame){
                String msg = "GAMEOVER";
                try {
                    channel.sendMessageTo(address, msg);
                    System.out.println("salji da je gotovo");
                    //slanje svog nicka i port

                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            
        }
    }

    public void FinishTheGame() {
        
        File f = new File(FILE_NAME);
        f.delete();
        clearScreen();
        btnMp.setDisable(false);
        btnStart.setText("New game");
        btnStart.setVisible(true);            
        Repository.getINSTANCE().getRectangleTraps().forEach(t -> gameScreen.getChildren().remove(t));
        Repository.getINSTANCE().getRectangleTraps().clear();
        Repository.getINSTANCE().getCircleTraps().forEach(t -> gameScreen.getChildren().remove(t));
        Repository.getINSTANCE().getCircleTraps().clear();
        if(mpGame){
            mpGame=false;
        }
        
    }

    private void Serialize() {
        try {
            Repository.getINSTANCE().setScore(score);
            File f = new File(FILE_NAME);
            f.delete();
            SerializationUtils.write(Repository.getINSTANCE(), FILE_NAME);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void playMp(ActionEvent event) {
        
        
// Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Server connection dialog");
        dialog.setHeaderText("Please enter player information");

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and port labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        TextField port = new TextField();
        port.setPromptText("Port");

        port.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    port.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }

        });

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Your port:"), 0, 1);
        grid.add(port, 1, 1);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), port.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePort -> {
//            System.out.println("Username=" + usernamePort.getKey() + ", Port=" + usernamePort.getValue());

            nickname=usernamePort.getKey();
            try {
                int sourcePort = Integer.parseInt(usernamePort.getValue());
                channel = new Channel(this);
                channel.bind(sourcePort);
                channel.start();

                System.out.println("Binded.");

                address = new InetSocketAddress("localhost", 1111);
                String msg = usernamePort.getKey() + "=" + usernamePort.getValue();

                channel.sendMessageTo(address, msg);

                btnMp.setDisable(true);
                lblWaiting.setVisible(true);
                btnStart.setVisible(false);

//		channel.stop();
//		System.out.println("Closed.");
            } catch (SocketException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public void startMpAsFirstPlayer() {
        mpGame=true;
        
        chatClient = new ChatClient(this);
        score = 0;
        GenerateShapes(1, 1, 1, 1, 1, 1);
        GenerateTraps(0, 2, 0, 1, 0, 0);
        Serialize();
        lblScore.setText("Score: " + String.valueOf(score));
        lblWaiting.setVisible(false);
        
        try {
            channel.sendRepoTo(address,Repository.getINSTANCE());

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }


    public void startMpAsOtherPlayer(byte[] buffer) {
        
        //----- buffer to repo
        mpGame=true;
        
        chatClient = new ChatClient(this);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais)){
            
            Repository rasda =(Repository)ois.readObject();
            
                loadScreen();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        score = 0;
        Serialize();
        lblScore.setText("Score: " + String.valueOf(score));
        lblWaiting.setVisible(false);
    }
    public void refreshRepo(byte[] buffer) {
        
        clearScreen();
        //----- buffer to repo
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais)){
            Repository rasda =(Repository)ois.readObject();
            
                loadScreen();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Serialize();
    }

    private void clearScreen() {
        
        
        Repository.getINSTANCE().getRectangles().forEach(r -> {
            gameScreen.getChildren().remove(r);
        });
        Repository.getINSTANCE().getCircles().forEach(c -> {
            gameScreen.getChildren().remove(c);
        });
        
        Repository.getINSTANCE().getRectangleTraps().forEach(r -> {
            gameScreen.getChildren().remove(r);
        });
        Repository.getINSTANCE().getCircleTraps().forEach(c -> {
            gameScreen.getChildren().remove(c);
        });
    }

    private void loadScreen() {
        
        
        Repository.getINSTANCE().getRectangles().forEach(r -> {
            r.setOnMouseClicked((MouseEvent event) -> {
                score += (60 - (r.getHeight() / 2));
                lblScore.setText("Score: " + String.valueOf(score));
                Repository.getINSTANCE().getRectangles().remove(r);
                gameScreen.getChildren().remove(r);
                Serialize();
                IsGameFinished();
                if(mpGame){
                    movePlayed();
                }
            });
            gameScreen.getChildren().add(r);
        });
        Repository.getINSTANCE().getCircles().forEach(c -> {
            c.setOnMouseClicked((MouseEvent event) -> {
                score += (60 - (c.getRadius() / 0.6 / 2));
                lblScore.setText("Score: " + String.valueOf(score));
                Repository.getINSTANCE().getCircles().remove(c);
                gameScreen.getChildren().remove(c);
                Serialize();
                IsGameFinished();
                if(mpGame){
                    movePlayed();
                }
            });
            gameScreen.getChildren().add(c);
        });
        
        Repository.getINSTANCE().getRectangleTraps().forEach(r -> {
            r.setOnMouseClicked((MouseEvent event) -> {
                score -= (60 - (r.getHeight() / 2));
                lblScore.setText("Score: " + String.valueOf(score));
                Repository.getINSTANCE().getRectangleTraps().remove(r);
                gameScreen.getChildren().remove(r);
                Serialize();
                IsGameFinished();
                if(mpGame){
                    movePlayed();
                }
            });
            gameScreen.getChildren().add(r);
        });
        Repository.getINSTANCE().getCircleTraps().forEach(c -> {
            c.setOnMouseClicked((MouseEvent event) -> {
                score -= (60 - (c.getRadius() / 0.6 / 2));
                lblScore.setText("Score: " + String.valueOf(score));
                Repository.getINSTANCE().getCircleTraps().remove(c);
                gameScreen.getChildren().remove(c);
                Serialize();
                IsGameFinished();
                if(mpGame){
                    movePlayed();
                }
            });
            gameScreen.getChildren().add(c);
        });
    }

    private void movePlayed() {
        
        String msg = "REFRESHREPO";
        try {
            channel.sendMessageTo(address, msg);
            channel.sendRepoToWithPort(address,Repository.getINSTANCE());

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void postMessage(String name, String message) {
        Platform.runLater(() -> addMessage(name,message));
    }

    private void addMessage(String name, String message) {
        messages.add("("+name+") "+message);
    }


}
