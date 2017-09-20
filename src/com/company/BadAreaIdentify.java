package com.company;
import java.util.*;

class GridIndex {
    int row;
    int col;

    public GridIndex(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

}
class Grid {
    private int label;
    private Position position;

    public Grid(Position position) {
        this.label = -1;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }
}
class Boundary {
    private Set<GridIndex> boundary;
    private Map<Integer, Map<Integer, GridIndex>> boundaryMap;
    private GridIndex firstBoundary;
    private int gridNum = 0;

    public Boundary() {
        this.boundary = new HashSet<>();
        this.boundaryMap = new HashMap<>();
    }

    public void setGridNum(int gridNum) {
        this.gridNum = gridNum;
    }

    public GridIndex getFirstBoundary() {
        return firstBoundary;
    }

    public void setFirstBoundary(GridIndex firstBoundary) {
        this.firstBoundary = firstBoundary;
    }

    public void add(GridIndex gridIndex) {
        boundary.add(gridIndex);
        if (boundaryMap.containsKey(gridIndex.getRow())) {
            boundaryMap.get(gridIndex.getRow()).put(gridIndex.getCol(), gridIndex);
        } else {
            Map<Integer, GridIndex> row = new HashMap<>();
            row.put(gridIndex.getCol(), gridIndex);
            boundaryMap.put(gridIndex.getRow(), row);
        }
    }

    public GridIndex index2instance(GridIndex gridIndex) {
        if (boundaryMap.containsKey(gridIndex.getRow()) &&
                boundaryMap.get(gridIndex.getRow()).containsKey(gridIndex.getCol())) {
            return boundaryMap.get(gridIndex.getRow()).get(gridIndex.getCol());
        }
        return null;
    }

    static public GridIndex getNextClockWiseGridIndex(GridIndex last, GridIndex cur) {
        List<GridIndex> offsets = new ArrayList<>();
        offsets.add(new GridIndex(-1, -1));
        offsets.add(new GridIndex(-1, 0));
        offsets.add(new GridIndex(-1, 1));
        offsets.add(new GridIndex(0, 1));
        offsets.add(new GridIndex(1, 1));
        offsets.add(new GridIndex(1, 0));
        offsets.add(new GridIndex(1, -1));
        offsets.add(new GridIndex(0, -1));
        offsets.add(new GridIndex(-1, -1));
        for (int i = 0; i < 8; i++) {
            if (last.getRow()== cur.getRow()+offsets.get(i).getRow() &&
                    last.getCol() == cur.getCol()+offsets.get(i).getCol()){
                return new GridIndex(cur.getRow()+offsets.get(i+1).getRow(),
                        cur.getCol()+offsets.get(i+1).getCol());
            }
        }
        return null;
    }

    private GridIndex getNextBoundary(GridIndex last, GridIndex cur) {
        GridIndex nextIndex;
        while (true) {
            nextIndex = getNextClockWiseGridIndex(last, cur);
            GridIndex next = index2instance(nextIndex);
            if (next != null) {
                return next;
            }
            last = nextIndex;
        }
    }

    public List<GridIndex> regularization() {
        if (gridNum <= 3) {
            return null;
        }
        GridIndex cur = getFirstBoundary();
        GridIndex last = new GridIndex(cur.getRow(), cur.getCol()-1);
        Map<GridIndex, GridIndex> processed = new HashMap<>();
        List<GridIndex> regularGridIndex = new ArrayList<>();

        GridIndex next;
        while (true) {
            next = getNextBoundary(last, cur);
            if (processed.containsKey(cur) &&
                    processed.get(cur) == next) {
                break;
            }
            regularGridIndex.add(cur);
            processed.put(cur, next);
            last = cur;
            cur = next;
        }

        return regularGridIndex;
    }

}
class Matrix {
    private Map<Integer, HashMap<Integer, Grid>> matrix;
    private GridIndex start;
    private GridIndex end;

    public GridIndex getStart() {
        return start;
    }

