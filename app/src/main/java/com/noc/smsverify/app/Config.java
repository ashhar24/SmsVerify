package com.noc.smsverify.app;

/*
 * Created by defoliate on 14-10-2015.
 */
public class Config
{
    // server URL configuration
    public static final String URL_REQUEST_SMS = "http://192.168.0.107:8000/regis/phone/";
    public static final String URL_VERIFY_OTP = "http://192.168.0.107:8000/o/token/";
    public static final String URL_SUBMIT_CRED = "http://192.168.0.107:8000/register/credentials/";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "ANHIVE";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
}
