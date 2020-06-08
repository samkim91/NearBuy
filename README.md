[작품소개] (Introduction)
이 프로젝트는 당근마켓을 카피코딩한 안드로이드 앱이다. 중고나라의 가장 큰 문제라고 할 수 있는 허위매물, 사기 등을 방지하기 위해 직거래를 성사시켜주는 아이디어가 굉장히 감명깊어서 따라 만들어봤다.

 

This project is a Android App copying 당근마켓. The idea that makes direct selling between indivisuals to prevent scam was very impressive, so I made this.

[주요기능] (Main functions)
- 물품 게시/조회/수정/삭제 (Create/Read/Update/Delete post.)
  * 복수 사진 추가 (Adding multi images)
  * 위치 기반 물품 조회 (Showing posts based my location)
  * Haversine Formula, EndlessScroll
- 내 위치 설정 (Set my location)
  * 최대 2개의 내 위치 및 거리를 설정 (Setting 2 locations and distance)
  * Material Chip, Kakao address API, Google Map API
- 실시간 채팅 (Realtime chatting)
  * TCP 통신을 이용한 실시간 1:1 채팅 (Realtime chatting between individuals using TCP)
  * TCP, MultiThread, Android Service, Android Broadcast, Notification
[부가기능] (others)
- 회원가입/로그인, 비밀번호 찾기 (Sign up, Sign in, Find password)
  * SMS 본인 인증 (SMS Athentication)
  * 이메일 이용 비밀번호 찾기 (Finding password using E-mail)
  * Firebase SMS auth, PHPmailer
- 프로필 수정 (Update my profile)
- 게시물 관련 (Regarding post)
  * 댓글/대댓글 (Comment and re-comment)
  * 찜하기 (Adding to cart)
  * 판매 상태 변경 (Changing selling status)
[공통기술] (Common skills)
- RecyclerView, Glide, Retrofit, GEOCoder, MultiImage, Shared Preferences

 

[후기] (Review)
프로젝트 기간이 제일 길었다. 약 4달이 조금 안되었고.. 오래걸린 만큼 많은 시도를 했던 것 같다.

하나의 기능에 대해서 여러 서비스가 어떻게 만들었는지 분석하면서 실제로는 어떻게 돌아가는지 궁금한 적이 많았다.

새로운 시도를 많이 해볼 수 있는 기회였다. 어려움도 많았다. 하지만 이걸 해결해나가는 쾌감은 더욱 있었다.

여러 기술들을 공부하니 이전 프로젝트들과는 또 다른 느낌을 많이 받았다.

당근마켓.. 가고싶다!

동영상 - https://epdev.tistory.com/25
