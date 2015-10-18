package com.noc.smsverify.app;

/**
 * Created by defoliate on 14-10-2015.
 */
public class Config
{
    // server URL configuration
    public static final String URL_REQUEST_SMS = "http://192.168.0.105:8000/register/phone/";
    public static final String URL_VERIFY_OTP = "http://192.168.0.101/android_sms/msg91/verify_otp.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "ANHIVE";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
}
