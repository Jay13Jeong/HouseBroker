# HouseBroker
An introductory service for real estate agents
## 소개
공인중개사가 부동산중개를 원하는 고객들을 대상으로 소개하거나 상담할 수 있는 개인용 홈페이지입니다.

## 기술 스택
<div align=center> 
<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=black">
<img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white">
<img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=TypeScript&logoColor=white">
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
</div>

## 구현 기능
- 매물게시 - 유형별 매물게시, 매물 검색, 지도에서 부동산 위치 둘러보기, 부동산 상세정보 보기</br>
- 매물관리 - 부동산 올리기, 부동산정보 수정, 부동산 게시물 내리기, 부동산 게시물 끌어올리기</br>
- 상담문의 - 관리자에게 문의, 실시간채팅, 2명이상 단체 채팅, 채팅방 지우기</br>

## Installation & Execution
프로젝트는 docker-compose로 구성되어 있습니다.

google api의 클라이언트 아이디와 시크릿키를 준비해야하며 없다면 발급해야 합니다.</br>
Google api발급하기 >>> [google](https://console.cloud.google.com/apis/credentials)</br>

프로젝트를 실행시키는 방법은 다음과 같습니다.
1. .env_template 파일을 통해 .env를 설정해줍니다. 설정해야 하는 항목은 다음과 같습니다.  
   - DB_USER= 디비 초기화 유저.
   - DB_PASSWORD= 디비 초기화 유저 비번.
   - DB_ROOT_PASSWORD= 디비 초기화 루트 비번.
   - SERVER_HOST= 서버의 호스트 네임
   - DOMAIN= 서비스 url (http 또는 https포함, 특별한 포트를 이용한다면 포트번호도 포함) 
   - APP_NAME= 프론트앱에서 표시 될 서비스 이름.
   - APP_LOCATION= 하단의 주소지 표기.
   - APP_PHONE_INFO= 하단의 전화번호 표기.
   - APP_PHONE_NUMBER= 통화아이콘 누르면 연결될 전화번호.
   - ADMIN_EMAIL= 관리자이메일 목록(','로 구분한다).
   - GOOGLE_MAIL_UNAME= smtp용 메일 주소 ID
   - GOOGLE_MAIL_PWD= smtp용 메일 주소 PASSWORD
   - GOOGLE_AUTH_CLIENT_ID= 구글api 클라이언트id
   - GOOGLE_ACCESS_SECRET= 구글api 클라이언트pwd
   - GOOGLE_AUTH_CALLBACK_URL= 구글api 클라이언트 콜백주소
   - KAKAO_MAP_KEY=카카오맵api 자바스크립트 키
   - MY_LOCATE_X= 사무실 좌표의 경도
   - MY_LOCATE_Y= 사무실 좌표의 위도
   
2. make 합니다.

## Browser Support
이 프로젝트는 크롬브라우저에서 테스트되었습니다.

## Screenshot
<img width="1712" alt="스크린샷 2024-01-20 오후 6 22 21" src="https://github.com/Jay13Jeong/HouseBroker/assets/63899204/c0a0e6fe-ba88-4588-915a-7e421f03fe6a">
<img width="1723" alt="스크린샷 2024-01-20 오후 6 22 53" src="https://github.com/Jay13Jeong/HouseBroker/assets/63899204/4d8fa769-835a-43ee-a983-ba40a6e368c2">
<img width="1590" alt="스크린샷 2024-01-20 오후 6 23 24" src="https://github.com/Jay13Jeong/HouseBroker/assets/63899204/2a21c3e7-ec5e-4f61-b224-bd9e25309ca9">
<img width="1574" alt="스크린샷 2024-01-20 오후 6 23 12" src="https://github.com/Jay13Jeong/HouseBroker/assets/63899204/fb3a5da3-4494-424e-9db6-adef511f81f2">
