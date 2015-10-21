package com.noc.smsverify.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.noc.smsverify.activity.SmsActivity;

/**
 * Created by defoliate on 14-10-2015.
 */
public class SmsReceiver extends BroadcastReceiver
{
    private static final String TAG = SmsReceiver.class.getSimpleName();

    @Override
    public void onReceive (Context context, Intent intent)
    {

        final Bundle bundle = intent.getExtras();
        try
        {
            if(bundle != null)
            {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for(Object aPdusObj : pdusObj)
                {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    //if (!senderAddress.toLowerCase().contains(Config.SMS_ORIGIN.toLowerCase())) {
                    //    return;
                    // }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);

                    Log.e(TAG, "OTP received: " + verificationCode);

                    /*Intent hhtpIntent = new Intent(context, HttpService.class);
                    hhtpIntent.putExtra("otp", verificationCode);
                    context.startService(hhtpIntent);*/

                    SmsActivity.getOtpFromSMS(verificationCode);
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode (String message)
    {
        int i = message.indexOf('"');
        int j = message.lastIndexOf('"');
        return message.substring(i + 1, j);
    }
}
