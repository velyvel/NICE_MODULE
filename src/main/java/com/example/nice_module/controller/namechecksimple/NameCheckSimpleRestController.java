package com.example.nice_module.controller.namechecksimple;

import NiceID.Check.CPClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

/**
 * ./NameCheckSimple.java 파일 실행
 * sRequestNumber(검증, 혹은 사용자 정의 파라미터) -> 사용 방법 정의(redis, session, token 등)
 * */

public class NameCheckSimpleRestController {

  private final String siteCode = "Z000";
  private final String sitePassword = "10753084";
  private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/nameCheckSimpleMain")
    public ResponseEntity<JsonNode> nameCheckSimpleMain(HttpSession session) {
      try{

        CPClient niceCheck = new CPClient();

        //https://example.com/success 형식 프로토콜부터 작성
        String sReturnURL 		= "리턴 받을 주소";

        // 로고 파일 경로 (방식:절대주소, 기본값:공백)
        String sClientImg		= "";

        // 요청고유번호 생성
        String sRequestNumber = niceCheck.getRequestNO(siteCode);

        // 세션에 저장
        session.setAttribute("REQ_SEQ" , sRequestNumber);

        // 인증요청 plain 데이터 생성 (형식 수정불가)
        String sPlainData = "7:RTN_URL" + sReturnURL.getBytes().length + ":" + sReturnURL +
            "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
            "7:IMG_URL" + sClientImg.getBytes().length + ":" + sClientImg ;

        // 인증요청 데이터 암호화 : id2 로 하면 안된다.
        int iReturn = niceCheck.fnEncode(siteCode, sitePassword, sPlainData);
        String sMessage = makeMessage(iReturn);
        String sEncData = iReturn == 0 ? niceCheck.getCipherData() : "";

        ObjectNode result = mapper.createObjectNode();
        result.put("sRequestNumber", sRequestNumber);
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

  @PostMapping("/simpleNameCheckSuccess")
  public ResponseEntity<JsonNode> simpleNameCheckSuccess() {
    try{
      ObjectNode result = mapper.createObjectNode();
      return new ResponseEntity<>(result, HttpStatus.OK);

    }catch (Exception e){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }



  }
