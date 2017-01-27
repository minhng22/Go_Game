package com.example.administrator.minh;

public class Location {
    private int row;
    private int col;
    private boolean canCopy;

    public Location(int r, int c, boolean copy) {
        this.row = r;
        this.col = c;
        this.canCopy = copy;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean canCopy() {
        return canCopy;
    }
}
