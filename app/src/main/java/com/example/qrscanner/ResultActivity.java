package com.example.qrscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.secureQR.module.SecureQR;

import java.io.File;

public class ResultActivity extends AppCompatActivity {

    String url = "";
    String scannedData_s = "";

    TextView urlText;
    TextView authMessage;
    TextView scannedData;
    Button connectButton;
    LottieAnimationView checkAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        init();
        setContents();
        buttonListener();
    }

    private void init() {
        urlText = findViewById(R.id.URL);
        authMessage = findViewById(R.id.AuthMessage);
        scannedData = findViewById(R.id.scannedData);
        connectButton = findViewById(R.id.connectButton);
        checkAni = findViewById(R.id.checkAnimation);
    }

    private void setContents() {
        Intent intent = getIntent();
        scannedData_s = intent.getStringExtra("scannedData");
        url = intent.getStringExtra("url");
        int isAuthQR = intent.getIntExtra("isAuthQR", 0);

        if (isAuthQR == SecureQR.IsAuthQR) {

            // FAIL CHECKING
            if (url.equals(SecureQR.FAIL_DECRYPT) || url.equals(SecureQR.FAIL_HASH) || url.equals(SecureQR.FAIL_DATA)) {
                authMessage.setText("보안 QR 코드에 문제가 있습니다.");
                checkAni.setAnimation(R.raw.alert);
                checkAni.playAnimation();
            } else if(url.equals(SecureQR.FAIL_INDEX)) {
                authMessage.setText("보안 QR 서버에 문제가 있습니다.");
                checkAni.setAnimation(R.raw.alert);
                checkAni.playAnimation();
            } else {
                authMessage.setText("보안 QR 코드 입니다.");
                checkAni.setAnimation(R.raw.check);
                checkAni.loop(false);
                checkAni.playAnimation();
            }
        } else {
            authMessage.setText("보안 QR 코드가 아닙니다.");
            checkAni.playAnimation();
        }

        urlText.setText(url);
        scannedData.setText(scannedData_s);
    }

    private void buttonListener() {
        connectButton.setOnClickListener(v -> {
            openCustomTab(url);
        });
    }

    // 인터넷 창(Chrome Custom Tabs) 띄우기
    private void openCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
}