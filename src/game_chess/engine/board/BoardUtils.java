/**
 * Author: Onanefe Osah
 * Author: Osama
 * Date Created:
 *
 * Board Utilities class.
 * This class holds necessary information about the board
 *
 */

package game_chess.engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BoardUtils {

    //column exceptions for pieces, due to movement constraints
    public static final boolean[] FIRST_COLUMN = inColumn(0);
    public static final boolean[] SECOND_COLUMN = inColumn(1);
    public static final boolean[] SEVENTH_COLUMN = inColumn(6);
    public static final boolean[] EIGHTH_COLUMN = inColumn(7);

    //row exceptions for pieces, due to movement constraints
    public static final boolean[] EIGHTH_RANK = inRow(0);
    public static final boolean[] SEVENTH_RANK = inRow(8);
    public static final boolean[] SIXTH_RANK = inRow(16);
    public static final boolean[] FIFTH_RANK = inRow(24);
    public static final boolean[] FOURTH_RANK = inRow(32);
    public static final boolean[] THIRD_RANK = inRow(40);
    public static final boolean[] SECOND_RANK = inRow(48);
    public static final boolean[] FIRST_RANK = inRow(56);

    public static final String[] ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

    //Board info
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private static String[] initializeAlgebraicNotation(){
        return new String[]{
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1" //get the full thing from his github TODO
        };
    }

    private static Map <String, Integer> initializePositionToCoordinateMap(){
        final  Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = 0; i < NUM_TILES; i++ ) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION[i], i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    //calculate column space
    private static boolean[] inColumn(int columnNumber) {

        final boolean[] column = new boolean[NUM_TILES];
        do{
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        }while(columnNumber < NUM_TILES);
        return column;

    }

    //calculate row spaces
    private static boolean[] inRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES];
        do{
            row[rowNumber] = true;
            rowNumber++;
        }while(rowNumber % NUM_TILES_PER_ROW != 0);

        return row;
    }

    public static boolean isValidTileCoordinate(int coordinate){
        return coordinate >= 0 && coordinate < NUM_TILES;
    }

    public static int getCoordinateAtPosition(final String position){
        return POSITION_TO_COORDINATE.get(position);

    }
    public static String getPositionAtCoordinate(final int coordinate){
        return ALGEBRAIC_NOTATION[coordinate];
    }
}
