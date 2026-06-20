# controller

HTTP 요청을 받고 Service를 호출한 뒤 Thymeleaf 뷰 이름을 반환합니다. 비즈니스 로직은 이 계층에 두지 않습니다.

현재 주요 Controller:
- `HomeController`: `/`, `/login`
- `DashboardController`: `/dashboard`
- `CommunityController`: `/boards/*`, `/posts`, `/posts/{id}`, `/events`, `/dancers`
- `MyPageController`: `/my-page`, `/me`, 프로필/계정/포트폴리오/참여 이벤트 변경
- `AdminController`: `/admin`
