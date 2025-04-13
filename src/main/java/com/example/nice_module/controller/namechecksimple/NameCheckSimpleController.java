package com.example.nice_module.controller.namechecksimple;

import NiceID.Check.CPClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller

/**
 * ./NameCheckSimple.java 파일 실행
 * sRequestNumber(검증, 혹은 사용자 정의 파라미터) -> 사용 방법 정의(redis, session, token 등)
 * */

public class NameCheckSimpleController {

  private final String siteCode = "Z000";
  private final String sitePassword = "10753084";
  private final ObjectMapper mapper = new ObjectMapper();

  @GetMapping("/nameCheckSimpleMain2")
  public String nameCheckSimpleMain2(HttpSession session, Model model) {
    try {

      CPClient niceCheck = new CPClient();

      //https://example.com/success 형식 프로토콜부터 작성
      String sReturnURL = "http://localhost:8080/nameCheckSimpleSuccess";

      // 로고 파일 경로 (방식:절대주소, 기본값:공백)
      String sClientImg = "";

      // 요청고유번호 생성
      String sRequestNumber = niceCheck.getRequestNO(siteCode);

      // 세션에 저장
      session.setAttribute("REQ_SEQ", sRequestNumber);

      // 인증요청 plain 데이터 생성 (형식 수정불가)
      String sPlainData = "7:RTN_URL" + sReturnURL.getBytes().length + ":" + sReturnURL +
          "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
          "7:IMG_URL" + sClientImg.getBytes().length + ":" + sClientImg;

      // 인증요청 데이터 암호화 : id2 로 하면 안된다.
      int iReturn = niceCheck.fnEncode(siteCode, sitePassword, sPlainData);
      String sMessage = makeMessage(iReturn);
      String sEncData = iReturn == 0 ? niceCheck.getCipherData() : "";

      model.addAttribute("message", sMessage);
      model.addAttribute("sEncData", sEncData);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "simplenamecheck/simplenamecheck";
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

  /*
   * post 더라도 form 으로 가기 때문에,
   * */
  @RequestMapping(value = "/nameCheckSimpleSuccess", method = {RequestMethod.GET,
      RequestMethod.POST})
  public String sNameCheckWebSuccess(
      @RequestParam(value = "enc_data", required = false) String sEncodeData, HttpSession session,
      Model model) {

    CPClient niceCheck = new CPClient();

    sEncodeData = "AgAEWjAwMM5DunnLcsIsWkw5aL95xkrdKxfybvAejUJOBKOVe9GHd5EDjRKaHVcV3z9S0FNeKHJIpKqYm9l37Lybz0zdynmcyhEu+itTwFf7vQQtKYa/BJin/vWSUOclUk6jJnok4O/BtTTDoyPMKoiiZDeXgpHh6N5uzQV5kmo81N0LAITUk80mAYxsJZgkRtA0JiC5eArJmguKfKDvBvlz+KrvRs9tcRU2NdFvxGgmpfDaJLTiKfq3N+1Nf71NdeqToL1LSwFI/gtFBBlukH+UEpnwK3m35H1dhnx9Ugz6e2cQQ/zJY12nbdOiXZxG9Vt+WTXFO3cyoRlyaoBTHETahYl5AgYZFLYTvIG8DyBzwbQkC4wSKY/uZI6ORzOqdeRZa+crcw==";

    // EncodeDataParam이 없으면 EncodeDataBody 사용
//    String encodeData = requestReplace(sEncodeData, "enc_data");
    String encodeData = URLDecoder.decode(sEncodeData);
    int iReturn = niceCheck.fnDecode(siteCode, sitePassword, encodeData);
    String sMessage = makeMessage(iReturn);

    if (iReturn != 0) {
      model.addAttribute("sMessage", sMessage);
    }

    // 성공 시 데이터 처리
    String sPlainData = niceCheck.getPlainData();
    Map<String, String> mapResult = niceCheck.fnParse(sPlainData);

    // 세션 요청번호 검증
//    String session_sRequestNumber = (String) session.getAttribute("REQ_SEQ");
//    String sRequestNumber = mapResult.get("REQ_SEQ");
//
//    if (!Objects.equals(session_sRequestNumber, sRequestNumber)) {
//      model.addAttribute("sMessage", "세션값이 다릅니다.");
//      mapResult.clear();
//    }
    model.addAttribute("iReturn", iReturn);
    model.addAttribute("sMessage", sMessage);
    model.addAttribute("mapResult", mapResult);

    return "simplenamecheck/sNameCheckWebSuccess";
  }


  public String requestReplace(String paramValue, String gubun) {
    if (paramValue == null) {
      return "";
    }

    // 기본 필터링: XSS 방지 (<, >) 및 허용되지 않는 문자 제거
    paramValue = paramValue
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replaceAll("[*?\\[\\]{}()^$'@%;:#,]", "")
        .replaceAll("--", ""); // 연속된 '--' 제거

    // Base64에 사용될 수 없는 문자 제거 (gubun이 "encodeData"가 아닐 경우)
    if (!"encodeData".equals(gubun)) {
      paramValue = paramValue.replaceAll("[+/=]", "");
    }

    return paramValue;
  }



  }
