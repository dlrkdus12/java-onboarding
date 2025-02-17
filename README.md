# Java-onboarding

<br/>

## 🛡️ JWT 기반 인증 및 토큰 만료 처리 (Spring Security)
#### 이 프로젝트에서는 JWT 기반 인증 시스템을 구현하여 Access Token과 Refresh Token을 사용하고, Redis를 이용한 Refresh Token 저장으로 보안성과 성능을 최적화하였습니다.

<br/>

## 📌 전체 흐름
1. 클라이언트가 로그인을 하면, **Access Token**과 **Refresh Token**을 발급받음.
2. 인증이 필요한 요청에서 클라이언트는 **Access Token**을 포함하여 서버에 요청.
3. 서버는 **Access Token**을 검증하여 유효하면 요청을 처리.
4. **Access Token**이 만료되었을 경우:
    - **Refresh Token**을 이용하여 새로운 **Access Token**을 발급
    - **Refresh Token**도 만료되었을 경우, 클라이언트에게 **재로그인** 요청
5. 새로운 **Access Token**이 발급되면 **쿠키**에 저장하여 자동으로 갱신.
6. 클라이언트는 새로운 **Access Token**을 사용하여 다시 요청을 보냄.

<br/>

### 🚀 테스트 코드
> 테스트 코드에서는 JWT 인증이 제대로 동작하는지 검증.
> 1. Access Token 생성 및 검증
> * 만료되지 않은 액세스 토큰을 생성하고 검증하는 테스트.
> * 만료된 토큰이 올바르게 감지되는지 테스트.
> 2. Refresh Token 사용 검증
> * 만료된 액세스 토큰을 전달했을 때, 리프레시 토큰을 이용해 새로운 액세스 토큰을 발급받는지 확인.
> * 리프레시 토큰이 만료된 경우 적절한 에러가 발생하는지 테스트.
> 3. JWT 필터 동작 테스트
> * Spring Security MockMvc를 사용해서 JWT 필터가 정상적으로 요청을 가로채는지 검증.
> 4. Redis 캐시 연동 테스트
> * 리프레시 토큰이 Redis에 저장되고 관리되는지 확인.
