package edu.msu.BlueSky.stacker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class FinalScoreView extends View {
	public FinalScoreView(Context context) {
		super(context);
	}
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint(); 
		paint.setColor(Color.WHITE); 
		paint.setStyle(Style.FILL); 
		canvas.drawPaint(paint); 

		paint.setColor(Color.BLACK); 
		paint.setTextSize(20); 
		canvas.drawText("Some Text", 10, 25, paint); 	}
}
