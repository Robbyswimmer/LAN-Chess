/**
 * Robert Moseley
 * 3/25/2020
 *
 * This is a simple networked Chess game that supports up to 2 players.
 * This class handles the majority of the game. It is primarily responsible
 * for producing the chess board, encoding it, and sending it across the server
 * to the correct player. It also tracks the current time that has elapsed while
 * playing the game, what pieces are left on the board, whose turn it is, and
 * whether or not a player has won the game. This server class essentially
 * makes all important decisions, while the client just presents information that
 * the server sends to the client.
 *
 * This class uses a HashMap to keep track of all of the pieces and pieces on the board.
 * The keys of the map are represented by positions, while the values of the keys are
 * pieces remaining on the board. Given that there are 64 available spaces on the chessboard,
 * there are 64 entries in the map. When a piece is 'captured' its value in the map is replaced
 * with the piece that captured it.
 */

import java.net.*;
import java.io.*;
import java.util.HashMap;

public class ChessServer {

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            throw new IllegalArgumentException("Parameters: <Port>");

        //create the map of the pieces
        HashMap<String, String> boardMap = new HashMap<String, String>();

        String[] whitePieces = new String[]
                {"wPa", "wPa", "wPa", "wPa", "wPa", "wPa", "wPa", "wPa",
                        "wCa", "wKn", "wBi", "wKi", "wQu", "wBi", "wKn", "wCa"};

        String[] blackPieces = new String[]
                {"bCa", "bKn", "bBi", "bKi", "bQu", "bBi", "bKn", "bCa",
                        "bPa", "bPa", "bPa", "bPa", "bPa", "bPa", "bPa", "bPa",};

        String[] positions = new String[64];

        //create the starting board
        startingBoard(boardMap, positions, blackPieces, whitePieces);

        int servPort = Integer.parseInt(args[0]);

        ServerSocket servSock = new ServerSocket(servPort);

        //simple int to keep track of the turn
        int i = 0;
        long startTime = System.currentTimeMillis();

        for (;;) {

            long elapsedTime = System.currentTimeMillis() - startTime;
            long elapsedSeconds = elapsedTime / 1000;
            long secondsDisplay = elapsedSeconds % 60;
            long elapsedMinutes = elapsedSeconds / 60;

            String s = printBoard(boardMap, positions);
            byte[] boardBytes = s.getBytes();

            Socket clientSock = servSock.accept();
            InputStream in = clientSock.getInputStream();
            OutputStream out = clientSock.getOutputStream();

            //System.out.println(in.read(boardBytes));
            out.write(boardBytes);

            //check for a winner
            if(gameWinner(boardMap, out)) break;

            //displays the elapsed time since the game started
           displayTime(out, secondsDisplay, elapsedMinutes);

            //displays which players turn it is
            displayTurn(out, i);

            //handle the received turn
            handleMove(in, boardMap);

            i++;
            System.out.println();
            clientSock.close();
        }
    }

    public static void handleMove(InputStream in, HashMap<String, String> boardMap) throws IOException {
        byte[] nextMove = new byte[5];
        int n = in.read(nextMove, 0, 5);
        System.out.println("This move: " + new String(nextMove));

        System.out.println("Updating the board...");

        updateBoard(boardMap, new String(nextMove));
    }

    public static void displayTime(OutputStream out, long secondsDisplay, long elapsedMinutes) throws IOException {
        String newSeconds = "" + secondsDisplay;
        if (secondsDisplay < 10) {
            newSeconds = "0" + newSeconds;
        }
        String time = elapsedMinutes + ":" + newSeconds;
        out.write(time.getBytes());
    }

    public static void displayTurn(OutputStream out, int i) throws IOException {
        String player = (i % 2 == 0) ? "white's" : "black's";
        System.out.println("It is currently " + player + " turn");
        out.write(player.getBytes());
    }

    public static boolean gameWinner (HashMap<String, String> boardMap, OutputStream out) throws IOException {
        //determines if a player has won the game!
        if (!boardMap.containsValue("bKi")) {
            System.out.println("White won the game.");
            out.write("b".getBytes());
            return true;
        } else if (!boardMap.containsValue("wKi")) {
            System.out.println("Black won the game.");
            out.write("w".getBytes());
            return true;
        } else {
            out.write("o".getBytes());
        }
        return false;
    }

    public static void startingBoard(HashMap boardMap, String[] positions, String[] blackPieces, String[] whitePieces) {
        createPositions(positions);

        for (int i = 0; i < 16; i++) {
            //populate the map with the starting positions of the black pieces
            boardMap.put(positions[i], blackPieces[i]);
        }

        for (int i = 16; i < 48; i++) {
            //populate the map with the starting positions of the empty pieces
            boardMap.put(positions[i], "");
        }

        for (int i = 48; i < 64; i++) {
            //populate the map with the starting positions of the white pieces
            boardMap.put(positions[i], whitePieces[i - 48]);
        }
    }

    //helper method for startingBoard that creates an array of positions
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

    public static void updateBoard(HashMap boardMap, String nextMove) {
        String pos1 = nextMove.substring(0, 2);
        String pos2 = nextMove.substring(3, 5); //assuming input is in the form "a3 a5" etc

        boardMap.remove(pos2);
        boardMap.put(pos2, boardMap.get(pos1));

        boardMap.remove(pos1);
        boardMap.put(pos1, "");
    }

    public static String printBoard(HashMap boardMap, String[] positions) {
        StringBuilder s = new StringBuilder();

        //print top piece
        s.append("      a       b       c       d       e       f       g       h\n");
        s.append("  +-------+-------+-------+-------+-------+-------+-------+-------+\n");

        //print rows
        int counter = 0;
        for (int i = 1; i < 9; i++) {
            s.append("  |       |       |       |       |       |       |       |       |\n");
            s.append(i + " |");
            for (int j = 0; j < 8; j++) {
                String currentPosition = positions[counter];

                String cPos = (String)boardMap.get(currentPosition);
                int l = cPos.length();

                if (l < 2) {
                    s.append("       |");
                } else {
                    s.append(boardMap.get(currentPosition) + "    |");
                }
                counter++;
            }
            s.append("\n");
            s.append("  +-------+-------+-------+-------+-------+-------+-------+-------+\n");
        }
        return s.toString();
    }
}
