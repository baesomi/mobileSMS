package com.example.lanco.mobile_sms.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lanco.mobile_sms.SMSData;

import java.util.Calendar;

/**
 * Created by Lanco on 2016-05-29.
 */
public class DBSingleManager {
    private static final String dbSingle="MessageSingle.db";
    private static final String tableSingle="MessageSingle";
    public static final int dbVersion=1;

    private static final String KEY_REPORT_DATE="reportdate";
    private static final String KEY_RESERVED_DATE="reserveddate";
    private static final String KEY_NAME="name";
    private static final String KEY_PHONE="phone";
    private static final String KEY_MESSAGE="message";
    private static final String KEY_SORT="sort";
    private static final String KEY_STATUS="status";
    private static final String KEY_RESULT="result";

    private DatabaseHelper DBHelper;
    private SQLiteDatabase sqldb;
    final Context context;

    public DBSingleManager(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, dbSingle, null, dbVersion);
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                String DBCreate = "create table " + tableSingle
                        + "(" + KEY_REPORT_DATE + " biginteger primary key, "
                        + KEY_RESERVED_DATE + " biginteger not null, "
                        + KEY_NAME + " text, "
                        + KEY_PHONE + " text, "
                        + KEY_MESSAGE + " text, "
                        + KEY_SORT + " text, "
                        + KEY_STATUS + " text, "
                        + KEY_RESULT + " text"
                        + ")";
                db.execSQL(DBCreate);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DBSinglemanager", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS ");
            onCreate(db);
        }
    }

    public DBSingleManager open() throws SQLException {
        sqldb = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    // 값 추가
    public long insertContact(SMSData sms) {
        ContentValues ini = new ContentValues();
        long timeReported = Calendar.getInstance().getTimeInMillis();
        sms.setReportDate(timeReported);
        ini.put(KEY_REPORT_DATE, sms.getReportDate());
        ini.put(KEY_RESERVED_DATE, sms.getReservedDate());
        ini.put(KEY_NAME, sms.getRecipientName());
        ini.put(KEY_PHONE, sms.getRecipientNumber());
        ini.put(KEY_MESSAGE,sms.getMessage());
        ini.put(KEY_SORT, sms.getSort());
        ini.put(KEY_STATUS, sms.getStatus());
        ini.put(KEY_RESULT, sms.getResult());
        return sqldb.insert(tableSingle, null, ini);
    }

    // 특정 전송시간대의 row를 삭제
    public boolean deleteContact(long rowId) {
        return sqldb.delete(tableSingle, KEY_REPORT_DATE + "=" + rowId, null) > 0;
    }

    // 업데이트부분
    public boolean updateContact(SMSData sms) {
        ContentValues args = new ContentValues();
        args.put(KEY_RESERVED_DATE, sms.getReservedDate());
        args.put(KEY_NAME, sms.getRecipientName());
        args.put(KEY_PHONE, sms.getRecipientNumber());
        args.put(KEY_MESSAGE,sms.getMessage());
        args.put(KEY_SORT, sms.getSort());
        args.put(KEY_STATUS, sms.getStatus());
        args.put(KEY_RESULT, sms.getResult());
        String pre = sms.getReportDate().toString();
        return sqldb.update(tableSingle, args, KEY_REPORT_DATE + "=" + pre, null) > 0;
    }
    //모든 값을 가져온다
    public Cursor getAllContacts() {
        return sqldb.query(tableSingle, new String[] { "*",KEY_REPORT_DATE + " AS _id" }, null, null, null, null, KEY_REPORT_DATE+" DESC");
    }

    // 전송시작 시간에 해당하는 값을 전송하는 커서로 반환
    public Cursor getReportContact(long report) throws SQLException {
        Cursor mCursor = sqldb.query(true, tableSingle, new String[] {"*",KEY_REPORT_DATE+" AS _id"}, KEY_REPORT_DATE + "=" + report,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    // 상태에 해당하는 row를 커서형태로 반환
    public Cursor getStatusCursor(String status){
        String[] columns = new String[]{ "*", KEY_REPORT_DATE + " AS _id" };
        String selection = "";
        if(null!=status && status.length()>0){
            selection = KEY_STATUS+"="+status;
        }
        String orderBy = KEY_REPORT_DATE+" DESC";
        Cursor c = sqldb.query(tableSingle,columns,selection,null,null,null,orderBy);
        if(c!=null)
            c.moveToFirst();

        return c;
    }


    // 커서의 값을 SMSData화 시켜서 보내주는 역할
    public SMSData getSMSData(Cursor c){
        SMSData m = new SMSData();
        m.setReportDate(c.getLong(c.getColumnIndex(KEY_REPORT_DATE)));
        m.setReservedDate(c.getLong(c.getColumnIndex(KEY_RESERVED_DATE)));
        m.setRecipientName(c.getString(c.getColumnIndex(KEY_NAME)));
        m.setRecipientNumber(c.getString(c.getColumnIndex(KEY_PHONE)));
        m.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));
        m.setSort(c.getString(c.getColumnIndex(KEY_SORT)));
        m.setStatus(c.getString(c.getColumnIndex(KEY_STATUS)));
        m.setResult(c.getString(c.getColumnIndex(KEY_RESULT)));
        return m;
    }
}
