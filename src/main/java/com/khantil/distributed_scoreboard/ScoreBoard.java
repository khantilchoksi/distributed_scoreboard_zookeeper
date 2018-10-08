package com.khantil.distributed_scoreboard;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ScoreBoard {

    public String recentScoresPath = "/RecentScores";
    public String recentScoresString = "RecentScores";
    public String playersPath = "/Players";
    public String playersString = "Players";

    public static int listSize;

    private static final Logger LOGGER = Logger.getLogger( ScoreBoard.class.getName() );

    public static ZooKeeper zooKeeper;
    public String connectionString;

    public ScoreBoard(String connectionString, int listSize){
        this.listSize = listSize;

        //Connect with ZooKeeper
        zooKeeper = ZooKeeperConnect.connectZooKeeperServer(connectionString);

        //Create Parent Nodes : RecentScores, HighestScores, Players
        createParentNode(recentScoresPath, recentScoresString);

        createParentNode(playersPath, playersString);

        //Set Watcher on RecentScores,  and Players
        try {
            zooKeeper.getChildren(recentScoresPath, new ScoreBoardWatcher());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        System.out.println("Watcher has been successfully created.");
    }

    
    public void createParentNode(String zNodePath, String data){
        byte[] byteArrayData = data.getBytes();
        try {
            zooKeeper.create(zNodePath, byteArrayData, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
		}
    }

    public void deleteParentNodes(){
        deleteNode(recentScoresPath);
        deleteNode(playersPath);
        System.out.println("Parent nodes have been successfully deleted.");
    }

    public void deleteNode(String nodePath){
        try {
            zooKeeper.delete(nodePath, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, e.getMessage());
		}
    }
    
}