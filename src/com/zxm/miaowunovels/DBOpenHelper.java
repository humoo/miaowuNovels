package com.zxm.miaowunovels;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "book.db";
	private static final int VERSION = 1;

	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS bookInfo ("
				+ "id integer primary key autoincrement,"
				+ "bookId varchar(10), " 
				+ "bookName varchar(15),"
				+ "bookNew varchar(15),"// 最新一章
				+ "bookTime varchar(15),"// 最新时间
				+ "bookUrl varchar(30)," 
				+ "bookPic varchar(15))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS bookInfo");
		onCreate(db);

	}

}
