package com.example.nice_module.controller.namechecksimple;

import NiceID.Check.CPClient;

/**
 * 모듈을 실행하여 sEncData 가 정상적으로 생성되는지 확인하는 클래스 입니다.
 * */
public class NameCheckSimple {

  private static String siteCode = "Z000";
  private static String sitePassword = "10753084";

  public static void main(String[] args) {
    CPClient niceCheck = new CPClient();

    //https://example.com/success 형식 프로토콜부터 작성, 클라이언트라면 클라이언트의 라우터 주소를 작성
    String sReturnURL 		= "http://localhost:8080/simpleNameCheckSuccess";
//    String sReturnURL 		= "리턴 받을 주소";

    // 로고 파일 경로 (방식:절대주소, 기본값:공백)
    String sClientImg		= "";

    // 요청고유번호 생성
    String sRequestNumber = niceCheck.getRequestNO(siteCode);

    // 인증요청 plain 데이터 생성 (형식 수정불가)
    String sPlainData = "7:RTN_URL" + sReturnURL.getBytes().length + ":" + sReturnURL +
        "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
        "7:IMG_URL" + sClientImg.getBytes().length + ":" + sClientImg ;

    // 인증요청 데이터 암호화 : id2 로 하면 안된다.
    int iReturn = niceCheck.fnEncode(siteCode, sitePassword, sPlainData);
    String sMessage = makeMessage(iReturn);
    String sEncData = iReturn == 0 ? niceCheck.getCipherData() : "";
    System.out.println("iReturn : " + iReturn + " sMessage : " + sMessage + "sEncData : " + sEncData);
  }

  private static String makeMessage(int iReturn) {
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
