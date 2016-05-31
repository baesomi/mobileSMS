package com.example.lanco.mobile_sms;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.telephony.SmsManager;

import com.example.lanco.mobile_sms.DB.DBSingleManager;

public class SMSSentService extends IntentService {

    public SMSSentService() {
        super("SMSSentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long smsId = intent.getExtras().getLong("ReportData",0);
        if (smsId == 0) {
            throw new RuntimeException("No SMS id provided with intent");
        }

        DBSingleManager db = new DBSingleManager(this);
        db.open();
        SMSData sms = db.getSMSData(db.getReportContact(smsId));

        String errorId = "";
        String errorString = "";
        String title = "메세지 전송에 실패했습니다.";
        String message = "";
        sms.setStatus(SMSData.STATUS_FAILED);

        switch (intent.getExtras().getInt(SMSSentReceiver.RESULT_CODE, 0)) {
            case Activity.RESULT_OK:
                title = "메세지 전송완료!";
                message = sms.getRecipientName()+" 님에게 전송을 완료하였습니다.";
                sms.setStatus(SMSData.STATUS_SENT);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                errorId = SMSData.ERROR_GENERIC;
                errorString = "내부적 전송 실패";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                errorId = SMSData.ERROR_NO_SERVICE;
                errorString = "서비스 지역이 아닙니다.";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                errorId = SMSData.ERROR_NULL_PDU;
                errorString = "PDU 실패";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                errorId = SMSData.ERROR_RADIO_OFF;
                errorString = "무선(Radio)가 꺼져있습니다.";
                break;
            default:
                errorId = SMSData.ERROR_UNKNOWN;
                errorString = "알수없는 오류";
                break;
        }
        if (errorId.length() > 0) {
            sms.setResult(errorId);
            message = sms.getRecipientName()+" 님에게 전송을 실패했습니다 : "+errorString;
        }
        db.updateContact(sms);
        db.close();
    }
}
