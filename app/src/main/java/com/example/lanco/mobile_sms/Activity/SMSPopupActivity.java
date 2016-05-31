package com.example.lanco.mobile_sms.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.R;

public class SMSPopupActivity extends AlertDialog.Builder {

    String d,n,m;
    Context c;
    LayoutInflater inflater;
    View dialogView;
    TextView name;
    TextView phone;
    TextView report;
    TextView reserved;
    TextView sort;
    TextView message;

    public SMSPopupActivity(Context context) {
        super(context);
        buildPopUp();
    }

    public SMSPopupActivity(Context context, String d, String n, String m,ViewGroup v) {
        super(context);
        this.d = d;
        this.n = n;
        this.m = m;
        this.c = context;
        dialogView= View.inflate(c, R.layout.activity_smspopup, null);
         name = (TextView)dialogView.findViewById(R.id.pop_name);
         phone = (TextView)dialogView.findViewById(R.id.pop_number);
         report = (TextView)dialogView.findViewById(R.id.pop_report);
         reserved = (TextView)dialogView.findViewById(R.id.pop_reserved);
         sort = (TextView)dialogView.findViewById(R.id.pop_sort);
         message = (TextView)dialogView.findViewById(R.id.pop_message);
        setView(dialogView);
        buildPopUp();
    }

    public void buildPopUp(){
        String t_name = null,t_phone = null,t_report = null,t_reserved = null,t_sort = null,t_message = null;

        this.setTitle("메세지 내용");
        this.setIcon(R.drawable.pop_m);
        DBSingleManager db = new DBSingleManager(c);
        db.open();
        Cursor data = db.getAllContacts();
        if(data.moveToFirst()) {
            do {
                if (data.getString(data.getColumnIndex("name")).equals(n)) {
                    if (data.getString(data.getColumnIndex("message")).equals(m)) {
                        if (DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME).equals(d)) {
                            t_name=data.getString(data.getColumnIndex("name"));
                            t_phone = data.getString(data.getColumnIndex("phone"));
                            t_report = DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                            t_reserved = DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reserveddate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                            t_sort = data.getString(data.getColumnIndex("sort"));
                            t_message = data.getString(data.getColumnIndex("message"));
                        }
                    }
                }
            }while(data.moveToNext());
        }
        db.close();

        name.setText(t_name);
        phone.setText(t_phone);
        report.setText(t_report);
        reserved.setText(t_reserved);
        sort.setText(t_sort);
        message.setText(t_message);

        this.setPositiveButton("변경/수정", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        this.setNegativeButton("삭제", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }


}

