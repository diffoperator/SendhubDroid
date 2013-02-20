package com.demo.sendhubdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SendHubSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_CONTACTS = "contacts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTACT_NAME = "contactname";
	public static final String COLUMN_CONTACT_NUMBER = "contactnumber";
	public static final String COLUMN_CONTACT_ID = "contactid";

	private static final String DATABASE_NAME = "sendhub_contacts_"
			+ SendHubSession.getCurrentSessionNumber() + ".db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_CONTACTS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ COLUMN_CONTACT_NAME + " text not null, " 
			+ COLUMN_CONTACT_NUMBER + " text not null);";

	public SendHubSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SendHubSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(db);
	}

}
