/**
 * Robert Moseley
 * 3/25/2020
 *
 * This class handles the client side data of the game.
 * It receives information from the server, and then presents
 * the relevant information to the player. This class handles
 * outputting the current move, and printing the current board
 * for the player to see. It also displays how long the player
 * has been playing, and the current color that is making a move.
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class ChessClient {

    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 3)
            throw new IllegalArgumentException("Parameters: <Server> <Word> [<Port>]");

        for (;;) {
            Scanner playerInput = new Scanner(System.in);

            String server = args[0];
            byte[] byteBuffer = args[1].getBytes();

            int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;

            Socket socket = new Socket(server, servPort);
            System.out.println();
            System.out.println("Waiting on opponent.");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();


            byte[] board = in.readNBytes(1759);

            int totalBytesRcvd = 0;
            int bytesRcvd;

            while (totalBytesRcvd < byteBuffer.length) {
                if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd, byteBuffer.length - totalBytesRcvd)) == -1) {
                    throw new SocketException("Connection closed prematurely");
                }

                totalBytesRcvd += bytesRcvd;
            }

            System.out.println("\n" + new String(board) + "---+");

           if (determineWinner(socket, in)) break;
           printTime(in);
           handleMove(in, out, playerInput);
           socket.close();
        }
    }

    public static boolean determineWinner(Socket socket, InputStream in) throws IOException {
        //receives information about whether black or white won
        byte[] winner = new byte[1];
        int w = in.read(winner, 0, 1);

        if (new String(winner).equals("w")) {
            System.out.println("Black won the game!");
            socket.close();
            return true;
        } else if (new String(winner).equals("b")) {
            System.out.println("White won the game!");
            socket.close();
            return true;
        } else if (new String(winner).equals("o")){
            //do nothing! The game must go on!
            return false;
        }
        return false;
    }

    public static void printTime(InputStream in) throws IOException {
        byte[] time = new byte[4];
        int t = in.read(time, 0, 4);
        System.out.println("Time: " + new String(time));

        System.out.println();
    }

    public static void handleMove(InputStream in, OutputStream out, Scanner playerInput) throws IOException {
        byte[] currentPlayer = new byte[7];
        int n = in.read(currentPlayer, 0, 7);

        System.out.println(new String(currentPlayer) + " move:");
        String nextMove = playerInput.nextLine();

        System.out.println();

        out.write(nextMove.getBytes());

    }
}
