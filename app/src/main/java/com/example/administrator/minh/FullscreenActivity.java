package com.example.administrator.minh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private static Handler handler;
    public static ButtonPair[][] pairs;
    private static Move currentMove;
    private static ArrayList<Space> cpuSpaces;
    private static int userScore;
    private static int cpuScore;
    private static final int TOTAL_SPACES = 64;
    private static boolean isPlayerTurn;
    private static String[] hints;
    private final int NUM_HINTS = 7;
    private static int currHint;
    private static boolean hintsDone;
    private static boolean gameStart;
    public static int aiSetting;
    public static int startingPieces;
    private boolean firstAITurn;
    private boolean secondAITurn;
    private boolean thirdAITurn;
    private boolean fourthAITurn;
    public static int firstAICol;

    private int numWins;
    private int numLosses;
    private int numTies;
    private double winRate;
    private static final String PREFS_NAME = "SuppressionSettings";

    public Button endGame;
    public Button replay;
    public LinearLayout buttonBar;
    public ImageButton audioButton;

    public FrameLayout menuLayout;
    public FrameLayout settingsLayout;
    public FrameLayout gameLayout;

    public Button quickStart;
    public Button customGame;
    public Button quit;
    public Button startCustom;
    public Button backToMenu;
    public SeekBar aiBar;
    public SeekBar startingBar;
    public TextView aiDescription;
    public TextView startingDescription;

    public Drawable darkTile;
    public Drawable darkTileCpu;
    public Drawable darkTileCopy;
    public Drawable darkTileMove;
    public Drawable darkTilePlayer;
    public Drawable darkTileSelectCpu;
    public Drawable darkTileSelectPlayer;

    public Drawable lightTile;
    public Drawable lightTileCpu;
    public Drawable lightTileCopy;
    public Drawable lightTileMove;
    public Drawable lightTilePlayer;
    public Drawable lightTileSelectCpu;
    public Drawable lightTileSelectPlayer;

    public Drawable speaker;
    public Drawable mute;

    public SoundPool soundPool;
    public SparseIntArray soundMap;
    private AudioThread audioThread;
    private boolean muted;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;
    public TextView userText;
    public TextView cpuText;
    public TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this.getApplicationContext();
        setContentView(R.layout.activity_fullscreen);

        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            muted = prefs.getBoolean("isMuted", false);
        } catch (Exception e) {
            muted = false;
        }

        try {
            numWins = prefs.getInt("wins", 0);
        } catch (Exception e) {
            numWins = 0;
        }

        try {
            numLosses = prefs.getInt("losses", 0);
        } catch (Exception e) {
            numLosses = 0;
        }

        try {
            numTies = prefs.getInt("ties", 0);
        } catch (Exception e) {
            numTies = 0;
        }

        winRate = calculateWinRate();

        menuLayout = (FrameLayout) findViewById(R.id.menuLayout);
        settingsLayout = (FrameLayout) findViewById(R.id.settingsLayout);
        gameLayout = (FrameLayout) findViewById(R.id.gameLayout);

        menuLayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.INVISIBLE);
        gameLayout.setVisibility(View.INVISIBLE);

        quickStart = (Button) findViewById(R.id.quickStartButton);
        customGame = (Button) findViewById(R.id.newGameButton);
        quit = (Button) findViewById(R.id.quitButton);


        startCustom = (Button) findViewById(R.id.customStart);
        backToMenu = (Button) findViewById(R.id.backButton);
        aiBar = (SeekBar) findViewById(R.id.aiBar);
        startingBar = (SeekBar) findViewById(R.id.startingBar);
        aiDescription = (TextView) findViewById(R.id.aiDescription);
        startingDescription = (TextView) findViewById(R.id.startingDescription);

        attachMenuListeners();
        attachSettingsListeners();

        darkTile = getResources().getDrawable(R.drawable.darktilesmall);
        darkTileCpu = getResources().getDrawable(R.drawable.darktilecpusmall);
        darkTileCopy = getResources().getDrawable(R.drawable.darktilecopysmall);
        darkTileMove = getResources().getDrawable(R.drawable.darktilemovesmall);
        darkTilePlayer = getResources().getDrawable(R.drawable.darktileplayersmall);
        darkTileSelectCpu = getResources().getDrawable(R.drawable.darktileselectedcpusmall);
        darkTileSelectPlayer = getResources().getDrawable(R.drawable.darktileselectedplayersmall);

        lightTile = getResources().getDrawable(R.drawable.lighttilesmall);
        lightTileCpu = getResources().getDrawable(R.drawable.lighttilecpusmall);
        lightTileCopy = getResources().getDrawable(R.drawable.lighttilecopysmall);
        lightTileMove = getResources().getDrawable(R.drawable.lighttilemovesmall);
        lightTilePlayer = getResources().getDrawable(R.drawable.lighttileplayersmall);
        lightTileSelectCpu = getResources().getDrawable(R.drawable.lighttileselectedcpusmall);
        lightTileSelectPlayer = getResources().getDrawable(R.drawable.lighttileselectedplayersmall);

        mute = getResources().getDrawable(R.drawable.mute);
        speaker = getResources().getDrawable(R.drawable.speaker);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        userText = (TextView) findViewById(R.id.userText);
        cpuText = (TextView) findViewById(R.id.cpuText);
        infoText = (TextView) findViewById(R.id.infoText);
        endGame = (Button) findViewById(R.id.endButton);
        replay = (Button) findViewById(R.id.replayButton);
        buttonBar = (LinearLayout) findViewById(R.id.buttonBar);
        audioButton = (ImageButton) findViewById(R.id.musicButton);

        audioThread = new AudioThread(context);
        audioThread.run();

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundMap = new SparseIntArray(2);
        soundMap.put(0, soundPool.load(context, R.raw.click1, 1));
        soundMap.put(1, soundPool.load(context, R.raw.click2, 1));

        endGame.setClickable(false);
        replay.setClickable(false);
        buttonBar.setVisibility(View.INVISIBLE);

        startingPieces = 1;
        aiSetting = 1;
        muted = false;

        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (muted) {
                    audioThread.mediaPlayer.start();
                    audioButton.setImageDrawable(speaker);
                    muted = false;
                }
                else {
                    audioThread.mediaPlayer.pause();
                    audioButton.setImageDrawable(mute);
                    muted = true;
                }
                audioButton.setScaleType(ImageView.ScaleType.FIT_XY);
                audioButton.setAdjustViewBounds(true);
            }
        });


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        pairs = assignButtons();
        handler = new Handler();
    }

    private double calculateWinRate() {
        int denominator = numWins + numLosses + numTies;
        double rate;
        if (denominator != 0) {
            rate = numWins / (numWins + numLosses + numTies);
        }
        else {
            rate = 0.0;
        }
        return rate;
    }

    private void attachSettingsListeners() {
        aiBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    aiDescription.setText(getResources().getText(R.string.cautious));
                }
                else if (progress == 1) {
                    aiDescription.setText(getResources().getText(R.string.strategic));
                }
                else {
                    aiDescription.setText(getResources().getText(R.string.aggressive));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        startingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    startingDescription.setText(getResources().getText(R.string.one));
                } else {
                    startingDescription.setText(getResources().getText(R.string.two));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsLayout.setVisibility(View.INVISIBLE);
                menuLayout.setVisibility(View.VISIBLE);
            }
        });

        startCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currAiSetting = aiBar.getProgress();
                int currStartSetting = startingBar.getProgress();

                aiSetting = currAiSetting;
                startingPieces = currStartSetting + 1;

                resetBoard();

                settingsLayout.setVisibility(View.INVISIBLE);
                gameLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void attachMenuListeners() {
        quickStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.setVisibility(View.INVISIBLE);
                gameLayout.setVisibility(View.VISIBLE);
                aiSetting = 1;
                startingPieces = 1;
                resetBoard();
            }
        });

        customGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.setVisibility(View.INVISIBLE);
                settingsLayout.setVisibility(View.VISIBLE);
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (audioThread.mediaPlayer.isPlaying()) {
            audioThread.mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!audioThread.mediaPlayer.isPlaying() && !muted) {
            audioThread.mediaPlayer.start();
        }
    }

    private void playSoundEffect() {
        double rand = Math.random();
        int num;
        if (rand <= 0.4) {
            num = 0;
        }
        else {
            num = 1;
        }

        if (num == 0) {
            soundPool.play(soundMap.get(0), 1.0f, 1.0f, 1, 0, 1.0f);
        } else {
            soundPool.play(soundMap.get(1), 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    private void resetBoard() {
        endGame.setClickable(false);
        replay.setClickable(false);
        buttonBar.setVisibility(View.INVISIBLE);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pairs[i][j].space.setCanCopy(false);
                pairs[i][j].space.setCanMove(false);
                pairs[i][j].space.setIsClaimedByPlayer(false);
                pairs[i][j].space.setIsClaimed(false);
                pairs[i][j].space.setIsSelected(false);
                pairs[i][j].space.setIsUnavailable(false);
            }
        }
        pairs[0][0].space.setIsClaimed(true);
        pairs[0][0].space.setIsClaimedByPlayer(true);
        pairs[7][7].space.setIsClaimed(true);

        if (startingPieces != 1) {
            pairs[0][7].space.setIsClaimed(true);
            pairs[0][7].space.setIsClaimedByPlayer(true);
            pairs[7][0].space.setIsClaimed(true);
        }

        userScore = startingPieces;
        cpuScore = startingPieces;

        generateHints();

        if (startingPieces == 2) {
            firstAITurn = true;
        }
        else {
            firstAITurn = false;
        }
        secondAITurn = false;
        thirdAITurn = false;
        fourthAITurn = false;
        gameStart = true;
        hintsDone = false;
        currHint = 0;
        currentMove = new Move();
        cpuSpaces = new ArrayList<Space>();
        isPlayerTurn = true;
        enableButtons();
        userText.setText(Integer.toString(userScore));
        cpuText.setText(Integer.toString(cpuScore));
        updateDisplay();
        infoText.setText(hints[currHint]);
        currHint++;
    }

    private void generateHints() {
        hints = new String[NUM_HINTS];
        if (startingPieces == 1) {
            hints[0] = "You start at the bottom left corner.  To begin, click your blue piece.";
        }
        else {
            hints[0] = "You start in the bottom row.  To begin, click one of your blue pieces.";
        }
        hints[1] = "The blue spaces are all the locations you can move to.  You can copy your piece to a light blue space, or jump to a dark blue one.";
        hints[2] = "If you move next to an enemy piece, all of the neighboring pieces will become yours.";
        hints[3] = "But be careful - the same rule applies for your opponent.";
        hints[4] = "When the board is filled, or either player has no pieces remaining, the game is over.";
        hints[5] = "Whoever has the most pieces at the end of the game wins!";
        hints[6] = "";
    }

    private void showNextHint() {
        infoText.setText(hints[currHint]);
        currHint++;
        if (currHint >= NUM_HINTS) {
            hintsDone = true;
        }
    }

    private ButtonPair[][] assignButtons() {
        ButtonPair[][] pairs = new ButtonPair[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pairs[i][j] = new ButtonPair();
            }
        }

        pairs[0][0].button = (ImageButton) findViewById(R.id.btn00);
        pairs[0][1].button = (ImageButton) findViewById(R.id.btn01);
        pairs[0][2].button = (ImageButton) findViewById(R.id.btn02);
        pairs[0][3].button = (ImageButton) findViewById(R.id.btn03);
        pairs[0][4].button = (ImageButton) findViewById(R.id.btn04);
        pairs[0][5].button = (ImageButton) findViewById(R.id.btn05);
        pairs[0][6].button = (ImageButton) findViewById(R.id.btn06);
        pairs[0][7].button = (ImageButton) findViewById(R.id.btn07);

        pairs[1][0].button = (ImageButton) findViewById(R.id.btn08);
        pairs[1][1].button = (ImageButton) findViewById(R.id.btn09);
        pairs[1][2].button = (ImageButton) findViewById(R.id.btn10);
        pairs[1][3].button = (ImageButton) findViewById(R.id.btn11);
        pairs[1][4].button = (ImageButton) findViewById(R.id.btn12);
        pairs[1][5].button = (ImageButton) findViewById(R.id.btn13);
        pairs[1][6].button = (ImageButton) findViewById(R.id.btn14);
        pairs[1][7].button = (ImageButton) findViewById(R.id.btn15);

        pairs[2][0].button = (ImageButton) findViewById(R.id.btn16);
        pairs[2][1].button = (ImageButton) findViewById(R.id.btn17);
        pairs[2][2].button = (ImageButton) findViewById(R.id.btn18);
        pairs[2][3].button = (ImageButton) findViewById(R.id.btn19);
        pairs[2][4].button = (ImageButton) findViewById(R.id.btn20);
        pairs[2][5].button = (ImageButton) findViewById(R.id.btn21);
        pairs[2][6].button = (ImageButton) findViewById(R.id.btn22);
        pairs[2][7].button = (ImageButton) findViewById(R.id.btn23);

        pairs[3][0].button = (ImageButton) findViewById(R.id.btn24);
        pairs[3][1].button = (ImageButton) findViewById(R.id.btn25);
        pairs[3][2].button = (ImageButton) findViewById(R.id.btn26);
        pairs[3][3].button = (ImageButton) findViewById(R.id.btn27);
        pairs[3][4].button = (ImageButton) findViewById(R.id.btn28);
        pairs[3][5].button = (ImageButton) findViewById(R.id.btn29);
        pairs[3][6].button = (ImageButton) findViewById(R.id.btn30);
        pairs[3][7].button = (ImageButton) findViewById(R.id.btn31);

        pairs[4][0].button = (ImageButton) findViewById(R.id.btn32);
        pairs[4][1].button = (ImageButton) findViewById(R.id.btn33);
        pairs[4][2].button = (ImageButton) findViewById(R.id.btn34);
        pairs[4][3].button = (ImageButton) findViewById(R.id.btn35);
        pairs[4][4].button = (ImageButton) findViewById(R.id.btn36);
        pairs[4][5].button = (ImageButton) findViewById(R.id.btn37);
        pairs[4][6].button = (ImageButton) findViewById(R.id.btn38);
        pairs[4][7].button = (ImageButton) findViewById(R.id.btn39);

        pairs[5][0].button = (ImageButton) findViewById(R.id.btn40);
        pairs[5][1].button = (ImageButton) findViewById(R.id.btn41);
        pairs[5][2].button = (ImageButton) findViewById(R.id.btn42);
        pairs[5][3].button = (ImageButton) findViewById(R.id.btn43);
        pairs[5][4].button = (ImageButton) findViewById(R.id.btn44);
        pairs[5][5].button = (ImageButton) findViewById(R.id.btn45);
        pairs[5][6].button = (ImageButton) findViewById(R.id.btn46);
        pairs[5][7].button = (ImageButton) findViewById(R.id.btn47);

        pairs[6][0].button = (ImageButton) findViewById(R.id.btn48);
        pairs[6][1].button = (ImageButton) findViewById(R.id.btn49);
        pairs[6][2].button = (ImageButton) findViewById(R.id.btn50);
        pairs[6][3].button = (ImageButton) findViewById(R.id.btn51);
        pairs[6][4].button = (ImageButton) findViewById(R.id.btn52);
        pairs[6][5].button = (ImageButton) findViewById(R.id.btn53);
        pairs[6][6].button = (ImageButton) findViewById(R.id.btn54);
        pairs[6][7].button = (ImageButton) findViewById(R.id.btn55);

        pairs[7][0].button = (ImageButton) findViewById(R.id.btn56);
        pairs[7][1].button = (ImageButton) findViewById(R.id.btn57);
        pairs[7][2].button = (ImageButton) findViewById(R.id.btn58);
        pairs[7][3].button = (ImageButton) findViewById(R.id.btn59);
        pairs[7][4].button = (ImageButton) findViewById(R.id.btn60);
        pairs[7][5].button = (ImageButton) findViewById(R.id.btn61);
        pairs[7][6].button = (ImageButton) findViewById(R.id.btn62);
        pairs[7][7].button = (ImageButton) findViewById(R.id.btn63);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pairs[i][j].space = new Space(i, j);
            }
        }
        pairs[0][0].space.setIsClaimed(true);
        pairs[0][0].space.setIsClaimedByPlayer(true);
        pairs[7][7].space.setIsClaimed(true);

        userScore = 1;
        cpuScore = 1;

        for (int i = 0; i < 8; i++) {
            for (ButtonPair pair : pairs[i]) {
                pair = addListener(pair);
            }
        }

        return pairs;
    }

    private ButtonPair addListener(ButtonPair pair) {
        final int row = pair.space.getRow();
        final int col = pair.space.getCol();
        pair.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSpace(row, col);
            }
        });
        return pair;
    }

    private void selectSpace(int row, int col) {

        if (isPlayerTurn) {
            if (pairs[row][col].space.isClaimedByPlayer()) {
                deselect();
                if (gameStart) {
                    showNextHint();
                    gameStart = false;
                }
                currentMove.addStart(pairs[row][col]);
                markSpacesForHighlighting(pairs[row][col].space);
            }
            else if (((pairs[row][col].space.canMove())
                    || (pairs[row][col].space.canCopy()))
                    && (!pairs[row][col].space.isClaimed())) {
                disableButtons();
                currentMove.addEnd(pairs[row][col]);
                currentMove.calculateScore(pairs[row][col].space, true);
                move(currentMove);
            }
            else {
                deselect();
                currentMove.resetMove();
            }
        }
        else {
            if (pairs[row][col].space.isClaimed() && !pairs[row][col].space.isClaimedByPlayer()) {
                deselect();
                currentMove.addStart(pairs[row][col]);
                markSpacesForHighlighting(pairs[row][col].space);
            }
            else if (((pairs[row][col].space.canMove())
                    || (pairs[row][col].space.canCopy()))
                    && (!pairs[row][col].space.isClaimed())) {
                disableButtons();
                currentMove.addEnd(pairs[row][col]);
                currentMove.calculateScore(pairs[row][col].space, false);
                move(currentMove);
            }
            else {
                deselect();
                currentMove.resetMove();
            }
        }

    }

    private void markSpacesForHighlighting(Space space) {
        pairs[space.getRow()][space.getCol()].space.setIsSelected(true);
        for (Location loc : space.getMovable()) {
            int row = loc.getRow();
            int col = loc.getCol();
            if (!pairs[row][col].space.isClaimed()) {
                if (loc.canCopy()) {
                    pairs[row][col].space.setCanCopy(true);
                }
                pairs[row][col].space.setCanMove(true);
            }
        }
        updateDisplay();
    }

    private void updateScores() {
        userText.setText(Integer.toString(userScore));
        cpuText.setText(Integer.toString(cpuScore));
    }


    private void move(Move move) {

        final int startRow = move.getStartingPair().space.getRow();
        final int startCol = move.getStartingPair().space.getCol();
        final boolean endCanCopy = move.getEndingPair().space.canCopy();
        final int endRow = move.getEndingPair().space.getRow();
        final int endCol = move.getEndingPair().space.getCol();

        if (isPlayerTurn && !hintsDone) {
            showNextHint();
        }

        if (hintsDone && !infoText.getText().equals(hints[6])) {
            infoText.setText(hints[6]);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!endCanCopy) {
                    pairs[startRow][startCol].space.setIsClaimedByPlayer(false);
                    pairs[startRow][startCol].space.setIsClaimed(false);

                    if (isPlayerTurn) {
                        userScore -= 1;
                    }
                    else {
                        cpuScore -= 1;
                    }
                    playSoundEffect();
                }

                deselect();
                updateDisplay();
            }
        }, 50);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pairs[endRow][endCol].space.setIsClaimed(true);
                if (isPlayerTurn) {
                    pairs[endRow][endCol].space.setIsClaimedByPlayer(true);
                    userScore += 1;
                } else {
                    pairs[endRow][endCol].space.setIsClaimedByPlayer(false);
                    cpuScore += 1;
                }
                playSoundEffect();

                updateScores();
                updateDisplay();
            }
        }, 100);

        int multiplier = 0;
        for (Location loc : pairs[endRow][endCol].space.getAdjacent()) {
            multiplier++;
            final int y = loc.getRow();
            final int x = loc.getCol();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isPlayerTurn) {
                        if (pairs[y][x].space.isClaimed() && !pairs[y][x].space.isClaimedByPlayer()) {
                            pairs[y][x].space.setIsClaimedByPlayer(true);
                            userScore += 1;
                            cpuScore -= 1;
                            playSoundEffect();
                        }
                    }
                    else {
                        if (pairs[y][x].space.isClaimed() && pairs[y][x].space.isClaimedByPlayer()) {
                            pairs[y][x].space.setIsClaimedByPlayer(false);
                            userScore -= 1;
                            cpuScore += 1;
                            playSoundEffect();
                        }
                    }
                    updateDisplay();
                    updateScores();
                }
            }, 200 + (multiplier * 50));
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                endTurn();
            }
        }, 300 + (multiplier * 50));
    }

    private void endTurn() {
        currentMove = new Move();
        cpuSpaces.clear();

        boolean playerHasMoves = analyzePlayerMoves();
        boolean cpuHasMoves = analyzeCPUMoves();

        if (!playerHasMoves && !cpuHasMoves) {
            gameOver();
        }
        else if ((userScore + cpuScore >= TOTAL_SPACES)
                || (userScore <= 0) || (cpuScore <= 0)) {
            gameOver();
        }
        else if (!playerHasMoves && !isPlayerTurn) {
            infoText.setText("You have no available moves.\nYou miss this turn.");
            isPlayerTurn = false;
            cpuTurn();
        }
        else if (!cpuHasMoves && isPlayerTurn) {
            infoText.setText("The computer has no available moves.\nYou get another turn.");
            isPlayerTurn = true;
            enableButtons();
        }
        else if (isPlayerTurn) {
            isPlayerTurn = false;
            cpuTurn();
        }
        else {
            isPlayerTurn = true;
            enableButtons();
        }
    }

    private boolean analyzePlayerMoves() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pairs[i][j].space.isClaimedByPlayer()) {
                    for (Location loc : pairs[i][j].space.getMovable()) {
                        if (!pairs[loc.getRow()][loc.getCol()].space.isClaimed()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean analyzeCPUMoves() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((!pairs[i][j].space.isClaimedByPlayer())
                        && (pairs[i][j].space.isClaimed())) {
                    for (Location loc : pairs[i][j].space.getMovable()) {
                        if (!pairs[loc.getRow()][loc.getCol()].space.isClaimed()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void updateDisplay() {
        boolean isDark;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i % 2 == j % 2) {
                    isDark = true;
                }
                else {
                    isDark = false;
                }

                if (pairs[i][j].space.isUnavailable()) {
                    pairs[i][j].button.setBackgroundColor(Color.DKGRAY);
                }
                else if (pairs[i][j].space.isSelected()) {
                    if (pairs[i][j].space.isClaimedByPlayer()) {
                        if (isDark) {
                            if (pairs[i][j].button.getDrawable() != darkTileSelectPlayer) {
                                pairs[i][j].button.setImageDrawable(darkTileSelectPlayer);
                            }
                        } else {
                            if (pairs[i][j].button.getDrawable() != lightTileSelectPlayer) {
                                pairs[i][j].button.setImageDrawable(lightTileSelectPlayer);
                            }
                        }
                    }
                    else {
                        if (isDark) {
                            if (pairs[i][j].button.getDrawable() != darkTileSelectCpu) {
                                pairs[i][j].button.setImageDrawable(darkTileSelectCpu);
                            }
                        } else {
                            if (pairs[i][j].button.getDrawable() != lightTileSelectCpu) {
                                pairs[i][j].button.setImageDrawable(lightTileSelectCpu);
                            }
                        }
                    }
                }
                else if (pairs[i][j].space.isClaimedByPlayer()) {
                    if (isDark) {
                        if (pairs[i][j].button.getDrawable() != darkTilePlayer) {
                            pairs[i][j].button.setImageDrawable(darkTilePlayer);
                        }
                    } else {
                        if (pairs[i][j].button.getDrawable() != lightTilePlayer) {
                            pairs[i][j].button.setImageDrawable(lightTilePlayer);
                        }
                    }
                }
                else if (pairs[i][j].space.isClaimed()) {
                    if (isDark) {
                        if (pairs[i][j].button.getDrawable() != darkTileCpu) {
                            pairs[i][j].button.setImageDrawable(darkTileCpu);
                        }
                    } else {
                        if (pairs[i][j].button.getDrawable() != lightTileCpu) {
                            pairs[i][j].button.setImageDrawable(lightTileCpu);
                        }
                    }
                }
                else if (pairs[i][j].space.canCopy()) {
                    if (isDark) {
                        if (pairs[i][j].button.getDrawable() != darkTileCopy) {
                            pairs[i][j].button.setImageDrawable(darkTileCopy);
                        }
                    } else {
                        if (pairs[i][j].button.getDrawable() != lightTileCopy) {
                            pairs[i][j].button.setImageDrawable(lightTileCopy);
                        }
                    }
                }
                else if (pairs[i][j].space.canMove()) {
                    if (isDark) {
                        if (pairs[i][j].button.getDrawable() != darkTileMove) {
                            pairs[i][j].button.setImageDrawable(darkTileMove);
                        }
                    } else {
                        if (pairs[i][j].button.getDrawable() != lightTileMove) {
                            pairs[i][j].button.setImageDrawable(lightTileMove);
                        }
                    }
                }
                else {
                    if (isDark) {
                        if (pairs[i][j].button.getDrawable() != darkTile) {
                            pairs[i][j].button.setImageDrawable(darkTile);
                        }
                    } else {
                        if (pairs[i][j].button.getDrawable() != lightTile) {
                            pairs[i][j].button.setImageDrawable(lightTile);
                        }
                    }
                }
                pairs[i][j].button.setScaleType(ImageView.ScaleType.FIT_XY);
                pairs[i][j].button.setAdjustViewBounds(true);
            }
        }
    }

    private void cpuTurn() {
        deselect();
        int highScore = -63;
        int currScore = -63;
        Location bestStart = new Location(0, 0, false);
        Location bestEnd = new Location(0, 0, false);
        cpuSpaces = new ArrayList<Space>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pairs[i][j].space.isClaimed() && !pairs[i][j].space.isClaimedByPlayer()) {
                    cpuSpaces.add(pairs[i][j].space);
                }
            }
        }

        for (Space space : cpuSpaces) {
            markSpacesForHighlighting(space);
            for (Location loc : space.getMovable()) {
                int row = loc.getRow();
                int col = loc.getCol();

                if (pairs[row][col].space.canMove()) {
                    currentMove = new Move(space, pairs[row][col].space);
                    currentMove.calculateScore(pairs[row][col].space, false);
                    currScore = currentMove.getScore();

                    if (fourthAITurn) {
                        if (firstAICol == 0) {
                            if (space.getCol() != 0) {
                                currScore = -63;
                            }
                        }
                        else {
                            if (space.getCol() != 7) {
                                currScore = -63;
                            }
                        }
                    }

                    if (secondAITurn || thirdAITurn) {
                        if (firstAICol == 0) {
                            if (space.getCol() != 7) {
                                currScore = -63;
                            }
                        }
                        else {
                            if (space.getCol() != 0) {
                                currScore = -63;
                            }
                        }
                    }

                    if (currScore > highScore) {
                        highScore = currScore;
                        bestStart = new Location(space.getRow(), space.getCol(), false);
                        bestEnd = new Location(loc.getRow(), loc.getCol(), loc.canCopy());
                    }
                }
            }
        }

        final int startRow = bestStart.getRow();
        final int startCol = bestStart.getCol();
        final int endRow = bestEnd.getRow();
        final int endCol = bestEnd.getCol();

        if (fourthAITurn) {
            fourthAITurn = false;
        }
        else if (thirdAITurn) {
            thirdAITurn = false;
            fourthAITurn = true;
        }
        else if (secondAITurn) {
            secondAITurn = false;
            thirdAITurn = true;
        }
        else if (firstAITurn) {
            firstAICol = startCol;
            firstAITurn = false;
            secondAITurn = true;
        }

        deselect();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectSpace(startRow, startCol);
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectSpace(endRow, endCol);
            }
        }, 1000);
    }

    private void gameOver() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        int rate;
        String endInfo;
        if (userScore > cpuScore) {
            endInfo = "You win!";
            numWins ++;
            editor.putInt("wins", numWins);

        }
        else if (userScore < cpuScore) {
            endInfo = "You lose.";
            numLosses ++;
            editor.putInt("losses", numLosses);
        }
        else {
            endInfo = "The game was a draw.";
            numTies ++;
            editor.putInt("ties", numTies);
        }
        editor.commit();
        winRate = calculateWinRate();
        rate = (int) (winRate * 100);
        endInfo += "\nOverall win rate: " + rate + "%";
        endInfo += "\nWould you like to play again?";

        infoText.setText(endInfo);

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBoard();
            }
        });

        endGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameLayout.setVisibility(View.INVISIBLE);
                menuLayout.setVisibility(View.VISIBLE);
            }
        });

        buttonBar.setVisibility(View.VISIBLE);
        endGame.setClickable(true);
        replay.setClickable(true);
    }

    private void disableButtons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pairs[i][j].button.setClickable(false);
            }
        }
    }

    private void enableButtons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pairs[i][j].button.setClickable(true);
            }
        }
    }

    private void deselect() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pairs[i][j].space.deselect();
            }
        }
        updateDisplay();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isMuted", muted);

        editor.commit();
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}