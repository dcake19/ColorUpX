package com.dcake19.android.colorupx;

import com.dcake19.android.colorupx.game.model.Coordinates;
import com.dcake19.android.colorupx.game.model.GameBoard;
import com.dcake19.android.colorupx.game.model.UpdateSquare;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameBoardUnitTest {

    GameBoard mGameBoard;

    @Test
    public void swipe_right(){
        mGameBoard = new GameBoard(3,6,9,3,6);

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,8,0,0,0,7},{3,2,2,0,2,7},{1,4,1,0,5,6}};

        mGameBoard.loadGame(board,0);

        ArrayList<UpdateSquare> updatesR = new ArrayList<>();
        updatesR.add(new UpdateSquare(new Coordinates(9,1),3,GameBoard.DIRECTION_RIGHT,false));
        updatesR.add(new UpdateSquare(new Coordinates(10,2),2,GameBoard.DIRECTION_RIGHT,true));
        updatesR.add(new UpdateSquare(new Coordinates(10,1),2,GameBoard.DIRECTION_RIGHT,false));
        updatesR.add(new UpdateSquare(new Coordinates(10,0),2,GameBoard.DIRECTION_RIGHT,false));
        updatesR.add(new UpdateSquare(new Coordinates(11,2),1,GameBoard.DIRECTION_RIGHT,false));
        updatesR.add(new UpdateSquare(new Coordinates(11,1),1,GameBoard.DIRECTION_RIGHT,false));
        updatesR.add(new UpdateSquare(new Coordinates(11,0),1,GameBoard.DIRECTION_RIGHT,false));

        assertTrue(mGameBoard.getUpdates(GameBoard.DIRECTION_RIGHT).equals(updatesR));

    }

    @Test
    public void swipe_left(){
        mGameBoard = new GameBoard(3,6,9,3,6);

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,8,0,0,0,7},{3,2,2,0,2,7},{1,4,1,0,5,6}};

        mGameBoard.loadGame(board,0);

        ArrayList<UpdateSquare> updates = new ArrayList<>();
        updates.add(new UpdateSquare(new Coordinates(9,1),1,GameBoard.DIRECTION_LEFT,false));
        updates.add(new UpdateSquare(new Coordinates(9,5),4,GameBoard.DIRECTION_LEFT,false));
        updates.add(new UpdateSquare(new Coordinates(10,2),1,GameBoard.DIRECTION_LEFT,true));
        updates.add(new UpdateSquare(new Coordinates(10,4),2,GameBoard.DIRECTION_LEFT,false));
        updates.add(new UpdateSquare(new Coordinates(10,5),2,GameBoard.DIRECTION_LEFT,false));
        updates.add(new UpdateSquare(new Coordinates(11,4),1,GameBoard.DIRECTION_LEFT,false));
        updates.add(new UpdateSquare(new Coordinates(11,5),1,GameBoard.DIRECTION_LEFT,false));


        assertTrue(mGameBoard.getUpdates(GameBoard.DIRECTION_LEFT).equals(updates));

    }

    @Test
    public void swipe_up(){
        mGameBoard = new GameBoard(3,6,9,3,6);

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,8,0,0,0,7},{3,2,2,0,2,7},{1,4,1,5,0,6}};

        mGameBoard.loadGame(board,0);

        ArrayList<UpdateSquare> updates = new ArrayList<>();
        updates.add(new UpdateSquare(new Coordinates(10,0),1,GameBoard.DIRECTION_UP,false));
        updates.add(new UpdateSquare(new Coordinates(11,0),1,GameBoard.DIRECTION_UP,false));
        updates.add(new UpdateSquare(new Coordinates(10,2),1,GameBoard.DIRECTION_UP,false));
        updates.add(new UpdateSquare(new Coordinates(11,2),1,GameBoard.DIRECTION_UP,false));
        updates.add(new UpdateSquare(new Coordinates(11,3),2,GameBoard.DIRECTION_UP,false));
        updates.add(new UpdateSquare(new Coordinates(10,4),1,GameBoard.DIRECTION_UP,false));
        updates.add(new UpdateSquare(new Coordinates(10,5),1,GameBoard.DIRECTION_UP,true));
        updates.add(new UpdateSquare(new Coordinates(11,5),1,GameBoard.DIRECTION_UP,false));

        assertTrue(mGameBoard.getUpdates(GameBoard.DIRECTION_UP).equals(updates));

    }

    @Test
    public void swipe_down(){
        mGameBoard = new GameBoard(3,6,9,3,6);

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,8,0,0,0,7},{3,2,2,0,2,7},{1,4,1,5,0,6}};

        mGameBoard.loadGame(board,0);

        ArrayList<UpdateSquare> updates = new ArrayList<>();
        updates.add(new UpdateSquare(new Coordinates(10,4),1,GameBoard.DIRECTION_DOWN,false));
        updates.add(new UpdateSquare(new Coordinates(9,5),1,GameBoard.DIRECTION_DOWN,true));

        assertTrue(mGameBoard.getUpdates(GameBoard.DIRECTION_DOWN).equals(updates));
    }

    @Test
    public void no_updates(){
        mGameBoard = new GameBoard(3,6,9,3,6);

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{4,6,3,8,9,1},{3,2,1,6,2,7},{0,0,0,0,0,0}};

        mGameBoard.loadGame(board,0);
        assertTrue(mGameBoard.getUpdates(GameBoard.DIRECTION_UP).size()==0);
        assertTrue(mGameBoard.getUpdates(GameBoard.DIRECTION_LEFT).size()==0);
        assertTrue(mGameBoard.getUpdates(GameBoard.DIRECTION_RIGHT).size()==0);
    }

    @Test
    public void add_fallen_square_1(){
        mGameBoard = new GameBoard(3,6,9,3,6);

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,8,0,0,0,7},{3,2,2,0,2,7},{1,4,1,5,0,6}};

        int[][] boardNew = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,3,0,0,0,0},{0,8,0,0,0,7},{3,2,2,0,2,7},{1,4,1,5,0,6}};

        mGameBoard.loadGame(board,0);
        assertTrue(mGameBoard.getWellRows()==9);
        assertTrue(mGameBoard.addFallenSquare(1,3)==-1);

        assertTrue(Arrays.deepEquals(mGameBoard.getBoard(),boardNew));
        assertTrue(mGameBoard.getWellRows()==8);
        assertFalse(mGameBoard.gameOver());
    }

    @Test
    public void add_fallen_square_2(){
        mGameBoard = new GameBoard(3,6,9,3,6);

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,8,0,0,0,0},{3,2,2,0,2,0},{1,4,1,5,0,0}};

        int[][] boardNew = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{3,8,9,0,2,0},{3,2,2,4,2,0},{1,4,1,5,0,7}};

        mGameBoard.loadGame(board,0);

        assertTrue(mGameBoard.addFallenSquare(0,3)==9);
        assertTrue(mGameBoard.addFallenSquare(2,9)==9);
        assertTrue(mGameBoard.addFallenSquare(3,4)==10);
        assertTrue(mGameBoard.addFallenSquare(4,2)==9);
        assertTrue(mGameBoard.addFallenSquare(5,7)==11);
        assertTrue(Arrays.deepEquals(mGameBoard.getBoard(),boardNew));
        assertTrue(mGameBoard.getWellRows()==9);
    }

    @Test
    public void game_over(){
        mGameBoard = new GameBoard(3,6,0,3,6);

        int[][] board = {{0,6,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,8,0,0,0,0},{3,2,2,0,2,0},{1,4,1,5,0,0}};

        mGameBoard.loadGame(board,0);

        assertTrue(mGameBoard.addFallenSquare(1,2)==-1);

        assertTrue(mGameBoard.gameOver());
    }

    @Test
    public void create_board(){
        int initialSquares = 6;
        mGameBoard = new GameBoard(12,6,9,3,9);
        mGameBoard.createRandomBoard(initialSquares);

        int[][] board = mGameBoard.getBoard();
        int count = 0;
        for (int i=0;i<board.length;i++)
            for (int j=0;j<board[i].length;j++)
                if(board[i][j]>0)
                    count++;

        assertTrue(count==initialSquares);
    }

}