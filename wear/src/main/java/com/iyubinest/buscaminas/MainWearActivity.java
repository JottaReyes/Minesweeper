package com.iyubinest.buscaminas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainWearActivity extends Activity {

    private final int x = 5, y = 5;
    private final int mines = 1;
    private final int minesToUnlock = x * y - mines;

    private Context mContext;
    private LinearLayout mLinearLayout;


    private boolean[][] board = new boolean[x][y];

    private int unlockedMines = 0;
    private boolean playable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main_wear);

        construcBoard();


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                createViews();
            }
        });
    }

    private void construcBoard() {
        playable = true;
        int minesToPut = mines;
        board = new boolean[x][y];
        do {
            int i = (int) (Math.random() * x);
            int j = (int) (Math.random() * y);
            if (!board[i][j]) {
                minesToPut--;
                board[i][j] = true;
            }
        } while (minesToPut > 0);
    }

    private int mineFinded(int i, int j) {
        if (board[i][j]) {
            return -1;
        } else {
            int mines = 0;
            for (int k = i - 1; k <= i + 1; k++) {
                for (int l = j - 1; l <= j + 1; l++) {
                    if (l >= 0 && k >= 0 && l < x && k < y) {
                        if (board[k][l]) {
                            mines++;
                        }
                    }
                }
            }
            return mines;
        }
    }

    private void createViews() {
        mLinearLayout = (LinearLayout) findViewById(R.id.mainWrapper);
        mLinearLayout.removeAllViews();
        Random rnd = new Random();
        for (int i = 0; i < x; i++) {
            LinearLayout linearLayout = new LinearLayout(mContext);

            for (int j = 0; j < y; j++) {
                TextView textView = new TextView(mContext);
                final int auxI = i;
                final int auxJ = j;
                int colora = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                textView.setBackgroundColor(colora);
                textView.setGravity(Gravity.CENTER);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (playable) {
                            int mines = mineFinded(auxI, auxJ);
                            if (mines == -1) {
                                ((TextView) v).setText(String.format("%s", "X"));
                                Toast.makeText(mContext, "You Lost!!", Toast.LENGTH_LONG).show();
                                restartGame();
                            } else {
                                ((TextView) v).setText(String.format("%s", mines));
                                unlockedMines++;
                                if (unlockedMines == minesToUnlock) {
                                    Toast.makeText(mContext, "You Win!!", Toast.LENGTH_LONG).show();
                                    restartGame();
                                }
                            }
                        }
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                linearLayout.addView(textView, params);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            mLinearLayout.addView(linearLayout, params);
        }
    }

    private void restartGame() {
        playable = false;
        unlockedMines = 0;
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                MainWearActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        construcBoard();
                        createViews();
                    }
                });

            }
        };

        TimerTask timerTaskPlayable = new TimerTask() {
            @Override
            public void run() {
                playable = true;
            }
        };
        timer.schedule(timerTask, 2000);
        timer.schedule(timerTaskPlayable, 2200);
    }

}