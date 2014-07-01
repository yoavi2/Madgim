package com.example.maptargetfull;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper{
	
	static class Points{
		static final String table_name = "POINTS";
		static class Columns{
			static final String row_id		= "rowid";
			static final String server_id	= "SERVER_ID";
			static final String first_name 	= "FIRST_NAME";
			static final String last_name 	= "LAST_NAME";
			static final String longitude 	= "LONGITUDE";
			static final String langitude 	= "LANGITUDE";
			static final String is_google 	= "IS_GOOGLE";
			static final String is_deleted 	= "IS_DELETED";
			static final String is_synched	= "IS_SYNCHED"; 	
		}
	}
	
	public SQLiteDB(Context context) {
		super(context, "MAPTARGET.db", null, 2);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Points.table_name + " (" +
				   Points.Columns.server_id + " TEXT," +
				   Points.Columns.first_name + " TEXT, " +
				   Points.Columns.last_name + " TEXT, " +
				   Points.Columns.longitude + " DOUBLE, " +
				   Points.Columns.langitude + " DOUBLE, " +
				   Points.Columns.is_google + " BOOLEAN, " +
				   Points.Columns.is_deleted + " BOOLEAN, " +
				   Points.Columns.is_synched + " BOOLEAN);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Points.table_name);
		onCreate(db);
	}
	
	static public int convertBoolean(Boolean bool){
		return bool?1:0;
	}
}
