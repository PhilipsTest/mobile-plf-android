/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.dicommclient.networknode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.philips.cdp.dicommclient.networknode.NetworkNode.PAIRED_STATUS;
import com.philips.cdp.dicommclient.util.DICommLog;

import java.util.ArrayList;
import java.util.List;

import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_BOOT_ID;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_CPP_ID;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_DEVICE_NAME;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_DEVICE_TYPE;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_ENCRYPTION_KEY;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_HTTPS;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_IP_ADDRESS;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_IS_PAIRED;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_LASTKNOWN_NETWORK;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_LAST_PAIRED;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.KEY_MODEL_ID;
import static com.philips.cdp.dicommclient.networknode.NetworkNodeDatabaseHelper.TABLE_NETWORK_NODE;

public class NetworkNodeDatabase {

    private NetworkNodeDatabaseHelper dbHelper;

    public NetworkNodeDatabase(Context context) {
        dbHelper = new NetworkNodeDatabaseHelper(context);
    }

    public List<NetworkNode> getAll() {
        List<NetworkNode> result = new ArrayList<NetworkNode>();

        Cursor cursor = null;

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(TABLE_NETWORK_NODE, null, null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String cppId = cursor.getString(cursor.getColumnIndex(KEY_CPP_ID));
                    long bootId = cursor.getLong(cursor.getColumnIndex(KEY_BOOT_ID));
                    String encryptionKey = cursor.getString(cursor.getColumnIndex(KEY_ENCRYPTION_KEY));
                    String name = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_NAME));
                    String lastKnownNetwork = cursor.getString(cursor.getColumnIndex(KEY_LASTKNOWN_NETWORK));
                    int pairedStatus = cursor.getInt(cursor.getColumnIndex(KEY_IS_PAIRED));
                    long lastPairedTime = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LAST_PAIRED));
                    String ipAddress = cursor.getString(cursor.getColumnIndex(KEY_IP_ADDRESS));
                    String deviceType = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_TYPE));
                    String modelId = cursor.getString(cursor.getColumnIndex(KEY_MODEL_ID));
                    boolean https = cursor.getShort(cursor.getColumnIndex(KEY_HTTPS)) == 1;

                    NetworkNode networkNode = new NetworkNode();
                    networkNode.setConnectionState(ConnectionState.DISCONNECTED);
                    networkNode.setCppId(cppId);
                    networkNode.setBootId(bootId);
                    networkNode.setEncryptionKey(encryptionKey);
                    networkNode.setName(name);
                    networkNode.setHomeSsid(lastKnownNetwork);
                    networkNode.setPairedState(NetworkNode.getPairedStatusKey(pairedStatus));
                    networkNode.setLastPairedTime(lastPairedTime);
                    networkNode.setIpAddress(ipAddress);
                    networkNode.setDeviceType(deviceType);
                    networkNode.setModelId(modelId);

                    result.add(networkNode);
                    DICommLog.d(DICommLog.DATABASE, "Loaded NetworkNode from db: " + networkNode);
                } while (cursor.moveToNext());
            } else {
                DICommLog.i(DICommLog.DATABASE, "Empty network node table");
            }
        } catch (Exception e) {
            DICommLog.e(DICommLog.DATABASE, "Error: " + e.getMessage());
        } finally {
            closeCursor(cursor);
            closeDatabase(db);
        }

        return result;
    }

    public long save(NetworkNode networkNode) {
        long rowId = -1L;

        if (networkNode == null)
            return rowId;

        if (networkNode.getPairedState() != NetworkNode.PAIRED_STATUS.PAIRED)
            networkNode.setPairedState(NetworkNode.PAIRED_STATUS.NOT_PAIRED);

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_CPP_ID, networkNode.getCppId());
            values.put(KEY_BOOT_ID, networkNode.getBootId());
            values.put(KEY_ENCRYPTION_KEY, networkNode.getEncryptionKey());
            values.put(KEY_DEVICE_NAME, networkNode.getName());
            values.put(KEY_LASTKNOWN_NETWORK, networkNode.getHomeSsid());
            values.put(KEY_IS_PAIRED, networkNode.getPairedState().ordinal());

            if (networkNode.getPairedState() == PAIRED_STATUS.PAIRED) {
                values.put(KEY_LAST_PAIRED, networkNode.getLastPairedTime());
            } else {
                values.put(KEY_LAST_PAIRED, -1L);
            }

            values.put(KEY_IP_ADDRESS, networkNode.getIpAddress());
            values.put(KEY_DEVICE_TYPE, networkNode.getDeviceType());
            values.put(KEY_MODEL_ID, networkNode.getModelId());
            values.put(KEY_HTTPS, networkNode.getHttps());

            rowId = db.insertWithOnConflict(TABLE_NETWORK_NODE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            DICommLog.d(DICommLog.DATABASE, "Saved NetworkNode in db: " + networkNode);
        } catch (Exception e) {
            e.printStackTrace();
            DICommLog.e(DICommLog.DATABASE, "Failed to save NetworkNode" + " ,Error: " + e.getMessage());
        } finally {
            closeDatabase(db);
        }

        return rowId;
    }

    public boolean contains(NetworkNode networkNode) {
        if (networkNode == null) return false;

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getWritableDatabase();
            cursor = db.query(TABLE_NETWORK_NODE, null, KEY_CPP_ID + " = ?", new String[]{networkNode.getCppId()}, null, null, null);

            if (cursor.getCount() > 0) {
                DICommLog.d(DICommLog.DATABASE, "NetworkNode already in db - " + networkNode);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase(db);
            closeCursor(cursor);
        }

        DICommLog.d(DICommLog.DATABASE, "NetworkNode not yet in db - " + networkNode);
        return false;
    }

    public int delete(NetworkNode networkNode) {
        SQLiteDatabase db = null;
        int rowsDeleted = 0;
        try {
            db = dbHelper.getReadableDatabase();

            rowsDeleted = db.delete(TABLE_NETWORK_NODE, KEY_CPP_ID + "= ?", new String[]{networkNode.getCppId()});
            DICommLog.d(DICommLog.DATABASE, "Deleted NetworkNode from db: " + networkNode + "  (" + rowsDeleted + ")");
        } catch (Exception e) {
            DICommLog.e(DICommLog.DATABASE, "Error: " + e.getMessage());
        } finally {
            closeDatabase(db);
        }
        return rowsDeleted;
    }

    private void closeDatabase(SQLiteDatabase db) {
        try {
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            DICommLog.e(DICommLog.DATABASE, "Error: " + e.getMessage());
        }
    }

    private void closeCursor(Cursor cursor) {
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            DICommLog.e(DICommLog.DATABASE, "Error: " + e.getMessage());
        }
    }
}
