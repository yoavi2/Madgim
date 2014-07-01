package com.example.maptargetfull;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

		if (GlobalParams.getInstance().getEenemy() == true) {
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.enemy);
			canvas.drawBitmap(mBitmap, 50, 403, mPaint);
		}
		if (GlobalParams.getInstance().getFriend() == true) {
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.friend);

			for (Friend iterable : GlobalParams.getInstance().getFriends()) {

				canvas.drawBitmap(mBitmap, iterable.getWidth(),
						iterable.getHeight(), mPaint);
				// iterable.setWidth(iterable.getWidth() + 1);

			}
		}
		if (GlobalParams.getInstance().pdialog != null) {
			GlobalParams.getInstance().pdialog.hide();
		}


	}

	public void Dr22() {

	}

}
