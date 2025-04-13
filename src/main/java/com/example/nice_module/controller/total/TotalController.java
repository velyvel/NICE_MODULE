package com.example.nice_module.controller.total;

import NiceID.Check.CPClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 본인확인 통합형, 클라이언트 연동이 필요하므로
 * 서버 + 클라이언트 구현이 필요하다
 * */
    @Controller
public class TotalController {
  private final String siteCode = "";
  private final String sitePassword = "";
  private final ObjectMapper mapper = new ObjectMapper();

  @GetMapping("/checkPlusMain2")
  public String checkPlusMain(HttpSession session) {
    return "checkplus/checkplusMain";
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
