package com.example.nice_module.controller.namecheck;

import com.example.nice_module.dto.namecheck.NameCheckRequestDto;
import com.example.nice_module.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import niceid.namecheck.RNCheck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * [테스트 순서]
 * /test/namecheck/NameCheck.java 파일 실행 -> 결과 확인 후 웹 적용
 * sRequestNumber(검증, 혹은 사용자 정의 파라미터) -> 사용 방법 정의(redis, session, token 등)
 * */
@RestController
public class NameCheckController {

  private String sSiteCode = "Z000"; // 사이트코드
  private String sSitePw = "10753084"; // 사이트 비밀번호
  private final ObjectMapper mapper = new ObjectMapper();

  @GetMapping("/nameCheckMain")
  public ResponseEntity<JsonNode> nameCheckMain(HttpSession session) {

    /**
     * 귀사 비지니스 로직에 따라 지정하는 값
     * session 사용하지 않는 경우 redis, token 등 사용
     * */
    try{
      String sRequestNumber = makeRequestNo();
      session.setAttribute("nameCheckSession", sRequestNumber);

      // 클라이언트에게 내려주기
      ObjectNode result = mapper.createObjectNode();
      result.put("sRequestNumber", sRequestNumber);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }catch (Exception e){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * 실명 인증 요청을 처리합니다.
   *
   * @param requestBody JSON 형식의 요청 바디로, 아래의 필드를 포함합니다:
   *   - sJumin (String): 주민등록번호 또는 외국인 등록번호
   *   - sName (String): 성함
   *   - sRequestNumber (String): 사전 발급된 인증 요청 번호 (JWT, session 등에 포함된 값과 비교)
   *   - token (String): 서버에서 발급한 JWT 토큰
   * @return 실명 인증 결과로, returnCode 및 returnMessage를 포함한 JSON 응답을 반환합니다.
   */

  @PostMapping("/nameCheckPost")
  public ResponseEntity<JsonNode> nameCheckPost(@RequestBody JsonNode requestBody, HttpSession session) {
    try{
      String sJumin = requestBody.has("sJumin") ? requestBody.get("sJumin").asText() : "";
      String sName = requestBody.has("sName") ? requestBody.get("sName").asText() : "";
      String sRequestNumber = requestBody.has("sRequestNumber") ? requestBody.get("sRequestNumber").asText() : "";
      String sessionRequestNumber = session.getAttribute("nameCheckSession").toString();
      RNCheck ncClient = new RNCheck();

      // 세션 비교
      if(!(sRequestNumber.equals(sessionRequestNumber))) {
        throw new RuntimeException("세션 값이 다릅니다.");
      }

      // 데이터 처리
      int iReturn = ncClient.fnRequest(sSiteCode, sSitePw, sJumin, sName);
      String sMessage = makeMessage(iReturn);

      if(!(iReturn == 0)) {
        throw new RuntimeException("실명인증 실패 : " + sMessage);
      }

      String returnCode = ncClient.getReturnCode();
      String returnMessage = makeReturnMessage(returnCode);

      // result 에 저정하기
      ObjectNode result = mapper.createObjectNode();
      result.put("returnCode", returnCode);
      result.put("returnMessage", returnMessage);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }catch (Exception e){
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 변경 가능한 매서드
   * 용도
   *  1) 같은 요청인지 검증하기 위한 값
   *  2) 인증 전 필요한 값들을 저장하기 위해 사용(사용자 정의 파라미터)
   * */
  private String makeRequestNo() {

    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    String requestTime = currentDateTime.format(formatter);

    //requestNumber 예제
    StringBuilder sb = new StringBuilder(13);
    for (int i = 0; i < 13; i++) {
      int digit = (int) (Math.random() * 10); // 0에서 9 사이의 숫자 생성
      sb.append(digit);
    }
    String randomNum = sb.toString();
    String requestNumber = "REQ" + requestTime + randomNum;
    return requestNumber;
  }

  private String makeMessage(int iReturn) {
    return switch (iReturn) {
      case 0 -> "정상처리, 실명인증 함수 태우기";
      case -1 -> "시스템 오류";
      case -2, -3 -> "암호화 처리 오류";
      case -4, -5, -6 -> "복호화 처리 오류";
      case -7, -8 -> "암복호화 버전 오류";
      case -18 -> "통신오류: 당사 서비스 IP를 방화벽에 등록해주십시오.<br>IP:203.234.219.72<br>port:81~85(총 5개 모두 등록)";
      default -> "알 수 없는 결과 코드 -> 사이트코드, 비밀번호, 성명, 주민등록번호(입력값) 확인" + iReturn;
    };
  }

  private String makeReturnMessage(String returnCode) {
    return switch (returnCode) {
      case "1" -> "인증성공";
      case "2" -> "성명불일치 오류: 주민번호와 성명이 일치하지 않습니다. www.niceid.co.kr 에서 실명정보를 재등록하시거나 NICE 고객센터(1600-1522)로 문의해주십시오.";
      case "3" -> "자료없음 오류: 주민번호가 조회되지 않습니다. www.niceid.co.kr 에서 실명정보를 등록하시거나  NICE 고객센터(1600-1522)로 문의해주십시오.";
      case "5" -> "주민번호 체크썸 오류: 주민번호 생성규칙에 맞지 않는 주민번호입니다.";
      case "9" -> "입력정보 오류: 입력정보가 누락되었거나 정상이 아닙니다.(성명, 주민번호 확인 -> 일부 고객 : 차단 (하루 정도 후에 풀림)";
      case "10" -> "사이트 코드 오류: 사이트코드를 대문자로 입력해주십시오. / 혹은 내-외국인 코드 달라서 발셍";
      case "11" -> "정지된 사이트코드 -> 계약 확인";
      case "12" -> "비밀번호 확인";
      case "21" -> "입력정보 형식 오류: 입력정보의 자릿수를 확인해주십시오. (주민번호:13자리, 패스워드: 8자리)";
      case "31", "32", "34", "44" -> "통신오류: 당사 서비스 IP를 방화벽에 등록해주십시오. IP:203.234.219.72, port:81~85(총 5개)";
      case "50" -> "명의도용차단 오류: 명의도용차단 서비스 이용 중인 주민번호입니다. www.credit.co.kr에서 명의도용차단 서비스 해제 후 재시도 하시거나 NICE고객센터(1600-1522)로 문의해주십시오.";
      case "60", "61", "62", "63" -> "네트워크 장애: 당사 서비스 IP와의 연결상태를 확인해주십시오.<br>IP:203.234.219.72<br>port:81~85(총 5개)";
      default -> "알 수 없는 결과 코드 -> 사이트코드, 비밀번호, 성명, 주민등록번호(입력값) 확인" + returnCode;
    };
  }

  /**
   * jwt 사용 예제
   * /util/JwtUtil.java 클래스 import 필요
   * */
  @PostMapping("/nameCheckMain_jwt")
  public ResponseEntity<JsonNode> nameCheckMainJWT() {
    String sRequestNumber = makeRequestNo();
    String token = JwtUtil.createToken(sRequestNumber);

    ObjectNode result = mapper.createObjectNode();
    result.put("sRequestNumber", sRequestNumber);
    result.put("token", token);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/nameCheckPost_jwt")
  public ResponseEntity<JsonNode> nameCheckPostJWT(@RequestBody NameCheckRequestDto dto) {
    try {
      String sJumin = dto.getSJumin();
      String sName = dto.getSName();
      String sRequestNumber = dto.getSRequestNumber();
      String token = dto.getToken();

      // JWT에서 sRequestNumber 추출
      String tokenRequestNumber = JwtUtil.extractRequestNumber(token);

      if (!tokenRequestNumber.equals(sRequestNumber)) {
        throw new RuntimeException("토큰 값이 일치하지 않습니다.");
      }

      RNCheck ncClient = new RNCheck();
      int iReturn = ncClient.fnRequest(sSiteCode, sSitePw, sJumin, sName);

      if (iReturn != 0) {
        throw new RuntimeException("실명인증 실패: " + makeMessage(iReturn));
      }

      ObjectNode result = mapper.createObjectNode();
      result.put("returnCode", ncClient.getReturnCode());
      result.put("returnMessage", makeReturnMessage(ncClient.getReturnCode()));

      return ResponseEntity.ok(result);
    } catch (Exception e) {
      ObjectNode error = mapper.createObjectNode();
      error.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }
}
