package com.example.lanco.mobile_sms.Activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lanco.mobile_sms.AlarmReceiver;
import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.R;
import com.example.lanco.mobile_sms.SMSData;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class WriteActivity extends AppCompatActivity {

    Context mContext;
    ImageButton btn_plus; // 이미지 버튼 - 연락처와 연동
    Button btn_send; // 문자 보내기 버튼
    EditText edit_name; // 이름
    EditText edit_phone; // 번호 입력칸
    EditText edit_message; // 메세지 입력칸
    Spinner sort_spinner; // 분류를 보여주는 스피너창
    TextView display; // 시간을 보여주는 창

    Button pickDate, pickTime; // 날짜와 시간을 구하는 버튼창
    GregorianCalendar mCalendar; //현재날자, 시간 정보를 구하는 객체

    private SMSData sms;

    //날짜를 변경하는 객체
    DateFormat fmDateAndTime = DateFormat.getDateTimeInstance();//날짜시간정보를 표시하는 형식
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    //시간을 변경하는 객체
    TimePickerDialog.OnTimeSetListener time =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    mCalendar.set(Calendar.MINUTE, minute);
                    updateLabel();
                }
            };//시간을 변경하는 객체

    //날짜,시간을 보여주는 텍스트뷰의 업데이트
    public void updateLabel() {
        display.setText(
                fmDateAndTime.format(mCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        sms = new SMSData();

        mContext = this;
        btn_plus = (ImageButton) findViewById(R.id.pBtn);
        btn_send = (Button) findViewById(R.id.sBtn);
        edit_name = (EditText) findViewById(R.id.nEdit);
        edit_phone = (EditText) findViewById(R.id.pEdit);
        edit_message = (EditText) findViewById(R.id.mEdit);
        display = (TextView) findViewById(R.id.display);
        pickDate = (Button) findViewById(R.id.pickDate);
        pickTime = (Button) findViewById(R.id.pickTime);
        sort_spinner = (Spinner) findViewById(R.id.class1);//기념일 분류

        //기념일 선택했을 때
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // tv.setText("position : " + position + parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Processing permission for SDK 23 Version(Android 6.0)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                    //Toast.makeText(this, "SMS 권한을 사용자가 승인함.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "SMS 권한 거부됨.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    public void dateSet(View v) {
        mCalendar = new GregorianCalendar();
        DatePickerDialog dpd = new DatePickerDialog(this, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();

    }

    public void timeSet(View v) {
        mCalendar = new GregorianCalendar();
        TimePickerDialog tpd = new TimePickerDialog(this, time, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        tpd.show();
    }

    //
    public void plusClick(View v) {
        Intent myActivity2 = new Intent(Intent.ACTION_PICK);
        myActivity2.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(myActivity2, 22);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
            cursor.moveToFirst();
            String number = cursor.getString(0);
            String name = cursor.getString(1);//0은 번호 / 1은 이름
            edit_name.setText(name);
            edit_phone.setText(number);
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendClick(View v) {
        if(mCalendar.getTimeInMillis()<GregorianCalendar.getInstance().getTimeInMillis()){
            Toast.makeText(getApplicationContext(), "현재 시간보다 낮아요!", Toast.LENGTH_SHORT).show();
            return;
        }
        // 빈 공백이 있는지 확인해주는 변수들
        String smsName = edit_name.getText().toString();
        String smsNum = edit_phone.getText().toString();
        String smsText = edit_message.getText().toString();
        String smsClass = sort_spinner.getSelectedItem().toString();

        sms.setReservedDate(mCalendar.getTimeInMillis());
        sms.setRecipientName(smsName);
        sms.setRecipientNumber(smsNum);
        sms.setMessage(smsText);
        sms.setSort(smsClass);
        sms.setStatus(SMSData.STATUS_PENDING);

        DBSingleManager db = new DBSingleManager(this);
        db.open();
        long a = db.insertContact(sms);
        db.close();

        scheduleAlarm(sms);

        finish();
    }

    private void scheduleAlarm(SMSData sms) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, sms.getReservedDate(), getAlarmPendingIntent(sms));
    }

    private PendingIntent getAlarmPendingIntent(SMSData sms) {
        Intent intent = new Intent(AlarmReceiver.INTENT_FILTER);
        intent.putExtra("ReportData", sms.getReportDate());
        return PendingIntent.getBroadcast(
                this,
                sms.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT & Intent.FILL_IN_DATA
        );
    }
}
