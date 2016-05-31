package com.example.lanco.mobile_sms.Activity;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.R;
import com.example.lanco.mobile_sms.SMSListAdapter;

public class SendActivity extends Fragment{

    private String sort[];
    private Spinner sorting;
    private ListView listview;
    private SMSListAdapter adapter;

    private Cursor mCursor;
    private DBSingleManager db;
    private ViewGroup v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_send, container, false);
        v = container;

        sort = getResources().getStringArray(R.array.sort_array);
        sorting = (Spinner)view.findViewById(R.id.sorting);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sort);
        sorting.setAdapter(sortAdapter);

        sorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int index = sorting.getSelectedItemPosition();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        adapter = new SMSListAdapter() ;
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) view.findViewById(R.id.smsList);

        db = new DBSingleManager(getActivity());

        db.open();

        mCursor = db.getAllContacts();


        if(mCursor.moveToFirst()){
            do{
                if(mCursor.getString(mCursor.getColumnIndex("status")).equals("PENDING"))
                    DisplayContact(mCursor);
            }
            while(mCursor.moveToNext());
        }
        db.close();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SMSPopupActivity builder = new SMSPopupActivity(getActivity(),adapter.getPositionDate(position),adapter.getPositionName(position),adapter.getPositionMessage(position), v);
                android.app.AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        listview.setAdapter(adapter);

        return view;
    }

    public void DisplayContact(Cursor c){
        String date = DateUtils.formatDateTime(getActivity(), c.getLong(c.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        adapter.addItem(ContextCompat.getDrawable(this.getActivity(), R.drawable.before), c.getString(c.getColumnIndex("name")), date, c.getString(c.getColumnIndex("message")));
    }
}

