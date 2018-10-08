package com.khantil.distributed_scoreboard;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ScoreBoardWatcher implements Watcher {
    private static final Logger LOGGER = Logger.getLogger( ScoreBoardWatcher.class.getName() );
    @Override
    public void process(WatchedEvent event) {

        String nodePath = event.getPath();
        // System.out.println("\n\n WatchEvent Type: "+event.getType().name());
        // System.out.println("NODE PATH: "+nodePath);
        if(event.getType() == Event.EventType.NodeChildrenChanged){

            try {
                List<String> childrenPathList = ScoreBoard.zooKeeper.getChildren(nodePath, new ScoreBoardWatcher());
                printScoreBoard(childrenPathList);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, e.getMessage());
			}

            //Player player = getNodeData(nodePath);
            // if(player.isOnline){
            //     if(mostRecentScores.size() >= listSize){
            //         mostRecentScores.pop();
            //         mostRecentNodePaths.pop();
            //     }
            //     mostRecentScores.add(player.toString());
            //     mostRecentNodePaths.add(nodePath);
            //     highestScores.add(player);
            //     printScoreBoard();
            // }

        }

        // if(we.getType() == Event.EventType.NodeCreated){
        //     getNodeData(nodePath);
        // }
    }

    //Sort Paths based on numnbers
    public void sortPaths(List<String> paths){
        //System.out.println("Received LIST: "+Arrays.toString(paths.toArray()));
        Collections.sort(paths, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                int s1 = Integer.valueOf(o1.substring(1));
                int s2 = Integer.valueOf(o2.substring(1));
                return s2-s1;
            }

        });
        //System.out.println("AFTER SORTING LIST: "+Arrays.toString(paths.toArray()));
    }
    
    public void printScoreBoard(List<String> paths){
        sortPaths(paths);
        //System.out.println("AFTER SORTING LIST: "+Arrays.toString(paths.toArray()));

        System.out.println("\n\n*********************\n\nMost Recent Scores");
        System.out.println("----------------------");

        for(int i = 0; i < paths.size() && i < 25 && i < ScoreBoard.listSize; i++){
            
            Player scorePlayer = getNodeData("/RecentScores/"+paths.get(i));
            System.out.print(scorePlayer.toString());

            Player player = getNodeData(scorePlayer.zNodePath);
            //System.out.println("I: "+i+" Player: "+player.name);
            if(player.isOnline){
                System.out.print("\t **");
            }
            System.out.print("\n");
        }

        System.out.println("\nHighest Scores");
        System.out.println("----------------------");
        // Iterator<Player> highestScoreIterator = highestScores.iterator();
        List<Player> scoresPlayers = new ArrayList<Player>();
        for(int i = 0; i < paths.size(); i++){
            Player scorePlayer = getNodeData("/RecentScores/"+paths.get(i));
            scoresPlayers.add(scorePlayer);
        }

        Collections.sort(scoresPlayers, Collections.reverseOrder());

        for(int i = 0; i < scoresPlayers.size() && i < 25 && i < ScoreBoard.listSize; i++){
            Player scorePlayer = scoresPlayers.get(i);
            System.out.print(scorePlayer.toString());
            Player player = getNodeData(scorePlayer.zNodePath);
            if(player.isOnline){
                System.out.print("\t **");
            }
            System.out.print("\n");
        }
    }

    public static Player getNodeData(String nodePath){
        Player player = null;
        try {
            byte[] data = ScoreBoard.zooKeeper.getData(nodePath, false, null);
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

    // public static void startPostingScoreBoard(String playerName, int count, int delay, float mean){
    //     Player player = getNodeData("/"+playerName);
    //     if(!player.isOnline){
    //         player.initAutomationParams(count, delay, mean);
    //         player.start();
    //     }else{
    //         System.out.println("Player: "+playerName+" is still currently playing, so you can not force him to play again.");
    //     }
    // }

}