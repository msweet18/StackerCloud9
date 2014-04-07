package edu.msu.BlueSky.stacker;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

	private Game game;
	
	public GameView(Context context) {
		super(context);
		init(context);
	}
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return game.onTouchEvent(this, event);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		game.draw(canvas);
	}
	
	private void init(Context context) {
		game = new Game(context, this);
	}
	
	public Game getGame() {
        return game;
    }
	
	/**
	 * Save the puzzle to a bundle
	 * @param bundle The bundle we save to
	 */
	public void saveInstanceState(Bundle bundle) {
		game.saveInstanceState(bundle);
	}
	
	/**
	 * Load the puzzle from a bundle
	 * @param bundle The bundle we save to
	 */
	public void loadInstanceState(Bundle bundle) {
		game.loadInstanceState(bundle);
	}
	
	public void createNewBrick(int weight){
		game.createNewBrick(weight);
		this.invalidate();
	}
	public void EndGame(){
		((GameActivity)getContext()).onEndGame(game.getScores());
	}
}