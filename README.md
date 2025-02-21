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
이 프로젝트는 JWT 인증 관련 기능을 테스트하는 코드입니다. 아래의 테스트는 Access Token과 Refresh Token의 생성 및 검증, 그리고 쿠키에서 JWT를 추출하는 로직을 포함합니다.
> 1. Access Token 생성 및 검증
> * createAccessToken 메서드를 사용하여 액세스 토큰을 생성하고, 반환된 토큰이 Bearer로 시작하는지 검증합니다.
> 2. Refresh Token 생성 및 검증
> * createRefreshToken 메서드를 사용하여 리프레시 토큰을 생성하고, 해당 토큰이 비어 있지 않은지 검증합니다.
> 3. substringToken 메서드 검증
> * substringToken 메서드를 사용하여 Bearer 접두어를 제거한 후 유효한 토큰을 추출하는지 테스트합니다.
> * 잘못된 형식의 토큰이 들어올 경우 IllegalArgumentException 예외가 발생하는지 확인합니다.
> 4. Claims 추출 및 검증
> * 액세스 토큰을 생성한 후, extractClaims 메서드를 사용하여 JWT에서 클레임을 정확하게 추출하는지 검증합니다.
> 5. resolveToken 메서드 검증
> * HTTP 요청에서 “Bearer” 쿠키를 찾아 그 값을 반환하는지 검증합니다. 
> * 쿠키에 “Bearer” 값이 없으면 null을 반환하는지 확인합니다.
