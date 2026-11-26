package com.example.libraryapp.home;
//9. 도서관 API 통신(Naru API)
//line 387, int displayCount = Math.min(processedList.size(), 20); - 10 -> 20 수정
//
//10. UI 업데이트 및 마커 제어
//line 420 - marker.setTag(Lib) 추가
//line 426~431 추가

// <editor-fold desc="[1. 임포트 모음]">
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
//import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.R;
import android.widget.ImageButton;

import com.example.libraryapp.seokwon.data.local.AppDatabase;
import com.example.libraryapp.seokwon.data.local.FavoriteLibrary;

import com.google.android.material.button.MaterialButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



// </editor-fold>

/**
 * 홈 화면 프래그먼트: 지도 표시, 도서관 검색, 내 위치 추적, 길찾기 연동 담당
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    // <editor-fold desc="[2. 멤버 변수 선언 구역]">
    // --- 상수 및 설정 값 ---
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000; // 위치 권한 요청 코드

    // --- 지도 관련 변수 ---
    private FusedLocationSource locationSource; // 네이버 지도 내 위치 소스
    private NaverMap naverMap; // 네이버 지도 객체
    private boolean isFirstLocation = true; // 앱 실행 후 첫 위치 수신 여부 (카메라 이동 제어)
    private double currentUserLat = 37.5665; // 현재 위도 (기본값: 서울)
    private double currentUserLng = 126.9780; // 현재 경도
    // 사용자의 현재 위치에 따른 도서관 지역 코드 (기본값: 서울 11)
    private String userRegionCode = "11";

    // --- UI 컴포넌트 ---
    private View libraryInfoCard; // 하단 도서관 상세 정보 카드 레이아웃
    private TextView infoName, infoDistance, infoAddress, infoTel; // 상세 정보 텍스트뷰들
    private EditText searchEditText; // 검색창
    private Spinner searchTypeSpinner; // 검색 타입 선택 (도서관/도서명)
    private RecyclerView searchResultRecycler; // 검색 결과 리스트
    private MaterialButton btnDetail, btnFindRoute, btnRefreshLocation; // 상세페이지 버튼, 길찾기 버튼, 새로고침 버튼
    private ImageButton btnFavorite; // 즐겨찾기 버튼

    // --- 데이터 및 통신 ---
    private LibraryAdapter libraryAdapter; // 리사이클러뷰 어댑터
    private List<LibraryResponse> searchList = new ArrayList<>(); // 검색 결과 데이터 리스트
    private List<Marker> activeMarkers = new ArrayList<>(); // 지도에 표시된 마커 관리 리스트
    private LibraryResponse selectedLibrary; // 현재 선택된 도서관 정보
    private LibraryService libraryService; // Retrofit 인터페이스

    private AppDatabase db; // Room 데이터베이스

    private com.example.libraryapp.minseong.ui.adapter.BookAdapter bookAdapter; // 민성 책 어댑터

    private String initialSearchQuery = null; // 상세 페이지에서 넘어온 검색어

    // [즐겨찾기 연동] 단일 도서관 정보 저장을 위한 변수 추가
    private LibraryResponse initialLibrary = null;


//    private com.example.libraryapp.seokwon.data.local.AppDatabase db;

    public static HomeFragment newInstance(String searchQuery) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("search_query", searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    // [즐겨찾기 연동] 도서관 클릭 시 지도로 이동하기 위한 팩토리 메서드 추가
    public static HomeFragment newInstanceWithLibrary(String name, double lat, double lng, String address, String tel, String homepage, String opTime, String closedDays) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("lib_name", name);
        args.putDouble("lib_lat", lat);
        args.putDouble("lib_lng", lng);
        args.putString("lib_address", address);
        args.putString("lib_tel", tel);
        args.putString("lib_homepage", homepage);
        args.putString("lib_optime", opTime);
        args.putString("lib_closed", closedDays);
        fragment.setArguments(args);
        return fragment;
    }
    // </editor-fold>

    // <editor-fold desc="[3. 프래그먼트 생명주기 - onCreateView]">
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 프래그먼트 레이아웃 인플레이트
        return inflater.inflate(R.layout.yelin_fragment_home, container, false);
    }
    // </editor-fold>

    // <editor-fold desc="[4. 프래그먼트 생명주기 - onViewCreated]">
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 인자(Arguments) 확인
        if (getArguments() != null) {
            initialSearchQuery = getArguments().getString("search_query");

            // ==========================================
            // [즐겨찾기 연동] 단일 도서관 정보가 넘어온 경우 객체로 복원
            // ==========================================
            if (getArguments().containsKey("lib_name")) {
                initialLibrary = new LibraryResponse();
                initialLibrary.name = getArguments().getString("lib_name");
                initialLibrary.latitude = getArguments().getDouble("lib_lat");
                initialLibrary.longitude = getArguments().getDouble("lib_lng");
                initialLibrary.address = getArguments().getString("lib_address");
                initialLibrary.tel = getArguments().getString("lib_tel");
                initialLibrary.homepage = getArguments().getString("lib_homepage");
                initialLibrary.operating_time = getArguments().getString("lib_optime");
                initialLibrary.closed_days = getArguments().getString("lib_closed");
            }
            // ==========================================
        }

        // 1. 초기화 메서드 호출
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        initRetrofit(); // API 통신 설정
        db = AppDatabase.getInstance(requireContext()); // DB 인스턴스 가져오기

        // 2. 뷰 바인딩
        libraryInfoCard = view.findViewById(R.id.library_info_card);
        infoName = view.findViewById(R.id.info_name);
        infoDistance = view.findViewById(R.id.info_distance);
        infoAddress = view.findViewById(R.id.info_address);
        infoTel = view.findViewById(R.id.info_tel);
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchTypeSpinner = view.findViewById(R.id.search_type_spinner);
        searchResultRecycler = view.findViewById(R.id.search_result_recycler);
        btnDetail = view.findViewById(R.id.btn_detail);
        btnFindRoute = view.findViewById(R.id.btn_find_route);
        btnRefreshLocation = view.findViewById(R.id.btn_refresh_location); // 버튼 연결
        btnFavorite = view.findViewById(R.id.btn_favorite); // 즐겨찾기 버튼 연결

        // 3. 리사이클러뷰 설정
        searchResultRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        libraryAdapter = new LibraryAdapter(searchList, lib -> selectSingleLibrary(lib)); // 리스트 아이템 클릭 시 처리

        bookAdapter = new com.example.libraryapp.minseong.ui.adapter.BookAdapter(currentUserLat, currentUserLng, userRegionCode);

        searchResultRecycler.setAdapter(libraryAdapter);

        // 4. 지도 프래그먼트 로드 (비동기 처리)
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        // - 현 지도에서 검색 버튼 클릭 이벤트
        btnRefreshLocation.setOnClickListener(v -> {
            if (naverMap != null) {
                LatLng center = naverMap.getCameraPosition().target;
                fetchLibrariesFromServer(center.latitude, center.longitude, null);
                btnRefreshLocation.setVisibility(View.GONE);
            }
        });

        // 5. 검색창 엔터 및 돋보기 아이콘 이벤트 처리
        // (1) 키보드 엔터 키 처리
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch(); // 공통 검색 함수 호출
                return true;
            }
            return false;
        });

        // (2) 화면의 파란색 돋보기 아이콘 클릭 처리
        View searchIcon = view.findViewById(R.id.search_icon);
        if (searchIcon != null) {
            searchIcon.setOnClickListener(v -> {
                performSearch(); // 아이콘 눌러도 검색 실행
            });
        }

        // 6. 상세페이지 버튼 클릭 이벤트
        btnDetail.setOnClickListener(v -> {
            if (selectedLibrary != null) {
                Intent intent = new Intent(getContext(), HomeLibraryDetailActivity.class);
                intent.putExtra("lib_name", selectedLibrary.name);
                intent.putExtra("lib_homepage", selectedLibrary.homepage);
                intent.putExtra("operating_time", selectedLibrary.operating_time);
                intent.putExtra("closed_days", selectedLibrary.closed_days);
                startActivity(intent);
            }
        });

        // 7. 길찾기 버튼 클릭 이벤트 (외부 네이버 지도 앱 연동)
        btnFindRoute.setOnClickListener(v -> {
            if (selectedLibrary != null) {
                openNaverMap(selectedLibrary.latitude, selectedLibrary.longitude, selectedLibrary.name);
            } else {
                Toast.makeText(getContext(), "도서관을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 8. 도서관 즐겨찾기 버튼 클릭 이벤트
        btnFavorite.setOnClickListener(v -> {
            if (selectedLibrary != null) {
                toggleLibraryFavorite();
            }
        });

        // 자동 검색 처리 (상세 화면에서 넘어온 경우)
        if (initialSearchQuery != null && !initialSearchQuery.isEmpty()) {
            searchEditText.setText(initialSearchQuery);
            // 검색 타입을 '도서명'으로 설정
            if (searchTypeSpinner.getCount() > 1) {
                searchTypeSpinner.setSelection(1); // 0: 도서관, 1: 도서명
            }
            // 뷰 초기화 완료 후 검색 실행
            searchEditText.post(() -> performSearch());
        }
    }
    // </editor-fold>

    // <editor-fold desc="[5. 길찾기 유틸리티 (외부 앱 연동)]">
    /**
     * 네이버 지도 앱 혹은 웹을 통해 길찾기 기능을 실행합니다.
     */
    private void openNaverMap(double dlat, double dlng, String dname) {
        // 1. [앱 전용] 네이버 지도 앱 설치 시 실행될 URI
        String appUrl = "nmap://route/public?dlat=" + dlat + "&dlng=" + dlng + "&dname=" + dname + "&appname=" + requireContext().getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        // 기기에 네이버 지도 앱이 있는지 체크
        List<ResolveInfo> list = requireContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list != null && !list.isEmpty()) {
            // 앱이 있으면 실행 (좌표를 그대로 사용)
            startActivity(intent);
        } else {
            // 2. [웹 전용] 앱이 없을 때 실행될 브라우저용 URL
            String encodedName = Uri.encode(dname);
            String webUrl = "https://m.map.naver.com/route.nhn?ename=" + encodedName + "&ex=" + dlng + "&ey=" + dlat + "&menu=route";

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
        }
    }
    // </editor-fold>

    // <editor-fold desc="[6. Retrofit 초기화]">
    /**
     * Retrofit 초기 설정을 수행합니다. (도서관 정보나루 API 연동용)
     */
    private void initRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.196.136.121/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        libraryService = retrofit.create(LibraryService.class);
    }
    // </editor-fold>

    // <editor-fold desc="[7. 네이버 지도 콜백 및 리스너 (onMapReady)]">
    /**
     * 지도가 준비되었을 때 호출되는 콜백
     */
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource); // 내 위치 소스 설정
        checkLocationPermission(); // 권한 확인
        naverMap.getUiSettings().setLocationButtonEnabled(true); // 내 위치 버튼 활성화

        // [즐겨찾기 연동] 즐겨찾기에서 넘어온 도서관이 있다면 즉시 핀 표시
        if (initialLibrary != null) {
            isFirstLocation = false; // 내 위치로 자동 이동 방지
            selectSingleLibrary(initialLibrary); // 이미 구현된 핀 강조 및 카드 표시 기능 호출
        }

        //지도 카메라 이동 감지 리스너
        naverMap.addOnCameraChangeListener((reason, animated) -> {
            // 사용자가 손가락으로 움직였을 때만 버튼 표시
            if (reason == -1) {
                btnRefreshLocation.setVisibility(View.VISIBLE);
            }
        });

        // 지도 빈 곳 클릭 시 검색 결과 리스트 닫기
        naverMap.setOnMapClickListener((point, latLng) -> {
            if (searchResultRecycler.getVisibility() == View.VISIBLE) {
                searchResultRecycler.setVisibility(View.GONE);
                hideKeyboard(searchEditText);
                searchEditText.clearFocus();
            }
            btnRefreshLocation.setVisibility(View.GONE); // 지도 클릭 시 버튼 숨김
            libraryInfoCard.setVisibility(View.GONE); // 카드도 같이 닫아주면 깔끔합니다.
        });

        // 실시간 위치 변경 리스너
        naverMap.addOnLocationChangeListener(location -> {
            currentUserLat = location.getLatitude();
            currentUserLng = location.getLongitude();

            // 앱 실행 후 최초 1회만 내 위치로 카메라 이동 및 주변 도서관 자동 검색
            if (isFirstLocation && currentUserLat != 0) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(currentUserLat, currentUserLng))
                        .animate(CameraAnimation.Easing);
                naverMap.moveCamera(cameraUpdate);

                fetchLibrariesFromServer(currentUserLat, currentUserLng, null);

                new Thread(() -> {
                    try {
                        // 프래그먼트가 화면에 붙어있을 때만 Context 접근 (에러 방지)
                        if (isAdded() && getContext() != null) {
                            android.location.Geocoder geocoder = new android.location.Geocoder(getContext(), java.util.Locale.KOREA);
                            java.util.List<android.location.Address> addresses = geocoder.getFromLocation(currentUserLat, currentUserLng, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String adminArea = addresses.get(0).getAdminArea(); // 예: "서울특별시"
                                userRegionCode = getRegionCode(adminArea);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // 주소 변환 실패 시 기본값(11) 유지
                    }
                }).start();

                isFirstLocation = false;
            }

//            try {
//                android.location.Geocoder geocoder = new android.location.Geocoder(requireContext(), java.util.Locale.KOREA);
//                java.util.List<android.location.Address> addresses = geocoder.getFromLocation(currentUserLat, currentUserLng, 1);
//                if (addresses != null && !addresses.isEmpty()) {
//                    String adminArea = addresses.get(0).getAdminArea(); // 예: "서울특별시", "충청남도"
//                    userRegionCode = getRegionCode(adminArea);
//                }
//            } catch (Exception e) {
//                e.printStackTrace(); // 주소 변환 실패 시 기본값(11) 유지
//            }
        });
    }
    // </editor-fold>

    // <editor-fold desc="[8. 위치 권한 처리]">
    /**
     * 위치 권한 상태를 확인하고 요청합니다.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow); // 권한 있으면 추적 시작
        } else {
            // 권한 없으면 팝업 요청
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 권한 요청 결과에 따른 처리
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (locationSource.isActivated()) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow); // 권한 허용 시 추적 모드
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // </editor-fold>

    // <editor-fold desc="[9. 도서관 API 통신 (Naru API)]">
    /**
     * 서버에서 도서관 목록을 가져오고 거리순으로 정렬합니다.
     */
    private void fetchLibrariesFromServer(double userLat, double userLng, String query) {
        // [Step 1] 전체 개수(numFound)만 먼저 확인하기 위해 1개만 요청
        libraryService.getLibraries(query, 1, 1, "json")
                .enqueue(new Callback<LibraryNaruResponse>() {
                    @Override
                    public void onResponse(Call<LibraryNaruResponse> call, Response<LibraryNaruResponse> response) {
                        if (!isAdded() || response.body() == null || response.body().response == null) return;

                        // API 결과에서 '검색된 총 개수'를 알아냅니다.
                        int totalCount = response.body().response.numFound;

                        // [Step 2] 알아낸 전체 개수만큼 데이터를 본격적으로 요청
                        if (totalCount > 0) {
                            fetchAllData(userLat, userLng, query, totalCount);
                        } else {
                            // 검색 결과가 0개일 경우 처리
                            updateUI(new ArrayList<>(), query);
                        }
                    }

                    @Override
                    public void onFailure(Call<LibraryNaruResponse> call, Throwable t) {
                        if (isAdded()) Toast.makeText(getContext(), "연결 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 진짜 데이터를 긁어와서 처리하는 함수
    private void fetchAllData(double userLat, double userLng, String query, int totalCount) {
        boolean isSearchMode = (query != null && !query.isEmpty());

        // 데이터는 일단 다 가져오되 (거리 계산을 위해), UI에는 10개만 뿌립니다.
        libraryService.getLibraries(query, 1, totalCount, "json")
                .enqueue(new Callback<LibraryNaruResponse>() {
                    @Override
                    public void onResponse(Call<LibraryNaruResponse> call, Response<LibraryNaruResponse> response) {
                        if (!isAdded() || response.body() == null || response.body().response == null) return;

                        List<LibraryNaruResponse.LibWrapper> libs = response.body().response.libs;
                        if (libs == null) return;

                        List<LibraryResponse> processedList = new ArrayList<>();
                        for (LibraryNaruResponse.LibWrapper wrapper : libs) {
                            LibraryNaruResponse.LibraryItem item = wrapper.lib;

                            // 이름 검색 모드일 때 필터링
                            if (isSearchMode) {
                                String q = query.replace(" ", "");
                                String n = (item.libName != null) ? item.libName.replace(" ", "") : "";
                                String a = (item.address != null) ? item.address.replace(" ", "") : "";

                                // 이름에도 없고 주소에도 없으면, 다음 데이터로 건너뜀.
                                if (!n.contains(q) && !a.contains(q)) {
                                    continue;
                                }
                            }
                            try {
                                double libLat = Double.parseDouble(item.latitude);
                                double libLng = Double.parseDouble(item.longitude);

                                LibraryResponse res = new LibraryResponse();
                                res.name = item.libName;
                                res.latitude = libLat;
                                res.longitude = libLng;
                                // 현재 위치(또는 지도 중앙)와의 거리 계산
                                res.distance = getHaversineDistance(userLat, userLng, libLat, libLng);
                                res.address = item.address;
                                res.tel = item.tel;
                                res.homepage = item.homepage;
                                res.operating_time = item.operatingTime;
                                res.closed_days = item.closed;
                                processedList.add(res);
                            } catch (Exception ignored) {}
                        }

                        // [중요] 거리순 정렬 (가까운 곳이 위로 오게)
                        Collections.sort(processedList, (o1, o2) -> Double.compare(o1.distance, o2.distance));

                        // [수정] 결과가 20개보다 많으면 상위 20개만 자르고, 적으면 전체를 보여줌
                        int displayCount = Math.min(processedList.size(), 20);
                        List<LibraryResponse> top10List = processedList.subList(0, displayCount);

                        // UI 업데이트 (상위 20개만 넘김)
                        updateUI(top10List, query);
                    }

                    @Override
                    public void onFailure(Call<LibraryNaruResponse> call, Throwable t) {
                        if (isAdded()) Toast.makeText(getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // </editor-fold>

    // <editor-fold desc="[10. UI 업데이트 및 마커 제어]">
    /**
     * 검색 결과를 리스트와 지도 마커로 업데이트합니다.
     */
    private void updateUI(List<LibraryResponse> libraries, String query) {
        if (naverMap == null) return;
        clearMarkers(); // 이전 마커 제거
        searchList.clear();
        searchList.addAll(libraries);

//      도서관 어댑터로 원상복구
        searchResultRecycler.setAdapter(libraryAdapter);

        libraryAdapter.notifyDataSetChanged();

        // 검색어 입력 시에만 결과 리스트를 보여줌
        searchResultRecycler.setVisibility((query != null && !query.isEmpty()) ? View.VISIBLE : View.GONE);
        libraryInfoCard.setVisibility(View.GONE);

        // 지도에 마커 추가
        for (LibraryResponse lib : libraries) {
            Marker marker = new Marker();
            marker.setPosition(new LatLng(lib.latitude, lib.longitude));
            marker.setCaptionText(lib.name);
            marker.setTag(lib);
            marker.setMap(naverMap);
            marker.setOnClickListener(o -> {
                selectSingleLibrary((LibraryResponse) o.getTag()); // 마커 클릭 시 해당 도서관 선택
                return true;
            });
            activeMarkers.add(marker);
        }
    }

    /**
     * 리스트 아이템이나 마커 클릭 시 하나의 도서관을 강조 표시합니다.
     */
    private void selectSingleLibrary(LibraryResponse lib) {
        if (naverMap == null) return;
        clearMarkers();
        selectedLibrary = lib;

        // 선택된 도서관 전용 마커
        Marker selectedMarker = new Marker();
        selectedMarker.setPosition(new LatLng(lib.latitude, lib.longitude));
        selectedMarker.setCaptionText(lib.name);

        // 이 마커를 다시 눌렀을 때도 카드가 나옴
        selectedMarker.setOnClickListener(o -> {
            showLibraryCard(lib); // 카드 다시 보여주기
            return true;
        });

        selectedMarker.setMap(naverMap);
        activeMarkers.add(selectedMarker);

        // 카메라 이동
        naverMap.moveCamera(CameraUpdate.scrollTo(new LatLng(lib.latitude, lib.longitude)).animate(CameraAnimation.Easing));

        showLibraryCard(lib); // 상세 정보 카드 표시
        searchResultRecycler.setVisibility(View.GONE); // 리스트 숨기기
        hideKeyboard(searchEditText);
        searchEditText.clearFocus();
        btnRefreshLocation.setVisibility(View.GONE); // 상세 정보 볼 때 버튼 숨김
    }

    /**
     * 지도상의 모든 마커를 제거합니다.
     */
    private void clearMarkers() {
        for (Marker m : activeMarkers) m.setMap(null);
        activeMarkers.clear();
    }

    /**
     * 도서관 상세 정보를 카드 레이아웃에 표시합니다.
     */
    private void showLibraryCard(LibraryResponse lib) {
        infoName.setText(lib.name);
        infoDistance.setText(lib.distance + "km");
        infoAddress.setText(lib.address);
        infoTel.setText(lib.tel);
        libraryInfoCard.setVisibility(View.VISIBLE);
        updateFavoriteButtonStatus(); // 즐겨찾기 버튼 상태 업데이트
    }

    /**
     * 도서관 즐겨찾기 버튼의 상태를 업데이트합니다.
     */
    private void updateFavoriteButtonStatus() {
        if (selectedLibrary == null) return;
        new Thread(() -> {
            boolean isFavorite = db.favoriteLibraryDao().isFavorite(selectedLibrary.name);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (isFavorite) {
                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                        btnFavorite.setColorFilter(android.graphics.Color.parseColor("#FFD700")); // 즐겨찾기 시 황금색
                    } else {
                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                        btnFavorite.setColorFilter(android.graphics.Color.parseColor("#757575")); // 즐겨찾기 전 회색
                    }
                });
            }
        }).start();
    }

    /**
     * 도서관 즐겨찾기 상태를 토글합니다.
     */
    private void toggleLibraryFavorite() {
        if (selectedLibrary == null) return;
        new Thread(() -> {
            boolean isFavorite = db.favoriteLibraryDao().isFavorite(selectedLibrary.name);
            if (isFavorite) {
                db.favoriteLibraryDao().deleteByName(selectedLibrary.name);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show());
                }
            } else {
                FavoriteLibrary newFavorite = new FavoriteLibrary(
                        selectedLibrary.name,
                        selectedLibrary.address,
                        selectedLibrary.latitude,
                        selectedLibrary.longitude,
                        selectedLibrary.tel,
                        selectedLibrary.homepage,
                        selectedLibrary.operating_time,
                        selectedLibrary.closed_days
                );
                db.favoriteLibraryDao().insert(newFavorite);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show());
                }
            }
            // UI 업데이트
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::updateFavoriteButtonStatus);
            }
        }).start();
    }


    // </editor-fold>

    // <editor-fold desc="[11. 기타 유틸리티 (거리계산/키보드)]">
    /**
     * 하버사인 공식을 이용한 두 지점 사이의 직선 거리를 계산합니다. (단위: km)
     */
    private double getHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return Math.round(R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 100.0) / 100.0;
    }

    /**
     * 소프트 키보드를 숨깁니다.
     */
    private void hideKeyboard(View v) {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    // </editor-fold>

    // <editor-fold desc="[12. 민성님 도서 검색 로직 연동]">
    /**
     * 도서명으로 검색 (팀원이 구현할 영역)
     */
    private void searchByBookTitle(String bookTitle) {
        if (bookTitle.isEmpty()) return;

        // 🚨 [새로 추가됨] 검색 버튼을 누른 순간의 '최신 내 위치'를 담아서 어댑터를 새로 고침합니다!
        bookAdapter = new com.example.libraryapp.minseong.ui.adapter.BookAdapter(currentUserLat, currentUserLng, userRegionCode);

        // 1. 리스트의 관리자를 '책 어댑터'로 교체
        searchResultRecycler.setAdapter(bookAdapter);
        searchResultRecycler.setVisibility(View.VISIBLE);
        libraryInfoCard.setVisibility(View.GONE); // 도서관 상세 카드는 숨김
        clearMarkers(); // 책 검색 중이므로 지도 위 도서관 마커들 싹 지우기

        // 2. 도서 검색 API 호출
        com.example.libraryapp.minseong.network.RetrofitClient.getApiService()
                .getBookLoanStatus(bookTitle, "json", 20)
                .enqueue(new Callback<com.example.libraryapp.minseong.model.BookResponse>() {
                    @Override
                    public void onResponse(Call<com.example.libraryapp.minseong.model.BookResponse> call, Response<com.example.libraryapp.minseong.model.BookResponse> response) {
                        // '유령 상태 방지(Crash 방지)' 로직
                        if (!isAdded() || getContext() == null) return;

                        if (response.isSuccessful() && response.body() != null) {
                            List<com.example.libraryapp.minseong.model.Book> books = response.body().getBooks();

                            if (books == null || books.isEmpty()) {
                                Toast.makeText(getContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 3. 어댑터에 책 데이터 넘겨주기
                                bookAdapter.setBookList(books);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.libraryapp.minseong.model.BookResponse> call, Throwable t) {
                        if (!isAdded() || getContext() == null) return;
                        Toast.makeText(getContext(), "통신 에러: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // </editor-fold>

    // <editor-fold desc="[13. 공통 검색 수행 로직 (performSearch)]">
    /**
     * 검색 수행 공통 로직
     */
    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        String searchType = searchTypeSpinner.getSelectedItem().toString();

        if (!query.isEmpty()) {
            String cleanQuery = query;
            if (cleanQuery.contains(":")) {
                cleanQuery = cleanQuery.split(":")[0];
            }
            String processedQuery = cleanQuery.replace(" ", "");

            if (searchType.equals("도서관")) {
                fetchLibrariesFromServer(currentUserLat, currentUserLng, processedQuery); // 도서관 검색
            } else {
                searchByBookTitle(processedQuery); // 도서명 검색
            }
        }
        searchEditText.clearFocus();
        hideKeyboard(searchEditText); // 키보드 숨기기
    }

    // <editor-fold desc="[14. 행정구역 지역코드 변환]">
    /**
     * 위도/경도로 알아낸 행정구역 이름("경기도", "부산광역시" 등)을 도서관 API용 지역코드("31", "21")로 변환
     */
    private String getRegionCode(String adminArea) {
        if (adminArea == null) return "11";

        if (adminArea.contains("서울")) return "11";
        if (adminArea.contains("부산")) return "21";
        if (adminArea.contains("대구")) return "22";
        if (adminArea.contains("인천")) return "23";
        if (adminArea.contains("광주")) return "24";
        if (adminArea.contains("대전")) return "25";
        if (adminArea.contains("울산")) return "26";
        if (adminArea.contains("세종")) return "29";
        if (adminArea.contains("경기")) return "31";
        if (adminArea.contains("강원")) return "32";
        if (adminArea.contains("충청북도") || adminArea.contains("충북")) return "33";
        if (adminArea.contains("충청남도") || adminArea.contains("충남")) return "34";
        if (adminArea.contains("전라북도") || adminArea.contains("전북")) return "35";
        if (adminArea.contains("전라남도") || adminArea.contains("전남")) return "36";
        if (adminArea.contains("경상북도") || adminArea.contains("경북")) return "37";
        if (adminArea.contains("경상남도") || adminArea.contains("경남")) return "38";
        if (adminArea.contains("제주")) return "39";

        return "11"; // 못 찾으면 기본값 서울
    }
    // </editor-fold>
}