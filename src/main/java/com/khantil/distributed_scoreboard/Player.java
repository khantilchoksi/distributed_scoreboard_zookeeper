package com.khantil.distributed_scoreboard;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Player extends Thread implements Comparable<Player>, Serializable {
    String name;
    String zNodePath;
    boolean isOnline;
    int score;
    int count;
    float scoreMean, delayMean;
    private static final Logger LOGGER = Logger.getLogger( Player.class.getName() );
    Random random;
    //ZooKeeper zooKeeper;
    // PlayerNodeWatcher playerNodeWatcher;
    public String recentScoresPath = "/RecentScores/";
    public String highestScoresPath = "/HighestScores/";
    public String playersPath = "/Players/";
    private String connectionString;

    public Player(String name, String connectionString){
        this.name = name;
        this.zNodePath = playersPath+this.name;
        // this.playerNodeWatcher = new PlayerNodeWatcher();
        this.connectionString = connectionString;
        this.isOnline = false;
        this.random = new Random();
        this.score = 0;
        // try {
        //   /*  If the watch is true and the call is successful (no exception is thrown),
        //      a watch will be left on the node with the given path.
        //      The watch will be triggered by a successful operation
        //      that creates/delete the node or sets the data on the node. */

        //     // zooKeeper.exists(zNodePath, playerNodeWatcher);

        //     byte[] data = Serializer.serialize(this);
        //     zooKeeper.create(zNodePath, data,
        //             ZooDefs.Ids.OPEN_ACL_UNSAFE,
        //             CreateMode.PERSISTENT);
        // } catch (KeeperException e) {
        //     e.printStackTrace();
        //     LOGGER.log(Level.SEVERE, e.getMessage());
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        //     LOGGER.log(Level.SEVERE, e.getMessage());
        // }catch (IOException e) {
        //     e.printStackTrace();
        //     LOGGER.log(Level.SEVERE, e.getMessage());
        // }
        
    }

    @Override
    public void run() {
        this.isOnline = true;
        try {
            for (int i = 0; i < this.count; i++) {

                score = getRandomNumber(scoreMean);
                this.setNodeData();
                int delay = getRandomNumber(delayMean);
                Thread.sleep(delay);
            }
            this.leave();
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }


    public void initAutomationParams(int count, float scoreMean, float delayMean){
        this.count = count;
        this.scoreMean = scoreMean;
        this.delayMean = delayMean * 1000; //Converting seconds to miliseconds for thread to sleep
    }

    public void joinScoreboard(){
        ZooKeeper zooKeeper = ZooKeeperConnect.connectZooKeeperServer(connectionString);
        try {
            byte[] data = Serializer.serialize(this);
            zooKeeper.create(zNodePath, data,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }


    public int getRandomNumber(float mean){
        float standardDeviation = mean > 1 ? mean-1 : 1;
        int number = (int) Math.round( (random.nextGaussian() * standardDeviation) + mean );

        //To prevent score and delay being negatively generated
        return number < 0 ? number*(-1) : number;
    }

    public void leave(){
        ZooKeeper zooKeeper = ZooKeeperConnect.connectZooKeeperServer(connectionString);
        System.out.println("Player- "+this.name+" :: has successfully posted all automation scores. Now, I am offline. Happy Playing!");
        this.isOnline = false;
        byte[] data;
        try {
            data = Serializer.serialize(this);
            zooKeeper.setData(zNodePath, data, zooKeeper.exists(zNodePath, true).getVersion());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public void setNodeData(){
        try {
            ZooKeeper zooKeeper = ZooKeeperConnect.connectZooKeeperServer(connectionString);
            System.out.println("Score posted by: "+this.name+" Score Value: "+this.score);
            byte[] data = Serializer.serialize(this);
            zooKeeper.setData(zNodePath, data,
                    zooKeeper.exists(zNodePath, true).getVersion());

            zooKeeper.create(recentScoresPath+"P", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (KeeperException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public int compareTo(Player player) {
        return this.score - player.score;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.name);
        stringBuilder.append("\t "+this.score);


        return stringBuilder.toString();
    }
}
