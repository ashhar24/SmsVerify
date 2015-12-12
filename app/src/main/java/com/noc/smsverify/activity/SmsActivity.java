package com.noc.smsverify.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.noc.smsverify.receiver.SmsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by defoliate on 14-10-2015.
 */
public class SmsActivity extends Activity implements View.OnClickListener
{
    private static String TAG = SmsActivity.class.getSimpleName();

    private static ViewPager viewPager;
    private static EditText inputOtp;
    private static LinearLayout layoutEditMobile;
    private static Context mContext;
    private static String client_id = "fhHqexqCk8z8HXi5K4ktdrN6sHxgIf4RR9efwk8I";
    private static String client_secret = "BQnvVD12ZSFYydUJDODnJyVl7u5ylGj1jEavx9hKy13bRf89DPQmmsRkWh11jUejtYMn9p9ZucfIoWtXPOGgkGAw3vescl9yIS4jSqBybAewuF0QmTI64cfbpavMAqwG";
    private EditText inputMobile, inputName, inputEmail;
    private ProgressBar progressBar;
    private PrefManager pref;
    private TextView txtEditMobile;

    public static void getOtpFromSMS (String SMSBody)
    {
        inputOtp.setText(SMSBody);
        disableBroadcastReceiver();
        verifyOtp();
    }

    public static void disableBroadcastReceiver ()
    {
        ComponentName receiver = new ComponentName(mContext, SmsReceiver.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(mContext, "Disabled broadcast receiver", Toast.LENGTH_SHORT).show();
    }

    /* sending the OTP to server and activating the user */
    private static void verifyOtp ()
    {
        final String otp = inputOtp.getText().toString().trim();
        if(!otp.isEmpty())
        {
            StringRequest strReq = new StringRequest(
                    Request.Method.POST,
                    Config.URL_VERIFY_OTP,
                    new Response.Listener<String>()
                    {

                        @Override
                        public void onResponse (String response)
                        {
                            Log.d(TAG, response);
                            try
                            {
                                JSONObject responseObj = new JSONObject(response);
                                Log.d(TAG, response);

                                // Parsing json object response
                                // response will be a json object
                                String message = responseObj.getString("success");
                                if(message.equals("404 not found"))
                                {
                                    disableBroadcastReceiver();
                                    viewPager.setCurrentItem(2);
                                    layoutEditMobile.setVisibility(View.GONE);
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }
                            }
                            catch(JSONException e)
                            {
                                Toast.makeText(mContext,
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse (VolleyError error)
                        {
                            Log.e(TAG, "Error: " + error.getMessage());
                            Toast.makeText(mContext,
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
            {

                @Override
                protected Map<String, String> getParams ()
                {
                    Map<String, String> params = new HashMap<>();
                    //params.put("otp", otp);
                    params.put("grant_type", "password");
                    params.put("username", "bahenchod");
                    params.put("password", "poiuytrewq");

                    Log.e(TAG, "Posting params: " + params.toString());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError
                {
                    HashMap<String, String> params = new HashMap<>();
                    String creds = client_id + ":" + client_secret;
                    Log.d(TAG, "creds" + creds);
                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                    params.put("Authorization", auth);
                    Log.d(TAG, "posting auth" + params.toString());
                    return params;
                }
            };

            // Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);
        }
        else
            Toast.makeText(mContext, "Please enter the OTP", Toast.LENGTH_SHORT).show();
    }

    /* Regex to validate the mobile number mobile number should be of 10 digits length
     * @param mobile
     * @return */
    private static boolean isValidPhoneNumber (String mobile)
    {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    private static boolean isvalid_email (String email)
    {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return email.matches(EMAIL_PATTERN);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);
        inputMobile = (EditText) findViewById(R.id.inputMobile);
        inputOtp = (EditText) findViewById(R.id.inputOtp);
        inputName = (EditText) findViewById(R.id.inputName);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtEditMobile = (TextView) findViewById(R.id.txt_edit_mobile);
        layoutEditMobile = (LinearLayout) findViewById(R.id.layout_edit_mobile);

        findViewById(R.id.btn_edit_mobile).setOnClickListener(this);
        findViewById(R.id.btn_request_sms).setOnClickListener(this);
        findViewById(R.id.btn_verify_otp).setOnClickListener(this);
        findViewById(R.id.btn_submit_credentials).setOnClickListener(this);

        // hiding the edit mobile number
        layoutEditMobile.setVisibility(View.GONE);

        pref = new PrefManager(this);

        // Checking for user session if user is already logged in, take him to main activity
        if(pref.isLoggedIn())
        {
            Intent intent = new Intent(SmsActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        viewPager.setAdapter(new ViewPagerAdapter());
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
    }

    @Override
    public void onClick (View view)
    {
        switch(view.getId())
        {
            case R.id.btn_request_sms:
                validateMobileForm();
                break;

            case R.id.btn_verify_otp:
                verifyOtp();
                break;

            case R.id.btn_edit_mobile:
                viewPager.setCurrentItem(0);
                layoutEditMobile.setVisibility(View.GONE);
                break;

            case R.id.btn_submit_credentials:
                submitcredentials();
                break;
        }
    }

    /* Validating user mobile number form */
    private void validateMobileForm ()
    {
        String mobile = inputMobile.getText().toString().trim();

        //getting MAC Id of device
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String mac = wInfo.getMacAddress();

        // validating mobile number, it should be of 10 digits length
        if(isValidPhoneNumber(mobile))
        {
            // request for sms
            progressBar.setVisibility(View.VISIBLE);

            // requesting for sms
            requestForSMS(mobile, mac);
        }
        else
            Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
    }

    /* Method initiates the SMS request on the server
     * @param  mobile  user_mobile_number
     * @param  mac     user_mac_address */
    private void requestForSMS (final String mobile, final String mac)
    {
        final Map<String, String> params = new HashMap<>();
        params.put("phone", mobile);
        //params.put("mac", mac);

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
                            //boolean error = false;//= responseObj.getBoolean("error");
                            String message = responseObj.getString("code");

                            // checking for error, if not error SMS is initiated device should receive it shortly
                            //if(!error)
                            //{
                                // moving the screen to next pager item i.e otp screen
                                viewPager.setCurrentItem(1);
                                //txtEditMobile.setText(pref.getMobileNumber());
                                layoutEditMobile.setVisibility(View.VISIBLE);
                                txtEditMobile.setText(message);
                                mContext = getApplicationContext();
                                enableBroadcastReceiver();

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //}
                            //else
                                //Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();

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

    public void enableBroadcastReceiver ()
    {
        ComponentName receiver = new ComponentName(mContext, SmsReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(this, "Enabled broadcast receiver", Toast.LENGTH_SHORT).show();
    }

    private void submitcredentials ()
    {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();

        if(name.length() == 0 || !isvalid_email(email))
        {
            Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                Config.URL_SUBMIT_CRED,
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
                            String message = responseObj.getString("success");

                            // checking for error, if not error SMS is initiated device should receive it shortly
                            if(!error)
                            {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SmsActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
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

    class ViewPagerAdapter extends PagerAdapter
    {
        @Override
        public int getCount ()
        {
            return 3;
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
                case 2:
                    resId = R.id.layout_credentials;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public void destroyItem (ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }
    }
}
