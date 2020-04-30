import java.io.Serializable;
import java.util.HashMap;

public class ChessBoard {

    public HashMap<String, String> boardMap = new HashMap<String, String>();

    private String[] whitePieces = new String[]
            {"wPa", "wPa", "wPa", "wPa", "wPa", "wPa", "wPa", "wPa",
             "wCa", "wKn", "wBi", "wKi", "wQu", "wBi", "wKn", "wCa"};

    private String[] blackPieces = new String[]
            {"bCa", "bKn", "bBi", "bKi", "bQu", "bBi", "bKn", "bCa",
            "bPa", "bPa", "bPa", "bPa", "bPa", "bPa", "bPa", "bPa",};

    private String[] positions = new String[64];

    public void startingBoard() {
        createPositions();

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
    private String[] createPositions() {
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

    public void updateBoard(String nextMove) {
        String pos1 = nextMove.substring(0, 2);
        String pos2 = nextMove.substring(3, 5); //assuming input is in the form "a3 a5" etc

        boardMap.remove(pos2);
        boardMap.put(pos2, boardMap.get(pos1));

        boardMap.remove(pos1);
        boardMap.put(pos1, "");
    }

    public void printBoard() {

        //print top piece
        System.out.println("      a       b       c       d       e       f       g       h");
        System.out.println("  +-------+-------+-------+-------+-------+-------+-------+-------+");

        //print rows
        int counter = 0;
        for (int i = 1; i < 9; i++) {
            System.out.println("  |       |       |       |       |       |       |       |       |");
            System.out.print(i + " |");
            for (int j = 0; j < 8; j++) {

                String currentPosition = positions[counter];
                if (boardMap.get(currentPosition).length() < 2) {
                    System.out.print("       |");
                } else {
                    System.out.print(boardMap.get(currentPosition) + "    |");
                }
                counter++;
            }
            System.out.println();
            System.out.println("  +-------+-------+-------+-------+-------+-------+-------+-------+");
        }
    }
}
