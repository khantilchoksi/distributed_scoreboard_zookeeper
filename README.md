# distributed_scoreboard_zookeeper
CSC 591-002 Data Intensive Computing Assignment 1

Unity ID: khchoksi

Step 0: To install and start ZooKeeper server on Ubuntu 16.04:  
```  
    sudo apt-get install zookeeper
    cd /usr/share/zookeeper/bin
    sudo ./zkServer.sh start
```


Step 1: git clone the repo and change directory    

Step 2: run `$ sudo make` make sure to run with root permissions  

Step 3: Now you can run the following command:   
`watcher 123.456.789.12:2181 12`    
Please wait until (based on network delay to connect with zookeeper) this message shows:  
 **"Watcher has been successfully created."**

To run the player on each vm, follow step 1 to 3 and then just run following command:  
`player 123.456.789.12:2181 Thor`   
Then it should show: **"New Player added: Thor"** 

Please wait until
and then run 
`player 123.456.789.12:2181 Thor 12 5 10`    


To terminate any of the above program, press ^C.  

Please watch my screencast video for this: (Click on following)

[![Watch Screencast](https://img.youtube.com/vi/X9_oV_N81Mk/0.jpg)](https://youtu.be/X9_oV_N81Mk)

Thank you.

