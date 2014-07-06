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
	
	public long createPoint(String firstName, String lastName, double longitude, double langitude, Boolean is_google, int pointType){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.first_name, firstName);
		values.put(Points.Columns.last_name, lastName);
		values.put(Points.Columns.longitude, longitude);
		values.put(Points.Columns.langitude, langitude);
		values.put(Points.Columns.is_google, SQLiteDB.convertBoolean(is_google));
		values.put(Points.Columns.is_deleted, SQLiteDB.convertBoolean(false));
		values.put(Points.Columns.is_synched, SQLiteDB.convertBoolean(false));
		values.put(Points.Columns.point_type, pointType);
		
		return db.insert(SQLiteDB.Points.table_name, null, values);
	}
	
	public Boolean SetServerID(long rowID, String serverID){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.server_id, serverID);
		return db.update(Points.table_name, values, Points.Columns.row_id + " = ?", new String[]{ String.valueOf(rowID) }) == 1;
	}
	
	public Boolean SetSynched(long rowID){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.is_synched, SQLiteDB.convertBoolean(true));
		return db.update(Points.table_name, values, Points.Columns.row_id + " = ?", new String[]{ String.valueOf(rowID) }) == 1;
	}
	
	public Boolean updatePoint(long rowID, String firstName, String lastName, double longitude, double langitude, Boolean is_google, int pointType){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.first_name, firstName);
		values.put(Points.Columns.last_name, lastName);
		values.put(Points.Columns.longitude, longitude);
		values.put(Points.Columns.langitude, langitude);
		values.put(Points.Columns.is_google, SQLiteDB.convertBoolean(is_google));
		values.put(Points.Columns.is_deleted, SQLiteDB.convertBoolean(false));
		values.put(Points.Columns.is_synched, SQLiteDB.convertBoolean(false));
		values.put(Points.Columns.point_type, pointType);
		return db.update(Points.table_name, values, Points.Columns.row_id + " = ?", new String[]{ String.valueOf(rowID) }) == 1;
	}
	
	public Boolean deletePoint(long rowID){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Points.Columns.is_synched, SQLiteDB.convertBoolean(false));
		values.put(Points.Columns.is_deleted, SQLiteDB.convertBoolean(true));
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
								    Points.Columns.langitude + ", " +
								    Points.Columns.point_type + " " +
								    "FROM " + Points.table_name + " " +
								    "WHERE " + Points.Columns.is_google + " = ? " +
								    "AND " + Points.Columns.is_deleted + " = " + String.valueOf(SQLiteDB.convertBoolean(false)), new String[]{ String.valueOf(SQLiteDB.convertBoolean(is_google)) });
	
		while (cursor.moveToNext()) {
			p = new Point();
			p.rowID = cursor.getLong(cursor.getColumnIndex(Points.Columns.row_id));
			p.first_name = cursor.getString(cursor.getColumnIndex(Points.Columns.first_name));
			p.last_name = cursor.getString(cursor.getColumnIndex(Points.Columns.last_name));
			p.longitude = cursor.getLong(cursor.getColumnIndex(Points.Columns.longitude));
			p.langitude = cursor.getLong(cursor.getColumnIndex(Points.Columns.langitude));
			p.pointType = cursor.getInt(cursor.getColumnIndex(Points.Columns.point_type));
			arrayPoint.add(p);
		}
		
		return arrayPoint;
	}
	
	public Boolean deletePointsForSync(){
		SQLiteDatabase db = this.mDBHandler.getWritableDatabase();
		return db.delete(Points.table_name, "1", null) != -1;
	}
	
	public ArrayList<PointForSync> getPointsForSync(){
		ArrayList<PointForSync> arrayPoint = new ArrayList<PointForSync>();
		PointForSync p;
		SQLiteDatabase db = this.mDBHandler.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT " +
								    Points.Columns.row_id + ", " +
								    Points.Columns.first_name + ", " +
								    Points.Columns.last_name + ", " +
								    Points.Columns.longitude + ", " +
								    Points.Columns.langitude + ", " +
								    Points.Columns.server_id + ", " +
								    Points.Columns.is_deleted + ", " +
								    Points.Columns.is_google + ", " +
								    Points.Columns.point_type + " " +
								    "FROM " + Points.table_name + " " +
								    "WHERE " + Points.Columns.is_synched + " = " + String.valueOf(SQLiteDB.convertBoolean(false)), null);
	
		while (cursor.moveToNext()) {
			p = new PointForSync();
			p.rowID = cursor.getLong(cursor.getColumnIndex(Points.Columns.row_id));
			p.first_name = cursor.getString(cursor.getColumnIndex(Points.Columns.first_name));
			p.last_name = cursor.getString(cursor.getColumnIndex(Points.Columns.last_name));
			p.longitude = cursor.getDouble(cursor.getColumnIndex(Points.Columns.longitude));
			p.langitude = cursor.getDouble(cursor.getColumnIndex(Points.Columns.langitude));
			p.server_id = cursor.getString(cursor.getColumnIndex(Points.Columns.server_id));
			p.is_deleted = cursor.getInt(cursor.getColumnIndex(Points.Columns.is_deleted));
			p.pointType = cursor.getInt(cursor.getColumnIndex(Points.Columns.point_type));
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
		public int pointType;
	}
	class PointForSync{
		public long rowID;
		public String first_name;
		public String last_name;
		public double longitude;
		public double langitude;
		public String server_id;
		public int is_deleted;
		public int is_google;
		public int pointType;
	}
}
