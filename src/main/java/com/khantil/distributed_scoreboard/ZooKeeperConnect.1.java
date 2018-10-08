// package com.khantil.distributed_scoreboard;

// import org.apache.zookeeper.KeeperException;
// import org.apache.zookeeper.WatchedEvent;
// import org.apache.zookeeper.Watcher;
// import org.apache.zookeeper.ZooKeeper;

// import java.io.IOException;
// import java.io.UnsupportedEncodingException;
// import java.util.*;
// import java.util.logging.Level;
// import java.util.logging.Logger;

// public class ZooKeeperConnect1 {

//     public static ZooKeeper zooKeeper;
//     private static Watcher scoreBoardWatcher;
//     private static final Logger LOGGER = Logger.getLogger( ZooKeeperConnect.class.getName() );
//     private static PriorityQueue<Player> highestScores = new PriorityQueue<>(Collections.reverseOrder());
//     private static LinkedList<String> mostRecentScores = new LinkedList<>();
//     private static LinkedList<String> mostRecentNodePaths = new LinkedList<>();
//     private static int listSize;

//     public static void connectZooKeeperServer(String hostUrl, int maxListSize){
//         listSize = maxListSize;
//         try {
//             zooKeeper = new ZooKeeper(hostUrl, 5000, scoreBoardWatcher);
//         } catch (IOException e) {
//             e.printStackTrace();
//             LOGGER.log(Level.SEVERE, e.getMessage());
//         }
//     }

//     public static void initializeScoreBoardWatcher(){
//         scoreBoardWatcher = new Watcher() {

//             public void process(WatchedEvent we) {
//                 //System.out.println("\n\n WatchEvent Type: "+we.getType().name());
//                 String nodePath = we.getPath();
//                 //System.out.println("NODE PATH: "+nodePath);
//                 if(we.getType() == Event.EventType.NodeDataChanged){
//                     Player player = getNodeData(nodePath);
//                     if(player.isOnline){
//                         if(mostRecentScores.size() >= listSize){
//                             mostRecentScores.pop();
//                             mostRecentNodePaths.pop();
//                         }
//                         mostRecentScores.add(player.toString());
//                         mostRecentNodePaths.add(nodePath);
//                         highestScores.add(player);
//                         printScoreBoard();
//                     }

//                 }

//                 if(we.getType() == Event.EventType.NodeCreated){
//                     getNodeData(nodePath);
//                 }
//             }
//         };
//     }

//     public static void printScoreBoard(){
//         System.out.println("\nMost Recent Scores");
//         System.out.println("----------------------");

//         for(int i = mostRecentScores.size()-1; i >= 0; i--){
//             System.out.print(mostRecentScores.get(i));
//             Player player = getNodeData(mostRecentNodePaths.get(i));
//             if(player.isOnline){
//                 System.out.print("\t **");
//             }
//             System.out.print("\n");
//         }

//         System.out.println("\nHighest Scores");
//         System.out.println("----------------------");
//         Iterator<Player> highestScoreIterator = highestScores.iterator();

//         for(int i = 0; i < listSize && highestScoreIterator.hasNext() ; i++){
//             Player highestScorePlayer = highestScoreIterator.next();
//             System.out.print(highestScorePlayer.toString());
//             Player onlinePlayer = getNodeData(highestScorePlayer.zNodePath);
//             if(onlinePlayer.isOnline){
//                 System.out.print("\t **");
//             }
//             System.out.print("\n");
//         }
//     }

//     public static Player getNodeData(String nodePath){
//         Player player = null;
//         try {
//             byte[] data = zooKeeper.getData(nodePath, true, null);
//             //String read_data = new String(data, "UTF-8");
//             player = (Player) Serializer.deserialize(data);
//             //System.out.println("\n CREATED NODE NOTIFIED DATA:: "+player.score);
//         } catch (KeeperException e) {
//             e.printStackTrace();
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         } catch (UnsupportedEncodingException e) {
//             e.printStackTrace();
//         }finally {
//             return player;
//         }
//     }

//     public static void startPostingScoreBoard(String playerName, int count, int delay, float mean){
//         Player player = getNodeData("/"+playerName);
//         if(!player.isOnline){
//             player.initAutomationParams(count, delay, mean);
//             player.start();
//         }else{
//             System.out.println("Player: "+playerName+" is still currently playing, so you can not force him to play again.");
//         }
//     }


// //    public ZooKeeper getZooKeeper(){
// //        if(this.zooKeeper != null){
// //            return this.zooKeeper;
// //        }else{
// //            LOGGER.log(Level.INFO, "Something went wrong. ZooKeeper object not found." );
// //        }
// //    }
// }