    Matrix(List<Position> Grids) {
        matrix = new HashMap<>();
        int minRow = Integer.MAX_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int maxCol = Integer.MIN_VALUE;

        for (Position position: Grids) {
            int colIndex = Math.floorDiv(position.getY(), 50);
            int rowIndex = Math.floorDiv(position.getX(), 50);
            if (colIndex < minCol) {
                minCol = colIndex;
            }
            if (colIndex > maxCol) {
                maxCol = colIndex;
            }
            if (rowIndex < minRow) {
                minRow = rowIndex;
            }
            if (rowIndex > maxRow) {
                maxRow = rowIndex;
            }

            HashMap<Integer, Grid> row = matrix.get(rowIndex);
            if (row == null) {
                row = new HashMap<>();
                matrix.put(rowIndex, row);
            }
            row.put(colIndex, new Grid(position));
        }
        this.start = new GridIndex(minRow, minCol);
        this.end = new GridIndex(maxRow, maxCol);
    }
    public GridIndex findNexUntreated(GridIndex last) {
        for (int rowIndex = last.getRow(); rowIndex <= end.getRow();++rowIndex) {
            HashMap<Integer, Grid> row = matrix.get(rowIndex);
            if (row == null) {
                continue;
            }
            int colIndex = rowIndex != last.getRow() ? 0 : last.getCol();
            for (; colIndex <= end.getCol(); ++colIndex) {
                if (row.containsKey(colIndex) && -1 == row.get(colIndex).getLabel()) {
                    return new GridIndex(rowIndex,colIndex);
                }
            }
        }
        return null;
    }

    public Boundary generateBadArea(GridIndex originator, int label) {
        int minBoundaryCol = Integer.MAX_VALUE;
        Stack<GridIndex> stack = new Stack<>();
        Boundary boundary = new Boundary();
        int badGridNum = 1;
        matrix.get(originator.getRow()).get(originator.getCol()).setLabel(label);
        stack.push(originator);
        while (!stack.empty()) {
            GridIndex cur = stack.pop();
            int neighbor = 0;

            List<GridIndex> offsets = new ArrayList<>();
            offsets.add(new GridIndex(1, 0));
            offsets.add(new GridIndex(0, 1));
            offsets.add(new GridIndex(-1, 0));
            offsets.add(new GridIndex(0, -1));
            for (GridIndex offset: offsets) {
                int rowIndex = cur.getRow()+offset.getRow();
                int colIndex = cur.getCol()+offset.getCol();
                if (matrix.containsKey(rowIndex) && matrix.get(rowIndex).containsKey(colIndex)) {
                    neighbor += 1;
                    if (matrix.get(rowIndex).get(colIndex).getLabel() == -1) {
                        matrix.get(rowIndex).get(colIndex).setLabel(label);
                        badGridNum += 1;
                        stack.push(new GridIndex(rowIndex, colIndex));
                    }
                }

            }

            if (neighbor != 4) {
                boundary.add(cur);
                if (cur.getCol() < minBoundaryCol) {
                    minBoundaryCol = cur.getCol();
                    boundary.setFirstBoundary(cur);
                }
            }
        }
        boundary.setGridNum(badGridNum);
        return boundary;
    }
    public Polygon generatePolygon(List<GridIndex> badArea) {
        Polygon polygon = new Polygon();
        List<Position> positions = new ArrayList<>();
        for (GridIndex gridIndex: badArea) {
            positions.add(matrix.get(gridIndex.getRow()).get(gridIndex.getCol()).getPosition());
            System.out.println(gridIndex.getRow() + " " + gridIndex.getCol());
        }

        polygon.setPoints(positions);
        return polygon;
    }
}
/**
 * Created by kele on 2017/9/13.
 */
public class BadAreaIdentify {
    private static final BadAreaIdentify badAreaIdentify = new BadAreaIdentify();

    private BadAreaIdentify() {
    }

    public static BadAreaIdentify getInstance() {
        return badAreaIdentify;
    }

    public List<Polygon> getBadAreas(List<Position> badGrids){
        List<Polygon> badAreas = new ArrayList<>();
        Matrix matrix = new Matrix(badGrids);
        int label = 0;
        GridIndex lastStart = matrix.getStart();
        while (true) {
            label += 1;
            GridIndex originator = matrix.findNexUntreated(lastStart);
            if (originator == null) {
                break;
            }
            lastStart = originator;
            Boundary boundary = matrix.generateBadArea(originator, label);
            if (boundary == null) {
                continue;
            }
            List<GridIndex>regularGridIndex = boundary.regularization();
            if (regularGridIndex != null) {
                Polygon badArea = matrix.generatePolygon(regularGridIndex);
                badAreas.add(badArea);
            }
        }

        return badAreas;
    }
}
