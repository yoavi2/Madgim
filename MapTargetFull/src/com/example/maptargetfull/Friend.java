package com.example.maptargetfull;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Friend {
	private String name;
	private long rowId;
	private Rect space;
	private Bitmap mBitmap;
	private double width;
	private double height;

	private String nickName;
	
	public Friend(String name, long Distance, String nick, double width, double height)
	{
		this.name = name;
		this.rowId = Distance;
		this.nickName = nick;
		this.width = width;
		this.height= height;
	}
	
	public Friend(String name, long Distance, String nick, double width, double height, Bitmap bitmap)
	{
		this.name = name;
		this.rowId = Distance;
		this.nickName = nick;
		this.width = width;
		this.height= height;
		this.setBitmap(bitmap);
	}
	
	public String getnick(){
		return this.nickName;
	}
	public void setDimension(int width, int height)
	{
		this.width = width;
		this.height = height;
		
	//	 mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_little);
	//	space = new Rect(0,200,0 + mBitmap.getWidth(),200 + mBitmap.getHeight());
	}
	public long getRowId(){
		return this.rowId;
	}
	public String getName(){
		return this.name;
	}
	public double getWidth(){
		return this.width;
	}
	public double getHeight(){
		return this.height;
	}
	public void setWidth(double w){
		this.width = w;
	}
	public void setHeight(double h){
		this.height = h;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}
}
