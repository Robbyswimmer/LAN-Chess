# LAN Chess
This project allows two computers on the same network to play a fulle game of Chess against each other through the command line.

![LAN Chess](/images/chess-pic.png)

## How it works

This project is composed of two components: (1) the server, and (2) the client. For every "game" of chess that is played, only one computer must be running the server. The client file for the game is merely in charge of receiving information from the server, displaying that information to the user, and then accepting a valid move from the user and transmitting it back to the server. The client does not handle any decision making, or any logistics of the game, it is simply in charge of translating the information from the server into something understandable for the client. 

The server handles the following:
* Updating the board with user moves 
* Telling each player when it is their turn
* Determining if someone has won the game
* Keep track of how long the game has been going on
* Creating the board and transforming it from a string to an array of bytes
* Sending the board to the correct player

To handle player moves a hash map is used. The keys of this map respond to the alphanumeric index of that spot on the chess board. Thus there are 64 indices in the hash table – a1 to h8. The values of the map respond to the piece that is being stored at that index. All 64 positions are created by attaching a char to an integer and converting it to a string. This is done as a pre-processing measure for all 64 positions on the board.

```java
private static String[] createPositions(String[] positions) {
        int counter = 0;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                char currentLetter = (char) (j + 96);
                positions[counter] =  "" + currentLetter + i;
                counter++;
            }
        }
        return positions;
    }
```
Given that there are only 32 pieces in a game of chess, there is at least 32 empty spaces on the board at all times. Empty spaces are just stored as empty strings in the map, and when a piece is captured its place in the map is replaced with an empty string so that it is not included in the next iteration of the board. 

```java
public static void updateBoard(HashMap boardMap, String nextMove) {
        String pos1 = nextMove.substring(0, 2);
        String pos2 = nextMove.substring(3, 5); //assuming input is in the form "a3 a5" etc

        boardMap.remove(pos2);
        boardMap.put(pos2, boardMap.get(pos1));

        boardMap.remove(pos1);
        boardMap.put(pos1, "");
    }
```

Checking for a winner in chess can be very difficult, so in order to improve on this, a winner is declared when the opposing king has been captured. 

When a player makes a move, that move is sent to the server where the board is updated with the new move and then the board is converted into a string, and then finally into an array of bytes so that the operation time of the server is maintained. Initially, the chess board was being sent over the server as an actual object, but the performance of that was poor and the current solution of creating the board as a string was found to be much better.

## How to play

* Download the zip file for this project

* Unzip the package

* Open Terminal or your favorite command line tool
  * Navigate to the directory that containes the Java files
  * Type 'Javac ChessServer.java' to compile the server code
  * Next type 'Java ChessServer Your-Port-Here>' which will launch the server. You can use any port that is open on your computer. Once you have started the server nothing will appear until you connect a client.
  
* Open another terminal window and navigate to the same directory
  * Type 'Javac ChessClient.java' to compile the client code
  * Next type 'Java ChessClient IP-of-target-computer Your-username' – example: "Java ChessClient 127.0.0.1 person1". If you use 127.0.0.1 you can play both clients on one computer, you will just have to open a third terminal window.
  
At this point you should be ready to start playing. Both clients should be connected to the computer and whoever is playing 'white' should be able to make the first move. 

Have fun!

Note: You only have to go through the first steps if you are the computer hosting the server, if you are just connecting to the server all you have to do is connect using the IP of the other computer. 
