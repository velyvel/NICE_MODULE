# NICE_ID 실명인증

## 1) 프로세스 안내

`NameCheck.java` 파일을 실행합니다.

## 2) 필요한 파라미터

| 변수명       | 설명                               |
| ----------- | ---------------------------------- |
| `siteCode`    | 사이트 코드 : NICE 에서 발급함         |
| `sitePassword`| 사이트 비밀번호 : NICE 에서 발급함   |
| `jumin`       | 주민등록번호 13자리 String           |
| `name`        | 성명                               |

## 3) 웹에 적용 : NameCheckController

### (1) 세션을 사용하는 방법

```java
request {
    // ... 세션 관련 코드 ...
}
