# Tetris Android App

간단한 테트리스 안드로이드 앱 프로젝트입니다.

## 기능
- 10x20 보드 기반 테트리스 플레이
- 좌/우 이동, 회전, 소프트 드롭, 하드 드롭
- 줄 제거 및 점수 계산
- 게임 오버/재시작 지원

## 스마트폰에서 바로 설치하려면 (PC 없이)
가능합니다. 핵심은 **APK 파일**을 만들어서 휴대폰에서 설치하는 것입니다.

### 방법 A) 이미 빌드된 APK를 받는 경우 (가장 쉬움)
1. 휴대폰에서 APK 파일(`.apk`) 다운로드
2. Android 설정에서 "알 수 없는 앱 설치 허용" 켜기
3. 다운로드한 APK 탭해서 설치

### 방법 B) GitHub Actions로 APK 자동 빌드 후 휴대폰에서 받기
이 저장소에는 APK 빌드 워크플로우가 포함되어 있습니다. (`.github/workflows/android-apk.yml`)

1. GitHub 앱(또는 모바일 브라우저)에서 이 저장소 열기
2. **Actions** 탭 → **Build Android APK** 선택
3. **Run workflow** 실행
4. 작업이 끝나면 Artifacts에서 `tetris-debug-apk` 다운로드
5. 휴대폰에서 `app-debug.apk` 설치

## Android Studio로 실행하는 방법 (PC가 있을 때)
1. Android Studio에서 프로젝트 열기
2. `app` 모듈 실행

## 참고
- 빌드를 위해 Android SDK와 Gradle 플러그인 다운로드가 가능한 네트워크 환경이 필요합니다.
- 휴대폰 설치 시 Android 버전에 따라 설치 권한 경로(보안 메뉴)가 조금 다를 수 있습니다.
