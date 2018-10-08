// package com.khantil.distributed_scoreboard;

// import org.apache.zookeeper.WatchedEvent;
// import org.apache.zookeeper.Watcher;

// public class PlayerNodeWatcher implements Watcher {

//     @Override
//     public void process(WatchedEvent event) {
//     //     //System.out.println("\n\n WatchEvent Type: "+we.getType().name());
//     //     String nodePath = we.getPath();
//     //     //System.out.println("NODE PATH: "+nodePath);
//     //     if(we.getType() == Event.EventType.NodeDataChanged){
//     //         Player player = getNodeData(nodePath);
//     //         if(player.isOnline){
//     //             if(mostRecentScores.size() >= listSize){
//     //                 mostRecentScores.pop();
//     //                 mostRecentNodePaths.pop();
//     //             }
//     //             mostRecentScores.add(player.toString());
//     //             mostRecentNodePaths.add(nodePath);
//     //             highestScores.add(player);
//     //             printScoreBoard();
//     //         }

//     //     }

//     //     if(we.getType() == Event.EventType.NodeCreated){
//     //         getNodeData(nodePath);
//     //     }
//     // }
// 	}

// }