package com.example.libraryapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.libraryapp.community.LoginActivity;
import com.example.libraryapp.minseong.ui.fragment.MyPageFragment;
import com.example.libraryapp.community.CommunityFragment;
import com.example.libraryapp.home.HomeFragment;
import com.example.libraryapp.seokwon.ui.book.PopularBookFragment;
import com.example.libraryapp.seokwon.ui.bookmark.BookmarkFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public void navigateToHomeWithSearch(String query) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home); // 하단 탭을 홈으로 변경

        HomeFragment homeFragment = HomeFragment.newInstance(query);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }

    public void navigateToHomeWithLibrary(String name, double lat, double lng, String address, String tel, String homepage, String opTime, String closedDays) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home); // 하단 탭을 홈으로 변경

        HomeFragment homeFragment = HomeFragment.newInstanceWithLibrary(name, lat, lng, address, tel, homepage, opTime, closedDays);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        앱이 새로 켜졌을 때만 이전 로그인 기록을 삭제
        if (savedInstanceState == null) { //화면 회전 등이 아닌 진짜 첫 실행을 의미
            FirebaseAuth.getInstance().signOut();
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();      // 홈
            } else if (id == R.id.nav_bestseller) {
                selectedFragment = new PopularBookFragment(); // 인기도서
            } else if (id == R.id.btn_nav_bookmark) {
                selectedFragment = new BookmarkFragment(); // 즐겨찾기
            } else if (id == R.id.nav_community) {
                selectedFragment = new CommunityFragment();  // 커뮤니티`
            } else if (id == R.id.nav_mypage) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(MainActivity.this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                    return false;
                } else {
                    // 로그인이 되어 있다면 마이페이지를 정상적으로 보여줍니다.
                    selectedFragment = new MyPageFragment();
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // 앱 처음 켜졌을 때 기본 화면
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}