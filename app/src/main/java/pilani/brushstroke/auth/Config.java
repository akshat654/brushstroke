package pilani.brushstroke.auth;

public class Config {
    //URLs to register.php and confirm.php file
    public static final String GENERATE_URL = "https://svatsa159.000webhostapp.com/AndroidOTP/GenerateOTP.php";
    public static final String CONFIRM_URL = "https://svatsa159.000webhostapp.com/AndroidOTP/ConfirmOTP.php";

    //Keys to send username, password, phone and otp
    public static final String KEY_PHONE = "phone";
    public static final String KEY_OTP = "otp";

    //JSON Tag from response from server
    public static final String TAG_RESPONSE= "ErrorMessage";
}
