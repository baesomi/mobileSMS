package com.example.lanco.mobile_sms.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lanco.mobile_sms.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends Fragment {
    LoginButton facebookLogin;
    LoginManager loginManager;
    String id = "";
    String name = "";
    String email = "";
    CallbackManager callbackManager;
    JSONArray rawName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) view.findViewById(R.id.login_button);
        facebookLogin.setReadPermissions("email");//facebook 권한요청
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {//로그인시 실행
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(//graphapi 요청
                                            JSONObject object,
                                            GraphResponse response) {
                        // Application code
                        try {
                            id = (String) response.getJSONObject().get("id");
                            name = (String) response.getJSONObject().get("name");
                            email = (String) response.getJSONObject().get("email");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                        }
                        // new joinTask().execute();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("myLog", "requestCode  " + requestCode);
        Log.d("myLog", "resultCode" + resultCode);
        Log.d("myLog", "data  " + data.toString());
    }
}
