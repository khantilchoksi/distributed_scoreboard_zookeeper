package com.khantil.distributed_scoreboard;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    private static final Logger LOGGER = Logger.getLogger( ZooKeeperConnect.class.getName() );
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		
        Scanner scanner = new Scanner(System.in);
        ScoreBoard scoreBoard = null;
        while (true) {
            String input = scanner.nextLine();
            //System.out.println("INPUT : "+input);
            String[] commands = input.split(" ");
            //System.out.println("Commands Length: "+commands.length);
            if (commands.length < 3) {
                showArgumentsError();
			}
			
            String connectionString = commands[1];
            if (commands[0].toLowerCase().equals("watcher")) {
                deleteParentNodes();
                int listSize = 0;
                try {
                    listSize = Integer.parseInt(commands[2]);
                    if(listSize < 0){
                        showError("List size error");
                        showArgumentsError();
                    }
                    else
                        scoreBoard = new ScoreBoard(connectionString, listSize);                  
                    //ZooKeeperConnect.initializeScoreBoardWatcher();
                    //ZooKeeperConnect.connectZooKeeperServer(serverUrl, listSize);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    showError("Pass maximum number of list as third argument.\nExample: watcher <ip:port> <number>");
                }


            } else if (commands[0].toLowerCase().equals("player")) {
                if (commands.length == 3) {
                    // player localhost:2181 Thor
                    String playerName = commands[2];
                    if (playerExists(playerName, connectionString)) {
                        showError("Player already exists!");
                    } else {

                        Player newPlayer = new Player(commands[2], connectionString);
                        newPlayer.joinScoreboard();

                        System.out.println("\n New player has been successfaully added to our game: " + playerName);
                    }
                }else if (commands.length == 6) {
                    // player 127.0.0.1:2181 Thor 15 10 140
                    //player name count delay score
                    if(Integer.parseInt(commands[3]) < 0 || Integer.parseInt(commands[4]) < 0
                     || Integer.parseInt(commands[5]) < 0){
                        showArgumentsError();
                    }else{
                        startPostingScoreBoard(connectionString,commands[2],    //Player Name
                            Integer.parseInt(commands[3]),  // Count
                            Float.parseFloat(commands[5]),  // Score
                            Float.parseFloat(commands[4])); //Delay
                    }      
                }else{
                    showArgumentsError();
                }
            }  else if(commands[0].toLowerCase().equals("delete")){
                scoreBoard.deleteParentNodes();
            }else{
                showArgumentsError();
            }
        }

	}

	public static void showArgumentsError(){
        System.out.println("\n"+ANSI_RED+"Warning: Not enough number of arguments / arguments unexpected. Please pass enough arguments to process."+ANSI_RESET);
        System.out.println(ANSI_YELLOW+"Hint: Following are the possible inputs"+
                "\n watcher <ip:port> <list_capacity> "+
                "\n player <ip:port> <name> "+ANSI_RESET);
    }

    public static void showError(String message){
        System.out.println("\n"+ANSI_RED+message+ANSI_RESET);
    }

    public static boolean playerExists(String playerName, String connectionString){
        Stat stat = null;
        ZooKeeper zooKeeper = ZooKeeperConnect.connectZooKeeperServer(connectionString);
        try {
            stat = zooKeeper.exists("/Players/" + playerName, false);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        return stat != null;
        
    }

    public static void deleteParentNodes(){
        ZooKeeper zooKeeper = ZooKeeperConnect.connectZooKeeperServer("localhost:2181");
        try {
            List<String> childrenList = zooKeeper.getChildren("/Players", false);
            for(String childPath: childrenList){
                deleteNode("/Players/"+childPath, zooKeeper);
            }
            List<String> children2List = zooKeeper.getChildren("/RecentScores", false);
            for(String childPath: children2List){
                deleteNode("/RecentScores/"+childPath, zooKeeper);
            }
            deleteNode("/Players", zooKeeper);
            deleteNode("/RecentScores", zooKeeper);
        } catch (KeeperException | InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    public static void deleteNode(String nodePath, ZooKeeper zooKeeper){
        try {
            zooKeeper.delete(nodePath, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, e.getMessage());
		}
    }

    public static void startPostingScoreBoard(String connectionString, String playerName, int count, float scoreMean, float delayMean){
        ZooKeeper zooKeeper = ZooKeeperConnect.connectZooKeeperServer(connectionString);
        if(playerExists(playerName, connectionString)){
            Player player = getNodeData("/Players/"+playerName, zooKeeper);
            if(!player.isOnline){
                player.initAutomationParams(count, scoreMean, delayMean);
                player.start();
            }else{
                System.out.println("Player: "+playerName+" is still currently playing, so you can not force him to play again.");
            }
        }else{
            showError("Player: "+playerName+" has not been created yet. Please creart player by command player <ip:port> <PlayerName>");
        }
        
    }


    public static Player getNodeData(String nodePath, ZooKeeper zooKeeper){
        Player player = null;
        try {
            byte[] data = zooKeeper.getData(nodePath, true, null);
            //String read_data = new String(data, "UTF-8");
            player = (Player) Serializer.deserialize(data);
            //System.out.println("\n CREATED NODE NOTIFIED DATA:: "+player.score);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            return player;
        }
    }
}

/*
Commands:

watcher localhost:2181 7 

player localhost:2181 Thor 
player localhost:2181 Bob
player localhost:2181 Jack
player localhost:2182 Poke

player localhost:2181 Thor 5 2.5 200
player localhost:2181 Bob 7 3.5 1200.5
player localhost:2181 Jack 17 4.5 12.5
player localhost:2181 Poke 17 4.5 12.5

delete localhost:2181
player localhost:2181 lian 5 10 10
*/

/*
CLI:
delete /RecentScores
delete /Players

*/