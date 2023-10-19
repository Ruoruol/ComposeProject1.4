package com.example.composeproject1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Task extends AsyncTask<String, Void, String> {
    private TextView tv_show;
    private Context context;
    //获取的API接口
    private static final String url = "https://data.moenv.gov.tw/dataset/detail/AQX_P_432";
    //获取的API的key
    private static final String key = "595c99b1-c44e-43e7-9398-a010e424e212";

    public Task(Context context, TextView tv_show) {
        this.context = context;
        this.tv_show = tv_show;
    }

    /***
     *
     * 通过API请求数据
     * @param strings :从主线程中执行子线程传入的字符串，即cityName
     * @return        :请求API之后返回的JSON格式数据
     */
    @Override
    protected String doInBackground(String... strings) {
        String city = strings[0];

        OkHttpClient client = new OkHttpClient();

        // 替换为模拟错误的 URL
        String apiUrl = "https://data.moenv.gov.tw/api/v2/aqx_p_432?api_key=595c99b1-c44e-43e7-9398-a010e424e212"; // 模拟错误的 URL

        // 构建请求对象
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .build();

        try {
            // 执行请求
            Log.d("MyApp", "Sending network request...");
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // 读取响应数据
                String result = response.body().string();
                Log.d("MyApp", "Network request successful"+result);
                return result;
            } else {
                // 处理错误情况
                // 获取错误状态码
                int errorCode = response.code();

                // 获取错误消息
                String errorMessage = response.message();

                // 在日志中记录错误信息
                Log.e("NetworkError", "Error code: " + errorCode + ", Message: " + errorMessage);

                // 根据错误情况提供反馈给用户
                if (errorCode == 404) {
                    // 显示资源未找到的错误消息
                    Log.d("MyApp", "Resource not found");
                    return "Resource not found";
                } else {
                    // 显示通用错误消息
                    Log.d("MyApp", "An error occurred");
                    return "An error occurred";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 网络请求发生异常
            // 在日志中记录异常信息
            Log.e("NetworkError", "Request error: " + e.getMessage());
            // 返回错误消息
            Log.d("MyApp", "Network request failed");
            return "Network request failed";
        }
    }




    /***
     *
     * 解析返回的数据
     * @param s   :请求之后返回的数据
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s!=null){
            try {
                JSONObject jsonObject=new JSONObject(s);
                int resultCode=jsonObject.getInt("resultcode");
                if (resultCode==200){
                    JSONArray resultArray=jsonObject.getJSONArray("result");
                    JSONObject resultJsonObject = resultArray.getJSONObject(0);
                    JSONObject A= (JSONObject) resultJsonObject.get("citynow");

                    String division="---------------------------------------------";




                }
                else if (resultCode==202){
                    String reason=jsonObject.getString("reason");
                    tv_show.setText(reason);
                }
                else{
                    Toast.makeText(context, "查詢失敗",
                            Toast.LENGTH_LONG).show();
                    tv_show.setText("");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(context, "查詢失敗",
                    Toast.LENGTH_LONG).show();
            tv_show.setText("");
        }
    }
}