package cs175fall2016.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Intent gameSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("intent", "Intent passed to Main Activity");
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                gameSession = data;
                ((Button) findViewById(R.id.newGameButton)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.playButton)).setText("Continue");
            } else if (resultCode == RESULT_CANCELED) {
                gameSession = null;
                ((Button) findViewById(R.id.newGameButton)).setVisibility(View.GONE);
                ((Button) findViewById(R.id.playButton)).setText("Play");
            }
        }
    }

    public void clickPlayButton(View view) {
        // If previous game data available, load data; else start new game.
        if (gameSession != null) {
            startActivityForResult(gameSession, 1);
        } else {
            startActivityForResult(new Intent(this, GameActivity.class), 1);
        }
    }

    public void clickNewGameButton(View view) {
        startActivityForResult(new Intent(this, GameActivity.class), 1);
    }

    public void clickRulesButton(View view) {
        startActivity(new Intent(getApplicationContext(), RulesActivity.class));
    }

}
