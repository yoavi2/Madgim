package com.example.maptargetfull;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;

import com.example.maptargetfull.PointsDBAccess.Point;

public class DrawSample extends View {

	private Paint mPaint = new Paint();
	private Bitmap mBitmap = null;
	private Canvas myCanvas;
	private Thread renderThread = null;
	private SurfaceHolder holder;
	volatile boolean running = false;
	private Context context;
	private int x = 0;

	public DrawSample(Context context, AttributeSet attr) {
		super(context, attr);
		this.setLongClickable(true);
		// resume();
		// holder = getHolder();
		this.context = context;

	}

	// @Override
	// public void surfaceChanged(final SurfaceHolder arg0,
	// final int arg1, final int arg2, final int arg3) {}
	// @Override
	// public void surfaceDestroyed(final SurfaceHolder arg0) {
	// // GameActivity.this.running = false;
	// }
	// @Override
	// public void surfaceCreated( SurfaceHolder arg0) {
	// holder = arg0;
	// }
	// create a paint brush
	// mPaint = new Paint();
	// mPaint.setColor(Color.DKGRAY);

	// public void run(){
	// // System.out.println("Hello World1");
	// while( x < 100){
	//
	// Dr22();
	// x++;
	// }
	// }
	// public void resume() {
	// // System.out.println("Hello World2");
	//
	// running = true;
	// renderThread = new Thread(this);
	// renderThread.start();
	// }
	@Override
	public void onDraw(Canvas canvas) {
		Bitmap friendBitmap, enemyBitmap;
		canvas.drawPaint(mPaint);

		mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.final_map);
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);

		mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.circle);
		canvas.drawBitmap(mBitmap, (getWidth() / 2) - (mBitmap.getWidth() / 2),
				(getHeight() / 2) - (mBitmap.getHeight() / 2), mPaint);

		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.me);
		canvas.drawBitmap(mBitmap, (getWidth() / 2) - (mBitmap.getWidth() / 2),
				(getHeight() / 2) - (mBitmap.getHeight() / 2), mPaint);

		friendBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.friend);

		enemyBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.enemy);
		
		for (Point point : GlobalParams.getInstance().getPoints()) {
			switch (point.pointType) {
			case 1:
				if (GlobalParams.getInstance().getFriend() == true)
				{
					canvas.drawBitmap(friendBitmap, (float) point.langitude,
							(float) point.longitude, mPaint);
				}
				break;
			case 2:
				if (GlobalParams.getInstance().getEenemy() == true)
				{
					canvas.drawBitmap(enemyBitmap, (float) point.langitude,
							(float) point.longitude, mPaint);
				}			
				break;
			default:
				break;
			}
		}
		
		if (GlobalParams.getInstance().pdialog != null) {
			GlobalParams.getInstance().pdialog.hide();
		}


	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void Dr22() {

	}

}
