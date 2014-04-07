package edu.msu.BlueSky.stacker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class FinalScoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final_score);
		((TextView)this.findViewById(R.id.player1Name)).setText(getIntent().getStringExtra("player1Name"));
		((TextView)this.findViewById(R.id.player2Name)).setText(getIntent().getStringExtra("player2Name"));
		((TextView)this.findViewById(R.id.player1Score)).setText(String.valueOf(getIntent().getIntExtra("player1Score", 0)));
		((TextView)this.findViewById(R.id.player2Score)).setText(String.valueOf(getIntent().getIntExtra("player2Score", 0)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.final_score, menu);
		return true;
	}
}
