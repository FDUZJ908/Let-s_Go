package util;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 11437 on 2017/12/1.
 */

public class httpUtil {
    public static void sendHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(address)
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendHttpPost(String address, String json,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        try{
            /*
            RequestBody requestBody = new FormBody.Builder()
                    .build();*/
            RequestBody requestBody=RequestBody.create(JSON,json); //JSON为content的类型,json为content
            Request request=new Request.Builder()
                    .url(address)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
