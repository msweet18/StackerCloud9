package edu.msu.BlueSky.stacker;

import java.util.ArrayList;

import android.R.string;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Game {
	
	/**
	 * The name of the bundle keys to save the game
	 */
	private final static String XLOCATIONS = "Game.xLocations";
	
	private final static String WEIGHTS = "Game.weights";
	
	private final static int scoreToWin = 5;
	
	
	/**
	 * Collection of Bricks
	 */
	public ArrayList<Brick> bricks = new ArrayList<Brick>();
	
    /**
     * How much we scale the bricks
     */
    private float scaleFactor;
	
	//strings of the names...might make a player class if needed
	public string Player1;
	public string Player2;
	
	private GameView gameView;
	
	private int screenWidth;
	private int screenHeight;
	
	/**
     * This variable is set to a brick we are dragging. If
     * we are not dragging, the variable is null.
     */
    private Brick dragging = null;
    
    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;
    
    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;
    
    private Context context;
    
    private boolean brickIsSet = true;
    
    private int yOffset = 0;
    
    private int brickHeight = 0;
    
    private boolean player1MoveFirst = true;
    private boolean player1Move = true;
    
    private int player1Score;
    private int player2Score;
    
    private float total;

	private float variance;

	private float maxX;

	private float minX;

	private int Tmass;

	private double massPosition;

	public Game(Context c, GameView view){
		context = c;
		gameView = view;
	}
	
	public void draw(Canvas canvas){
		screenWidth = canvas.getWidth();
		screenHeight = canvas.getHeight();
		
		int wid = Resources.getSystem().getDisplayMetrics().widthPixels;
		int hit = Resources.getSystem().getDisplayMetrics().heightPixels;
		
		// Determine the minimum of the two dimensions
		int minDim = wid < hit ? wid : hit;
		
		scaleFactor = (float)screenWidth/minDim;
				
		for(Brick brick : bricks){
			brick.draw(canvas, bricks.indexOf(brick), scaleFactor, yOffset);
		}
	}
	
	/**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {

    	float relX = (event.getX()/screenWidth);
    	float relY = (event.getY()/screenHeight);
    	
    	switch (event.getActionMasked()) {

        case MotionEvent.ACTION_DOWN:
        	return onTouched(relX, relY);
        case MotionEvent.ACTION_UP:
        	if(dragging != null){
        		return onReleased();
        	}
        case MotionEvent.ACTION_CANCEL:
        	Log.i("action cancel", "dragging set to null");
        	if(dragging != null) {
        		dragging = null;
                return true;
            }
        case MotionEvent.ACTION_MOVE:
        	if(dragging != null)
        	{
        		dragging.move(relX-lastRelX);
        		Log.i("dragging", "top brick is being dragged");
        		lastRelX = relX;
        		view.invalidate();
        		return true;
        	}
        	else{
        		yOffset -= (int)((relY-lastRelY)*screenHeight);
        		if(yOffset>0)
        		{
        			yOffset=0;
        		}
        		Log.i("scrolling", "lastRelY: "+lastRelY);
        		Log.i("scrolling", "relY: "+relY);
        		Log.i("scrolling", "yOffset: "+yOffset);
        		lastRelY = relY;
        		view.invalidate();
        		return true;
        	}
        }
        return false;
    }
    
    private boolean onReleased() {
		// TODO Auto-generated method stub
    	if(dragging != null){
    		Log.i("on released", "dragging set to null");
    		dragging = null;
    		return true;
    	}
		return false;
	}

	/**
	 * Save the puzzle to a bundle
	 * @param bundle The bundle we save to
	 */
	public void saveInstanceState(Bundle bundle) {
		float [] xLocations = new float[bricks.size()];
		int [] weights = new int[bricks.size()];
		
		for(int i=0;  i<bricks.size(); i++) {
			Brick brick = bricks.get(i);
			xLocations[i] = brick.getX();
			weights[i] = brick.getWeight();
		}
		bundle.putFloatArray(XLOCATIONS, xLocations);
		bundle.putIntArray(WEIGHTS, weights);
	}
	
	/**
	 * Read the puzzle from a bundle
	 * @param bundle The bundle we save to
	 */
	public void loadInstanceState(Bundle bundle) {
		float [] xLocations = bundle.getFloatArray(XLOCATIONS);
		Log.i("load", "length: "+XLOCATIONS.length());
		for(int i=0; i<xLocations.length; i++){
			bricks.add(new Brick(context, (i%2==0), 0));
			bricks.get(i).setX(xLocations[i]);
		}
	}
	
	private boolean onTouched(float x, float y){
		Log.i("touched", x+" "+y);
		if(bricks.size()>0){
			Brick topBrick = bricks.get(bricks.size()-1); 		
			if(topBrick.hit(x, y+((float)yOffset/screenHeight), screenWidth, screenHeight, scaleFactor)) {
	            // We hit a piece!
	        	dragging = topBrick;
	        	Log.i("dragging", "dragging set to top brick");
	            lastRelX = x;
	            return true;
	        }
		}
		lastRelY = y;
		return true;
	}
	public void createNewBrick(int weight){
		if(brickIsSet){
			bricks.add(new Brick(context, (bricks.size()%2==0), weight));
			if(bricks.size()>1){
				bricks.get(bricks.size()-1).setX(bricks.get(bricks.size()-2).getX());
			}
			Log.i("bricks", "Size: "+bricks.size());
			brickIsSet = false;
		}
		brickHeight = (int)(bricks.get(0).getHeight()*scaleFactor);
		if((int)(screenHeight*.7)-brickHeight*bricks.size()<0){
			yOffset = (int)(screenHeight*.7)-brickHeight*bricks.size();
		}
		else{
			yOffset = 0;
		}
			
	}
	
	public void setBrick(){
		//check balance
		if(!isBallanced())
		{
			EndRound();
		}
		brickIsSet = true;
	}
	
	public int getBottomUpTotalMass(int size){
		for(int ii=1;ii<=size;ii++)
		{
			Tmass += bricks.get(ii).getWeight();
		}
		return Tmass;
	}

	public int getTopDownTotalMass(int size){
		for(int ii=size;ii<bricks.size();ii++)
		{
			Tmass += bricks.get(ii).getWeight();
		}
		return Tmass;
	}

	public boolean isBallanced(){
		if(bricks.size()<=1)
		{
			return true;
		}
		variance = (bricks.get(0).getBrickWidth()*scaleFactor)/2;
		Log.i("maxX", Float.toString(maxX));
		Log.i("minX", Float.toString(minX));

		for(int ii=bricks.size()-1;ii>0;ii--)
		{
			int xx = ii;
			Tmass = getTopDownTotalMass(ii);
			Log.i("MAssMassMass", Integer.toString(Tmass));
			maxX = (bricks.get(ii-1).getX()*screenWidth) + variance;
			minX = (bricks.get(ii-1).getX()*screenWidth) - variance;

			while(xx<bricks.size())
			{
				float tempWeight = bricks.get(xx).getWeight();
				float tempXpos = bricks.get(xx).getX()*screenWidth;

				total += (tempWeight * tempXpos);
				xx++;
			}
			massPosition = (1.0/Tmass)*total;
			Log.i("total", Float.toString(total));
			Log.i("total", Float.toString(Tmass));
			Log.i("total", Double.toString(1.0/Tmass));
			Log.i("xxxxxxxxxxxx", Double.toString(massPosition));

			if (massPosition > maxX || massPosition < minX)
			{
				Log.i("FAilED", "Failfailfailfail");
				massPosition = 0;
				return false;
			}


			Tmass = 0;
			total = 0;

		}

		return true;

	}
	
	public void EndRound()
	{
		if(player1Move){
			player2Score++;
		}
		else{
			player1Score++;
		}
		if(player1Score>=scoreToWin || player2Score>=scoreToWin){
			EndGame();
		}
		player1Move = !player1Move;
		player1MoveFirst = !player1MoveFirst;
		bricks.clear();
		gameView.invalidate();
	}
	
	public void EndGame(){
		gameView.EndGame();
	}
	
	public int[] getScores(){
		int [] scores = new int[2];
		scores[0] = player1Score;
		scores[1] = player2Score;
		return scores;
	}
}