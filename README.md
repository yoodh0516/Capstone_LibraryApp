# 도서관 통합 플랫폼 앱 (졸업작품)

전국 도서관 통합 검색, 실시간 대출 가능 여부 확인, 지도 기반 도서관 찾기, 사용자 커뮤니티를 제공하는 안드로이드 앱입니다. 남서울대학교 컴퓨터소프트웨어학과 캡스톤디자인(졸업작품) 팀 프로젝트입니다.

- 기간: 2025.09 ~ 2026.05
- 인원: 4명 (팀장 1, 팀원 3) — 팀 프로젝트

## 구성

| 위치 | 내용 |
|---|---|
| `community/` | 회원가입·로그인, 커뮤니티 게시글·댓글 |
| `home/` | 홈 화면, 도서관 목록·상세, 네이버 지도 |
| `minseong/` | 도서 검색, 마이페이지, 비밀번호 변경, 개인정보 설정 |
| `seokwon/` | 인기 도서, 도서 상세, 즐겨찾기(Room) |
| `MainActivity.java` | 하단 내비게이션, 로그인 상태 확인·로그아웃 |

(경로 기준: `app/src/main/java/com/example/libraryapp/`)

**기술**: Java, Kotlin, Android Studio, Firebase(Authentication, Firestore), Retrofit, Room, 네이버 지도 SDK, 도서관 정보나루 API(Django 프록시 서버 경유)

## 담당 파트 (유동호, 팀장)

> 통합 과정에서 팀원 코드와 합쳐져, 아래 경로는 해당 기능이 최종적으로 들어 있는 위치입니다.

- **팀장 및 프로젝트 통합 관리**: 역할 분담, 일정 관리, 팀원 작업 코드 통합
- **Firebase 사용자 인증**: 이메일·비밀번호 회원가입(Firestore `users`에 닉네임 저장), 로그인, 로그인 상태 확인 및 로그아웃
  - 관련 위치: `community/SignupActivity.java`, `community/LoginActivity.java`, `MainActivity.java`
- **Firestore 커뮤니티**: 게시글 작성·삭제(작성자 본인만), 댓글 작성·조회, `addSnapshotListener` 기반 실시간 게시글 목록 갱신
  - 관련 위치: `community/CommunityFragment.java`, `community/WritePostFragment.java`, `community/PostDetailFragment.java`
  - 레이아웃: `app/src/main/res/layout/dongho_*.xml`

## 실행

1. Android Studio로 프로젝트를 엽니다.
2. Firebase 콘솔에서 Android 앱(패키지명 `com.example.libraryapp`)을 등록하고 받은 `google-services.json`을 `app/` 폴더에 넣습니다. (보안을 위해 저장소에는 포함하지 않았습니다.)
3. 네이버 클라우드 플랫폼에서 Maps Client ID를 발급받아 `local.properties`에 추가합니다.

       NAVER_CLIENT_ID=발급받은_Client_ID

4. Gradle Sync 후 실행합니다.

※ 도서 검색·대출 여부 조회는 졸업작품 기간에 운영한 Azure 프록시 서버를 사용하므로, 서버가 종료된 경우 해당 기능은 동작하지 않을 수 있습니다.
