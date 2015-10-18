package com.noc.smsverify.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.noc.smsverify.R;
import com.noc.smsverify.app.Config;
import com.noc.smsverify.app.MyApplication;
import com.noc.smsverify.helper.PrefManager;
import com.noc.smsverify.service.HttpService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by defoliate on 14-10-2015.
 */
public class SmsActivity extends Activity implements View.OnClickListener
{
    private static String TAG = SmsActivity.class.getSimpleName();

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button btnRequestSms, btnVerifyOtp;
    //private EditText inputName, inputEmail;
    private EditText inputMobile, inputOtp;
    private ProgressBar progressBar;
    private PrefManager pref;
    private ImageButton btnEditMobile;
    private TextView txtEditMobile;
    private LinearLayout layoutEditMobile;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);
        //inputName = (EditText) findViewById(R.id.inputName);
        //inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputMobile = (EditText) findViewById(R.id.inputMobile);
        inputOtp = (EditText) findViewById(R.id.inputOtp);
        btnRequestSms = (Button) findViewById(R.id.btn_request_sms);
        btnVerifyOtp = (Button) findViewById(R.id.btn_verify_otp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnEditMobile = (ImageButton) findViewById(R.id.btn_edit_mobile);
        txtEditMobile = (TextView) findViewById(R.id.txt_edit_mobile);
        layoutEditMobile = (LinearLayout) findViewById(R.id.layout_edit_mobile);

        btnEditMobile.setOnClickListener(this);
        btnRequestSms.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);

        // hiding the edit mobile number
        layoutEditMobile.setVisibility(View.GONE);

        pref = new PrefManager(this);

        // Checking for user session if user is already logged in, take him to main activity
        if(pref.isLoggedIn())
        {
            Intent intent = new Intent(SmsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected (int position)
            {
            }

            @Override
            public void onPageScrollStateChanged (int state)
            {

            }
        });


        /** Checking if the device is waiting for sms showing the user OTP screen */
        if(pref.isWaitingForSms())
        {
            viewPager.setCurrentItem(1);
            layoutEditMobile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick (View view)
    {
        switch(view.getId())
        {
            case R.id.btn_request_sms:
                validateForm();
                break;

            case R.id.btn_verify_otp:
                verifyOtp();
                break;

            case R.id.btn_edit_mobile:
                viewPager.setCurrentItem(0);
                layoutEditMobile.setVisibility(View.GONE);
                pref.setIsWaitingForSms(false);
                break;
        }
    }

    /* Validating user details form */
    private void validateForm ()
    {
        //String name = inputName.getText().toString().trim();
        //String email = inputEmail.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();

        //getting MAC Id of device
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String mac = wInfo.getMacAddress();

        // validating empty name and email
        /*if(name.length() == 0 || email.length() == 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }*/

        // validating mobile number, it should be of 10 digits length
        if(isValidPhoneNumber(mobile))
        {
            // request for sms
            progressBar.setVisibility(View.VISIBLE);

            // saving the mobile number in shared preferences
            //pref.setMobileNumber(mobile);

            // requesting for sms
            //requestForSMS(name, email, mobile, mac);
            requestForSMS(mobile, mac);
        }
        else
            Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
    }

    /* Method initiates the SMS request on the server
     * @param  name    user_name
     * @param  email   user_email_address
     * @param  mobile  user_mobile_number
     * @param  mac     user_mac_address */
    //private void requestForSMS (final String name, final String email, final String mobile, final String mac)
    private void requestForSMS (final String mobile, final String mac)
    {
        final Map<String, String> params = new HashMap<>();
        //params.put("name", name);
        //params.put("email", email);
        params.put("mobile", mobile);
        params.put("mac", mac);

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                Config.URL_REQUEST_SMS,
                new Response.Listener<String>()
                {
                    //response from the server
                    @Override
                    public void onResponse (String response)
                    {
                        Log.d(TAG, response);
                        try
                        {
                            JSONObject responseObj = new JSONObject(response);
                            // Parsing json object response response will be a json object
                            boolean error = responseObj.getBoolean("error");
                            String message = responseObj.getString("message");

                            // checking for error, if not error SMS is initiated device should receive it shortly
                            if(!error)
                            {
                                // boolean flag saying device is waiting for sms
                                pref.setIsWaitingForSms(true);

                                // moving the screen to next pager item i.e otp screen
                                viewPager.setCurrentItem(1);
                                txtEditMobile.setText(pref.getMobileNumber());
                                layoutEditMobile.setVisibility(View.VISIBLE);

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();

                            // hiding the progress bar
                            progressBar.setVisibility(View.GONE);
                        }
                        catch(JSONException e)
                        {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse (VolleyError error)
                    {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
        )
        {

            /* Passing user parameters to our server
             * @return*/
            @Override
            protected Map<String, String> getParams ()
            {
                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    /* sending the OTP to server and activating the user */
    private void verifyOtp ()
    {
        String otp = inputOtp.getText().toString().trim();
        if(!otp.isEmpty())
        {
            Intent grapprIntent = new Intent(getApplicationContext(), HttpService.class);
            grapprIntent.putExtra("otp", otp);
            startService(grapprIntent);
        }
        else
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
    }

    /* Regex to validate the mobile number mobile number should be of 10 digits length
     * @param mobile
     * @return */
    private static boolean isValidPhoneNumber (String mobile)
    {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    class ViewPagerAdapter extends PagerAdapter
    {
        @Override
        public int getCount ()
        {
            return 2;
        }

        @Override
        public boolean isViewFromObject (View view, Object object)
        {
            return view == object;
        }

        public Object instantiateItem (ViewGroup collection, int position)
        {
            int resId = 0;
            switch(position)
            {
                case 0:
                    resId = R.id.layout_sms;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
            }
            return findViewById(resId);
        }
    }
}
