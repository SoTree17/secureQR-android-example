package com.example.qrscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.qrscanner.data.RequestUrl;
import com.example.qrscanner.data.ResponseUrl;
import com.example.qrscanner.retrofit.RetrofitAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 테스트를 위해 여기에서 호출했음, 최종 위치는 onActivityResult()로 가야될거 같음.
        requestPOST();

        // initialize
        init();
    }

    // Zxing 라이브러리 관련 초기 설정
    private void init() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCaptureActivity(QrReaderActivity.class);
        intentIntegrator.setPrompt("Test");
        intentIntegrator.initiateScan();
    }

    // 서버에 HTTP Request(POST 방식)를 하는 함수
    private void requestPOST() {
        // 요청할 서버의 주소
        String Base_URL = "http://192.168.219.107:8080/api/";

        // Request body에 추가할 데이터
        RequestUrl reqURL = new RequestUrl("index");

        // Retrofit 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 사용자 지정 인터페이스(RetrofitAPI.java)를 Retrofit 라이브러리에 의해 인스턴스로 자동 구현
        RetrofitAPI api = retrofit.create(RetrofitAPI.class);

        // 인터페이스 함수를 호출하여 Call 객체 생성 (이때, body 데이터를 넣어준다.)
        Call<ResponseUrl> call = api.getUrl(reqURL);

        // Call 객체를 통해 서버에 요청
        call.enqueue(new Callback<ResponseUrl>() {

            // 서버에서 응답 성공
            @Override
            public void onResponse(Call<ResponseUrl> call, Response<ResponseUrl> response) {
                Log.e("Response", "onResponse success");

                // Response body에서 데이터 꺼내기
                ResponseUrl result = response.body();
                Toast.makeText(getApplicationContext(), result.getUrl(), Toast.LENGTH_SHORT).show();
            }

            // 서버에서 응답 실패
            @Override
            public void onFailure(Call<ResponseUrl> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onResponse failed", Toast.LENGTH_SHORT).show();
                Log.e("Response", Log.getStackTraceString(t));
            }
        });
    }


    // QR 코드를 인식 후, 데이터 꺼내는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // QrReaderActivity에서 QR 스캔후, 스캔된 데이터는 intent에 담겨 MainActivity로 넘어온다.
        // 그래서 intent에서 result 데이터를 꺼낸다.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            // 결과값이 없으면
            if (result.getContents() == null)
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            // 결과값이 제대로 있으면 Toast 메시지를 통해 출력
            else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}