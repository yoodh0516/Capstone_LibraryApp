package com.example.libraryapp.home;

import android.content.Intent;
import android.graphics.Color; // Color 클래스 추가 확인
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.libraryapp.R;

public class HomeLibraryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yelin_home_activity_library_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("도서관 상세 정보");
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 1. Intent에서 데이터 한 번만 정확히 꺼내기
        String name = getIntent().getStringExtra("lib_name");
        String homepage = getIntent().getStringExtra("lib_homepage");
        String opTime = getIntent().getStringExtra("operating_time");
        String clDays = getIntent().getStringExtra("closed_days");

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvHomepage = findViewById(R.id.tvDetailHomepage);
        TextView tvOpTime = findViewById(R.id.tvDetailOperating);
        TextView tvClDays = findViewById(R.id.tvDetailClosed);

        tvName.setText(name);
        tvOpTime.setText(opTime != null && !opTime.isEmpty() ? opTime : "운영시간 정보가 없습니다.");
        tvClDays.setText(clDays != null && !clDays.isEmpty() ? clDays : "휴관일 정보가 없습니다.");

        // 2. 홈페이지 정보 표시 및 클릭 이벤트 처리
        if (homepage != null && !homepage.isEmpty() && !homepage.equals("null")) {
            tvHomepage.setText(homepage);
            tvHomepage.setTextColor(Color.BLUE);

            tvHomepage.setOnClickListener(v -> {
                String url = homepage.trim();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "웹 브라우저를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvHomepage.setText("홈페이지 정보가 없습니다.");
            tvHomepage.setTextColor(Color.GRAY);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}