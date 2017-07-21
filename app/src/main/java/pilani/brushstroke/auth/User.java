package pilani.brushstroke.auth;

import android.content.SharedPreferences;

public class User {

    private String Email;
    private String Name;
    private String Image_URL;
    private String Mobile;
    private String Street;
    private String City;
    private String State;
    private String Pincode;



    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String MOBILE = "mobile";
    public static final String ADDRESS = "address";
    public static final String STREET = "street";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String IMAGE_URL = "image_url";
    public static final String PINCODE = "pincode";
    public static final String ISLOGIN ="isLogin";
    public static final String Profile ="profileSharedPreferences";

    public User() {
        Email = "";
        Name = "";
        Mobile = "";
        Street = "";
        City = "";
        State = "";
        Pincode = "";
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String pincode) {
        Pincode = pincode;
    }


    public static boolean emailValid(String email){
        String EMAIL_REGEX = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if (email.matches(EMAIL_REGEX)){
            return true;
        }else {
            return false;
        }
    }
    public static boolean fieldValid(String name){
        if (name.length()>0){
            return true;
        } else {
            return false;
        }
    }
    public static boolean passwordValid(String password){
        if (password.length()>=6){
            return true;
        } else {
            return false;
        }
    }
    public static boolean mobileValid(String mobile) {

        if(mobile.length()==10) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveProfileCredentials(SharedPreferences sharedPreferences,
                                            String email,
                                            String name,
                                            String address,
                                              String street,
                                              String city,
                                              String state,
                                              String mobile,
                                              String pincode,
                                              String image) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, email);
        editor.putString(NAME, name);
        editor.putString(ADDRESS,address);
        editor.putString(STREET,street);
        editor.putString(CITY,city);
        editor.putString(STATE,state);
        editor.putString(MOBILE,mobile);
        editor.putString(PINCODE,pincode);
        editor.putString(IMAGE_URL,image);
        editor.putBoolean(ISLOGIN,true);
        editor.commit();
    }


    public static void removeProfileCredentials(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, "");
        editor.putString(NAME, "");
        editor.putString(ADDRESS,"");
        editor.putString(STREET,"");
        editor.putString(CITY,"");
        editor.putString(STATE,"");
        editor.putString(MOBILE,"");
        editor.putString(PINCODE,"");
        editor.putString(IMAGE_URL,"");
        editor.putBoolean(ISLOGIN,false);
        editor.commit();
    }
}
