package com.example.lanco.mobile_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String INTENT_FILTER = "com.example.lanco.mobile_sms.AlarmReceiver.INTENT_FILTER";
    public AlarmReceiver() {}
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SMSSendService.class);
        service.putExtras(intent.getExtras());
        startWakefulService(context,service);
    }
}
