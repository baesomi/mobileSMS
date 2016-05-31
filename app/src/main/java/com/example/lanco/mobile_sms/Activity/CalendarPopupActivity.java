package com.example.lanco.mobile_sms.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.lanco.mobile_sms.R;

public class CalendarPopupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_calendar_popup);

        TextView tb = (TextView) findViewById(R.id.tb);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String people = b.getString("data");
        tb.setText(people);
    }
}
