# ✅ NICE_ID 실명인증 모듈

Spring Boot 환경에서 실명 인증 연동을 위한 예제 코드입니다.

## 📌 1. 실명 인증 프로세스 안내

- `NameCheck.java`: 콘솔 테스트용 Java 클래스
- `NameCheckController.java`: 웹 기반 인증 테스트용 컨트롤러

웹 환경에서는 고유 요청 번호인 `sRequestNumber`를 활용하여 세션 기반 검증 또는 JWT 기반 검증을 수행합니다.

## 📦 2. 필요한 파라미터

| 변수명 | 설명 |
|--------|------|
| `siteCode` | 사이트 코드 (NICE 발급) |
| `sitePassword` | 사이트 비밀번호 (NICE 발급) |
| `jumin` | 주민등록번호 13자리 (String) |
| `name` | 성명 |

## 🧾 3. NameCheckRequestDto

토큰 방식(JWT)을 사용하는 경우, 요청 파라미터는 아래와 같습니다:

```json
{
  "token": "생성한 jwt 토큰",
  "sjumin": "주민등록번호 13자리 String",
  "sname": "성함",
  "srequestNumber": "고유 요청번호"
}
```

`makeRequestNo()` 함수를 활용하거나, 자체 로직으로 `sRequestNumber`를 생성할 수 있습니다.

## 🌐 4. 웹 적용 방식

### ✅ (1) 세션을 사용하는 방법

1. `/nameCheckMain` 실행 → `sRequestNumber` 발급
2. `/nameCheckPost`로 인증 요청 → 세션에 저장된 `sRequestNumber`와 일치 여부 검증

#### 예시

**요청 1**: `/nameCheckMain`
```json
{}
```

**응답**:
```json
{
  "sRequestNumber": "REQ202*04072216314214819745808"
}
```

**요청 2**: `/nameCheckPost`
```json
{
  "sjumin": "9****92*****9",
  "sname": "강*림",
  "srequestNumber": "REQ202*04072216314214819745808"
}
```

**응답**:
```json
{
  "returnCode": "1",
  "returnMessage": "인증성공"
}
```

### ✅ (2) 토큰(JWT)을 사용하는 방법

1. `/nameCheckMain_jwt` 실행 → `sRequestNumber` 및 `token` 발급
2. `/nameCheckPost_jwt`로 인증 요청 → 토큰 내 `sRequestNumber`와 비교

#### 예시

**요청 1**: `/nameCheckMain_jwt`
```json
{}
```

**응답**:
```json
{
  "sRequestNumber": "REQ202*04072221147585037430718",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuYW1lQ2hlY2siLCJzUmVxdWVzdE51bWJlciI6IlJFUTIwMjUwNDA3MjIyMTE0NzU4NTAzNzQzMDcxOCIsImlhdCI6MTc0NDAzMjA3NCwiZXhwIjoxNzQ0MDMyMzc0fQ.WBi7lgtGj14XjcgnjO7irDFhHX12KLZ3xWwf-9o6g3s"
}
```

**요청 2**: `/nameCheckPost_jwt`
```json
{
  "token": "위의 토큰값",
  "sjumin": "주민등록번호 13자리 String",
  "sname": "성함",
  "srequestNumber": "위의 sRequestNumber"
}
```

**응답**:
```json
{
  "returnCode": "1",
  "returnMessage": "인증성공"
}
```

## ✅ 기타 참고

- `sRequestNumber`는 요청의 정합성을 위한 고유 식별자입니다.
- 세션을 사용할 수 없는 환경에서는 JWT로 보안을 대체할 수 있습니다.
- Swagger를 통해 API 테스트 문서를 연동할 수 있습니다.



