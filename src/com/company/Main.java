package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        List<Position> badGrids = new ArrayList<Position>();
        badGrids.add(new Position(1*50, 3*50));
        badGrids.add(new Position(1*50, 4*50));
        badGrids.add(new Position(2*50, 3*50));
        badGrids.add(new Position(2*50, 7*50));
        badGrids.add(new Position(2*50, 8*50));
        badGrids.add(new Position(3*50, 3*50));
        badGrids.add(new Position(3*50, 7*50));
        badGrids.add(new Position(3*50, 8*50));
        badGrids.add(new Position(4*50, 3*50));
        badGrids.add(new Position(4*50, 4*50));
        for (int i = 5; i < 3000; ++i) {
            for (int j = 0; j < 3000; j++) {
                badGrids.add(new Position(i*50, j*50));
            }
        }
        BadAreaIdentify.getInstance().getBadAreas(badGrids);
    }
}
