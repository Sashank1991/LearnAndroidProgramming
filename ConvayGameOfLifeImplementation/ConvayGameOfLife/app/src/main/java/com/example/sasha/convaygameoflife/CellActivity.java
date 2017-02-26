package com.example.sasha.convaygameoflife;


/**
 * Created by sasha on 2/25/2017.
 */

public class CellActivity {
    public static int CELL_SIZE = 22;
    public static int WIDTH = 264 / CELL_SIZE;
    public static int HEIGHT = 264 / CELL_SIZE;
    public static Float Scale;
    private static int[][] _cellData = new int[HEIGHT][WIDTH];


    public CellActivity(float scale) {
        this.Scale = scale;
        initializeGrid();
    }

    public static int[][] getGrid() {
        return _cellData;
    }

    public void initializeGrid() {
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                _cellData[h][w] = 0;
            }
        }
    }

    public void nextGeneration() {
        int neighbours;
        int minimum = 2;
        int maximum = 3;
        int spawn = 3;
        int[][] temp = new int[HEIGHT][WIDTH];
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                neighbours = calculateNeighbours(h, w);

                if (_cellData[h][w] != 0) {
                    if ((neighbours >= minimum) && (neighbours <= maximum)) {
                        temp[h][w] = neighbours;
                    }
                } else {
                    if (neighbours == spawn) {
                        temp[h][w] = spawn;
                    }
                }
            }
        }

        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                _cellData[h][w] = temp[h][w];
            }
        }
    }


    private int calculateNeighbours(int y, int x) {
        int total = (_cellData[y][x] != 0) ? -1 : 0;
        int startPosY = (x - 1 < 0) ? x : x - 1;
        int startPosX = (y - 1 < 0) ? y : y - 1;
        int endPosY = (x + 1 > WIDTH - 1) ? x : x + 1;
        int endPosX = (y + 1 > HEIGHT - 1) ? y : y + 1;
        for (int rowNum = startPosX; rowNum <= endPosX; rowNum++) {
            for (int colNum = startPosY; colNum <= endPosY; colNum++) {
                if (_cellData[rowNum][colNum] != 0) {
                    total++;
                }
            }
        }
        return total;
    }

    public void updateCellData(int x, int y) {

        int X = (int) (x / (Scale * CELL_SIZE));
        int Y = (int) (y / (Scale * CELL_SIZE));

        if (Y < HEIGHT && X < WIDTH) {
            int val = _cellData[Y][X];
            if (val > 0) {
                _cellData[Y][X] = 0;
            } else {
                _cellData[Y][X] = 1;
            }
        }
        // Toast.makeText(this, "ACTION_UP " + "X: " + X + " Y: " + Y, Toast.LENGTH_SHORT).show();

    }
}