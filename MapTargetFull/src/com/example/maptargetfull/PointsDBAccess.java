package com.example.maptargetfull;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.maptargetfull.SQLiteDB.Points;

public class PointsDBAccess {
	private SQLiteDB mDBHandler;
	
	public PointsDBAccess(Context context) {
		this.mDBHandler = new SQLiteDB(context);
	}
	
	public long createPoint(String firstName, String lastName, double longitude, double langitude, Boolean is_google){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.first_name, firstName);
		values.put(Points.Columns.last_name, lastName);
		values.put(Points.Columns.longitude, longitude);
		values.put(Points.Columns.langitude, langitude);
		values.put(Points.Columns.is_google, is_google);
		return db.insert(SQLiteDB.Points.table_name, null, values);
	}
	
	public Boolean updatePoint(long rowID, String firstName, String lastName, double longitude, double langitude, Boolean is_google){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.first_name, firstName);
		values.put(Points.Columns.last_name, lastName);
		values.put(Points.Columns.longitude, longitude);
		values.put(Points.Columns.langitude, langitude);
		values.put(Points.Columns.is_google, is_google);
		values.put(Points.Columns.is_synched, false);
		return db.update(Points.table_name, values, Points.Columns.row_id + " = ?", new String[]{ String.valueOf(rowID) }) == 1;
	}
	
	public Boolean deletePoint(long rowID){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.is_synched, false);
		values.put(Points.Columns.is_deleted, true);
		return db.update(Points.table_name, values, Points.Columns.row_id + " = ?", new String[]{ String.valueOf(rowID) }) == 1;
	}
	
	public ArrayList<Point> getPoints(Boolean is_google){
		ArrayList<Point> arrayPoint = new ArrayList<PointsDBAccess.Point>();
		Point p;
		SQLiteDatabase db = this.mDBHandler.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT " +
								    Points.Columns.row_id + ", " +
								    Points.Columns.first_name + ", " +
								    Points.Columns.last_name + ", " +
								    Points.Columns.longitude + ", " +
								    Points.Columns.langitude + " " +
								    "FROM " + Points.table_name + " " +
								    "WHERE " + Points.Columns.is_google + " = ? " +
								    "AND " + Points.Columns.is_deleted + " = " + true, new String[]{ is_google.toString() });
		while (cursor.moveToNext()) {
			p = new Point();
			p.rowID = cursor.getLong(cursor.getColumnIndex(Points.Columns.row_id));
			p.first_name = cursor.getString(cursor.getColumnIndex(Points.Columns.first_name));
			p.last_name = cursor.getString(cursor.getColumnIndex(Points.Columns.last_name));
			p.longitude = cursor.getLong(cursor.getColumnIndex(Points.Columns.longitude));
			p.langitude = cursor.getLong(cursor.getColumnIndex(Points.Columns.langitude));
			arrayPoint.add(p);
		}
		
		return arrayPoint;
	}
	
	class Point{
		public long rowID;
		public String first_name;
		public String last_name;
		public double longitude;
		public double langitude;
	}
}
