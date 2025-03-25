### 주요 기능
- **회원가입 및 로그인**: 사용자는 이메일, 비밀번호, 프로필 사진을 포함한 회원가입을 할 수 있으며, 로그인 시 JWT를 통해 인증됩니다.
- **게시글 관리**: 게시글을 작성, 수정, 삭제할 수 있으며, 게시글 상세보기와 댓글 기능도 제공합니다.
- **댓글 관리**: 게시글에 댓글을 작성하고 삭제할 수 있습니다.
- **좋아요 기능**: 게시글에 좋아요를 추가하고 제거할 수 있습니다.
- **프로필 수정**: 사용자는 자신의 프로필을 수정할 수 있습니다.
- **비밀번호 수정**: 사용자가 비밀번호를 안전하게 변경할 수 있습니다.

## 기술 스택

### 백엔드
- **Spring Boot**: RESTful API 서버 구축
- **JWT**: JSON Web Token을 사용한 인증 및 보안
- **JPA**: 데이터베이스 연동을 위한 ORM (MySQL 사용)
- **Spring Security**: 사용자 인증 및 권한 관리

API 엔드포인트
1. 사용자 관련 API
회원가입: POST /users

로그인: POST /users/login

회원정보 수정: PUT /users/{user_id}

비밀번호 수정: PUT /users/{user_id}/password

2. 게시글 관련 API
게시글 작성: POST /posts

게시글 수정: PUT /posts/{post_id}

게시글 삭제: DELETE /posts/{post_id}

게시글 상세조회: GET /posts/{post_id}

3. 댓글 관련 API
댓글 추가: POST /posts/{post_id}/comments

댓글 삭제: DELETE /posts/{post_id}/comments/{comment_id}

4. 좋아요 관련 API
좋아요 추가: PATCH /posts/{post_id}/like

3. 인증 및 보안
JWT 토큰
- 로그인 후 서버에서 제공하는 JWT를 클라이언트에서 저장하고, 이후 요청 시 JWT를 포함하여 보냅니다.
- JWT는 Authorization 헤더를 통해 서버에 전달됩니다.
- 서버는 JWT를 검증하여 사용자 인증을 수행합니다.
- 로그아웃 시 클라이언트에 저장되어있는 토큰은 삭제됩니다.
