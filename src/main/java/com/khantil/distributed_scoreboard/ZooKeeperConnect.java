package com.khantil.distributed_scoreboard;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZooKeeperConnect {

    
    private static final Logger LOGGER = Logger.getLogger( ZooKeeperConnect.class.getName() );


    public static ZooKeeper connectZooKeeperServer(String hostUrl){
        ZooKeeper zooKeeper = null;
        CountDownLatch connectionLatch = new CountDownLatch(1);
        Watcher watcher = new Watcher(){
        
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        };
        try {
            zooKeeper = new ZooKeeper(hostUrl, 5000, watcher);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return zooKeeper;
    }

}
