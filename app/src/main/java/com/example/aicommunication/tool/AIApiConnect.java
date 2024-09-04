package com.example.aicommunication.tool;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIApiConnect {
    public static final String API_KEY = "cHrayJd7toswWHTpmgWinx0a";
    public static final String SECRET_KEY = "tTcjaVCu3oeFy256NYgMutyJzpAdBj8r";
    public Response response_main = null;
    private String text = "";
    private final OkHttpClient HTTP_CLIENT;
    private int tokens;

    public AIApiConnect() {
        HTTP_CLIENT = new  OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
                .build();
    }

    public void startAIApiConnect(JSONObject js) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.Companion.create(js.toString() , mediaType);
                try {
                    Request request = new Request.Builder()
                            .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-lite-8k?access_token=" + getAccessToken())
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    response_main = HTTP_CLIENT.newCall(request).execute();
                    JSONObject jso = new JSONObject(response_main.body().string());
                    Log.w("AIApiConnect","Json:\n"+jso);
                    text = jso.getString("result");
                    tokens =jso.getJSONObject("usage").getInt("total_tokens");
                } catch (IOException | JSONException e) {
                    Log.e("AIApiConnect", "请求失败!");
                    text = "请求失败！";
                    tokens = 3;
                }
            }
        }).start();
    }

    public String getText() {
        return text;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTextNull() {
        text = "";
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     */
    private String getAccessToken() {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            return new JSONObject(response.body().string()).getString("access_token");
        } catch (JSONException | IOException e) {
            Log.e("AIApiConnect", "请求签名失败！");
        }
        return "";
    }

}
