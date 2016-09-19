package cs175fall2016.memorygame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    final static private ArrayList<Integer> IMAGE_ID = new ArrayList<>(Arrays.asList(
            R.drawable.oh_01, R.drawable.oh_02, R.drawable.oh_03, R.drawable.oh_04, R.drawable.oh_05,
            R.drawable.oh_06, R.drawable.oh_07, R.drawable.oh_08, R.drawable.oh_09, R.drawable.oh_10
    ));

    final static private int WAIT_TIME = 500;
    private SharedPreferences sharedPreferences;

    private ArrayList<Integer> tileImageIDs;
    private boolean[] isMatched;
    private int matchedCount;
    private boolean isGameFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Intent intent = getIntent();

        // If intent has values, restore values.
        if (intent.hasExtra("tileImageIDs") && intent.hasExtra("isMatched") && intent.hasExtra("matchedCount")) {
            Log.i("intent", "Passed intent and restore values.");
            tileImageIDs = intent.getIntegerArrayListExtra("tileImageIDs");
            isMatched = intent.getBooleanArrayExtra("isMatched");
            matchedCount = intent.getIntExtra("matchedCount", 0);
        }
        // else if saved state exists, restore state.
        else if (savedInstanceState != null) {
            Log.i("state", "Restored saved state.");
            tileImageIDs = savedInstanceState.getIntegerArrayList("tileImageIDs");
            isMatched = savedInstanceState.getBooleanArray("isMatched");
            matchedCount = savedInstanceState.getInt("matchedCount");
            isGameFinished = savedInstanceState.getBoolean("isGameFinished");
        }
        // else instantiate new values.
        else {
            Log.i("state", "Saved state does not exist.");
            tileImageIDs = new ArrayList<>();
            isMatched = new boolean[20];
            matchedCount = 0;
            isGameFinished = false;

            // Duplicate and shuffle images.
            tileImageIDs.addAll(IMAGE_ID);
            tileImageIDs.addAll(IMAGE_ID);
            Collections.shuffle(tileImageIDs);
        }

        initView();
    }

    /** Initialize view */
    private void initView() {
        // Set points text view.
        TextView textView = (TextView) findViewById(R.id.pointsText);
        textView.setText("Points: " + sharedPreferences.getInt("points", 0));

        // Set custom adapter to grid view.
        final GridView gridView = (GridView) findViewById(R.id.gridview_tiles);
        gridView.setAdapter(new ImageAdapter(this, tileImageIDs, isMatched));

        if (isGameFinished) {
            gameFinished(gridView);
        }

        // Attach click listener to grid view.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
            int compareIndex = -1;
            View compareView;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int indexOne = i;
                final int indexTwo = compareIndex;

                // Only check if tile not active and not matchedCount yet.
                if (indexOne != compareIndex && !isMatched[i]) {
                    final ImageView tileOneView = (ImageView) view;
                    tileOneView.setImageResource(tileImageIDs.get(indexOne));

                    // Compare tiles after one is active.
                    if (compareIndex >= 0) {
                        final ImageView tileTwoView = (ImageView) compareView;
                        gridView.setEnabled(false);

                        // If match, leave image as flipped.
                        if (tileImageIDs.get(indexOne).equals(tileImageIDs.get(indexTwo))) {
                            Log.i("game", "Match found.");
                            tileOneView.setImageResource(tileImageIDs.get(indexOne));
                            tileTwoView.setImageResource(tileImageIDs.get(indexTwo));
                            tileOneView.startAnimation(fadeOut);
                            tileTwoView.startAnimation(fadeOut);

                            gridView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tileOneView.setAlpha(0.4f);
                                    tileTwoView.setAlpha(0.4f);
                                    gridView.setEnabled(true);
                                }
                            }, WAIT_TIME);

                            isMatched[indexOne] = true;
                            isMatched[indexTwo] = true;
                            // Game over when all tiles matchedCount.
                            if (++matchedCount == 10) {
                                gameFinished(gridView);
                            }
                        } else { // If not match, reset images.
                            gridView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tileOneView.setImageResource(R.drawable.oh_00);
                                    tileTwoView.setImageResource(R.drawable.oh_00);
                                    gridView.setEnabled(true);
                                }
                            }, WAIT_TIME);
                        }
                        // Reset check values.
                        compareView = null;
                        compareIndex = -1;
                    } else { // No active tiles, mark current tile.
                        compareIndex = i;
                        compareView = view;
                    }
                }
            }
        });
    }

    /** Runs when game is isMatched */
    private void gameFinished(GridView gridView) {
        Log.i("game", "Game finished.");
        isGameFinished = true;
        ((TextView) findViewById(R.id.memoryGameText)).setText("You win!");

        // Update total points
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("points", sharedPreferences.getInt("points", 0) + matchedCount);
        editor.commit();
        ((TextView) findViewById(R.id.pointsText)).setText("Points: "+ sharedPreferences.getInt("points", 0));

        // Disable grid view touch
        gridView.setEnabled(false);
        final Animation fadeLoop = AnimationUtils.loadAnimation(this, R.anim.fadeloop);
        gridView.startAnimation(fadeLoop);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("state", "GamveActivity: State saved.");
        outState.putIntegerArrayList("tileImageIDs", tileImageIDs);
        outState.putBooleanArray("isMatched", isMatched);
        outState.putInt("matchedCount", matchedCount);
        outState.putBoolean("isGameFinished", isGameFinished);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        // If game is not finished, save game session.
        if (!isGameFinished) {
            Log.i("intent", "Saved intent on back pressed.");
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            intent.putExtra("tileImageIDs", tileImageIDs);
            intent.putExtra("isMatched", isMatched);
            intent.putExtra("matchedCount", matchedCount);
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
