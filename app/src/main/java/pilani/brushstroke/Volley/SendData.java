package pilani.brushstroke.Volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import pilani.brushstroke.auth.User;

public class SendData extends StringRequest {


    private Map<String,String> params;

    //loginactivity
    public SendData(String url, String email, String password, Response.Listener<String> listener){
        super(Method.POST, url,listener,null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);
    }

    //signup activity
    public SendData(String url, User user,String password, Response.Listener<String> listener){
        super(Method.POST, url,listener,null);
        params = new HashMap<>();
        params.put("name",user.getName());
        params.put("email",user.getEmail());
        params.put("mobile",user.getMobile());
        params.put("address",user.getStreet()+", "+user.getCity()+", "+user.getState());
        params.put("pincode",user.getPincode());
        params.put("password",password);
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
