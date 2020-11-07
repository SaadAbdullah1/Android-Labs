package com.example.androidlabs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.example.androidlabs.Message;
import com.example.androidlabs.Message.Type;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    private final DatabaseHelper helper;
    private final Context context;
    private SQLiteDatabase db;

    public MessageRepository(Context context) {
        this.helper = new DatabaseHelper(context);
        this.context = context;
    }

    public Message save(Message message) {
        db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(DatabaseHelper.COL_TEXT, message.getText());
        // Using the ordinal to convert the Type enum to 0 or 1.
        row.put(DatabaseHelper.COL_TYPE, message.getType().ordinal());

        long id = db.insert(DatabaseHelper.TABLE_NAME, null, row);
        db.close();
        return new Message(id, message);
    }

    public List<Message> findAll() {
        db = helper.getReadableDatabase();
        String[] columns = {
                DatabaseHelper.COL_ID,
                DatabaseHelper.COL_TYPE,
                DatabaseHelper.COL_TEXT
        };
        Cursor results = db.query(
                false,
                DatabaseHelper.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Cursor copy = db.query(
                false,
                DatabaseHelper.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null
        );
        printCursor(copy, db.getVersion());
        List<Message> messages = new ArrayList<>();
        if (results.getCount() > 0) {
            while (results.moveToNext()) {
                Message message = new Message(
                        // ID
                        results.getLong(results.getColumnIndex(DatabaseHelper.COL_ID)),
                        // Text
                        results.getString(results.getColumnIndex(DatabaseHelper.COL_TEXT)),
                    /* Send or received.
                       Have to check if it is not 0 for RECEIVED, otherwise it is SENT. */
                        results.getInt(results.getColumnIndex(DatabaseHelper.COL_TYPE)) > 0
                                ? Type.RECEIVED : Type.SENT
                );
                messages.add(message);
            }
        }
        // Avoid memory leak
        results.close();
        copy.close();
        db.close();
        return messages;
    }

    public void delete(Message message) {
        db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, "_id=?", new String[]{message.getId().toString()});
        db.close();
    }

    private void printCursor(Cursor c, int version) {
        String activity = ((Activity) context).getComponentName().flattenToString();
        StringBuilder columnNames = new StringBuilder();
        Log.i(activity, "Database Version: " + version);
        Log.i(activity, "Number of Columns: " + c.getColumnCount());
        for (int i = 0; i < c.getColumnCount(); i++) {
            columnNames.append(c.getColumnName(i));
            if (i != c.getColumnCount()) {
                columnNames.append(", ");
            }
        }
        Log.i(activity, "Columns: " + TextUtils.join(",", c.getColumnNames()));
        Log.i(activity, "Number of Results: " + c.getCount());
        Log.i(activity, "Results:\n");
        while (c.moveToNext()) {
            StringBuilder resultRow = new StringBuilder();
            for (int i = 0; i < c.getColumnCount(); i++) {
                resultRow
                        .append(c.getColumnName(i))
                        .append(": ")
                        .append(c.getString(i))
                        .append("; ");
            }
            Log.i(activity, resultRow.toString());
        }

    }
}