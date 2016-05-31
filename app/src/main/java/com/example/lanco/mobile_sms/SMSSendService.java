package com.example.lanco.mobile_sms;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import com.example.lanco.mobile_sms.DB.DBSingleManager;

import java.util.ArrayList;

public class SMSSendService extends IntentService {

    public static final String KEY_REPORT_DATE = "reportdate"; // 작성일
    public static final String KEY_RESERVED_DATE = "reserveddate"; // 예약된 날짜
    public static final String KEY_NAME = "name";  // 이름
    public static final String KEY_PHONE = "phone"; // 번호
    public static final String KEY_MESSAGE = "message"; // 내용
    public static final String KEY_SORT = "sort"; // 분류
    public static final String KEY_STATUS = "status"; // 상태
    public static final String KEY_RESULT = "result"; // 결과

    public SMSSendService() {
        super("SMSSendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long smsId = intent.getExtras().getLong("ReportData", 0);
        if(smsId==0){
            throw new RuntimeException("No SMS id provided with intent");
        }

        DBSingleManager db = new DBSingleManager(this);
        db.open();
        SMSData c = db.getSMSData(db.getReportContact(smsId));
        db.close();

        sendSMS(c);
    }

    private void sendSMS(SMSData sms) {
        Long smsId = sms.getReportDate();

        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = null;

        Intent sentIntent = new Intent(this, SMSSentReceiver.class);
        sentIntent.setAction(smsId.toString());
        sentIntent.putExtra("ReportData", smsId);
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, sentIntent, 0);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> mSMSMessage = smsManager.divideMessage(sms.getMessage());
        for (int i = 0; i < mSMSMessage.size(); i++) {
            sentPendingIntents.add(i, sentPendingIntent);
        }
        smsManager.sendMultipartTextMessage(sms.getRecipientNumber(), null, mSMSMessage, sentPendingIntents, null);
    }
}
