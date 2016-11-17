package com.example.a310238655.securedb.SQLCipherORMLitePOC;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Basic class which provides no-op methods for all Android version.
 * 
 * @author graywatson
 */
public class BasicApiCompatibility implements ApiCompatibility {

	public Cursor rawQuery(SQLiteDatabase db, String sql, String[] selectionArgs, CancellationHook cancellationHook) {
		// NOTE: cancellationHook will always be null
		return db.rawQuery(sql, selectionArgs);
	}

	public CancellationHook createCancellationHook() {
		return null;
	}
}
