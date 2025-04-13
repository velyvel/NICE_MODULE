package com.example.nice_module.controller.total;

import NiceID.Check.CPClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 본인확인 통합형, 클라이언트 연동이 필요하므로
 * 서버 + 클라이언트 구현이 필요하다
 * */
@RestController
public class TotalRestController {
  private final String siteCode = "T9999";
  private final String sitePassword = "000000000000";
  private final ObjectMapper mapper = new ObjectMapper();

  @GetMapping("/checkPlusMain")
  public ResponseEntity<JsonNode> checkPlusMain(HttpSession session) {
    try{
      CPClient niceCheck = new CPClient();
      /**
       * 요청 번호
       * 성공, 실패 후에 같은 값을 되돌려주게 되므로, 귀사 로직에 맞게 변경하여 사용하시거나,
       * 아래와 같이 생성합니다.
       * */
      String sRequestNumber = "";        	// 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로
      sRequestNumber = niceCheck.getRequestNO(siteCode);
      session.setAttribute("REQ_SEQ" , sRequestNumber);	// 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.

      String sAuthType = "";      	// 없으면 기본 선택화면, M(휴대폰), X(인증서공통), U(공동인증서), F(금융인증서), S(PASS인증서), C(신용카드)
      String customize 	= "Mobile";		//없으면 기본 웹페이지 / Mobile : 모바일페이지

      /**
       * 인증 결과를 받는 url
       * (팝업창 호출과 동일한 프로토콜, 도메인(포트) 를 사용한다.
       * client 와 분리되어 있는 환경일 경우 client 의 라우터 주소를 작성합니다.
       * */
      String sReturnUrl = "http://localhost:8080/checkplus_success";      // 성공시 이동될 URL

      // 입력될 plain 데이타를 만든다.
      String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
          "8:SITECODE" + siteCode.getBytes().length + ":" + siteCode +
          "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
          "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
          "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize;

      int iReturn = niceCheck.fnEncode(siteCode, sitePassword, sPlainData);
      String sMessage = makeMessage(iReturn);
      String sEncData = iReturn == 0 ? niceCheck.getCipherData() : "";

      ObjectNode result = mapper.createObjectNode();
      result.put("iReturn", iReturn);
      result.put("sMessage", sMessage);
      result.put("sEncData", sEncData);


      return new ResponseEntity<>(result, HttpStatus.OK);

    }catch (Exception e){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }
  }

  private String makeMessage(int iReturn) {
    return switch (iReturn) {
      case 0 -> "성공 ";
      case -1 -> "암호화 / 복호화 시스템 에러입니다";
      case -2 -> "암호화 처리오류입니다.";
      case -3 -> "암호화 데이터 오류입니다.";
      case -4 -> "복호화 처리 오류입니다.";
      case -5 -> "복호화 해쉬 오류입니다.";
      case -6 -> "복호화 데이터 오류입니다.";
      case -9 -> "입력 데이터 오류입니다.";
      case -12 -> "사이트 패스워드 오류입니다.";
      default -> "기타오류 iReturn : " + iReturn + " 값을 NICE 평가정보 전산담당자에게 전달";
    };
  }

}
