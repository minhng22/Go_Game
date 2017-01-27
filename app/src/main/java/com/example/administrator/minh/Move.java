package com.example.administrator.minh;

public class Move {

    private ButtonPair startingPair;
    private ButtonPair endingPair;
    private int score;

    public Move() {
        startingPair = null;
        endingPair = null;
        score = 0;
    }

    public void resetMove() {
        this.startingPair = null;
        this.endingPair = null;
        this.score = 0;
    }

    public void addStart(ButtonPair start) {
        this.startingPair = start;
    }

    public void addEnd(ButtonPair end) {
        this.endingPair = end;
    }

    public Move (Space start, Space end) {
        startingPair = new ButtonPair(null, start);
        endingPair = new ButtonPair(null, end);
        score = 0;
    }

    public void calculateScore(Space space, boolean isPlayer) {
        int score;
        int gained = 0;
        int saved = 0;
        int lost = 0;
        int losingRow;
        int losingCol;
        int row;
        int col;
        int startRow = this.getStartingPair().space.getRow();
        int startCol = this.getStartingPair().space.getCol();

        for (Location losingLoc : this.getStartingPair().space.getAdjacent()) {
            losingRow = losingLoc.getRow();
            losingCol = losingLoc.getCol();
            if (isPlayer) {
                if (FullscreenActivity.pairs[losingRow][losingCol].space.isClaimed()
                        && FullscreenActivity.pairs[losingRow][losingCol].space.isClaimedByPlayer()) {
                    lost += 1;
                }
            }
            else {
                if (FullscreenActivity.pairs[losingRow][losingCol].space.isClaimed()) {
                    lost += 1;
                }
            }

        }

        Space tempSpace;
        boolean isCopy = false;
        for (Location loc : space.getAdjacent()) {
            row = loc.getRow();
            col = loc.getCol();
            if (startRow == row && startCol == col) {
                isCopy = true;
            }
            tempSpace = FullscreenActivity.pairs[row][col].space;

            if (isPlayer) {
                if (tempSpace.isClaimed() && !tempSpace.isClaimedByPlayer()) {
                    gained += 1;
                }
                else if (tempSpace.isClaimedByPlayer()) {
                    saved += 1;
                }
            }
            else {
                if (tempSpace.isClaimedByPlayer()) {
                    gained += 1;
                }
                else if (tempSpace.isClaimed()) {
                    saved += 1;
                }
            }
        }

        //default
        if (FullscreenActivity.aiSetting == 1) {
            score = (saved * 3) + (gained * 7);

            if (!isCopy) {
                score -= (lost * 4);
            }
        }
        //aggressive
        else if (FullscreenActivity.aiSetting == 2) {
            score = (saved * 2) + (gained * 8);

            if (!isCopy) {
                score -= (lost * 3);
            }
        }
        //cautious
        else {
            score = (saved * 3) + (gained * 6);

            if (!isCopy) {
                score -= (lost * 6);
            }
        }

        this.score = score;
    }

    public ButtonPair getStartingPair() {
        return startingPair;
    }

    public ButtonPair getEndingPair() {
        return endingPair;
    }

    public int getScore() {
        return score;
    }
}
