package com.example.nice_module.test.namecheck;

import niceid.namecheck.RNCheck;

public class NameCheck {

  public static void main(String[] args) {
    final String siteCode = "";
    final String sitePassword = "";

    String jumin = "";
    String name = "";

    RNCheck nameCheck = new RNCheck();
    int iReturn = nameCheck.fnRequest(siteCode, sitePassword, jumin, name);
    String sMessage = makeMessage(iReturn);

    if (!(iReturn == 0)) {
      // 실패
      System.out.println("iReturn = " + iReturn + " sMessage = " + sMessage);
    }
    else {
      // 성공
      String returnCode = nameCheck.getReturnCode();
      String returnMessage = makeReturnMessage(returnCode);
      System.out.println("returnCode = " + returnCode + " returnMessage = " + returnMessage);
    }
  }


  private static String makeMessage(int iReturn) {
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

  private static String makeReturnMessage(String returnCode) {
    return switch (returnCode) {
      case "1" -> "인증성공";
      case "2" ->
          "성명불일치 오류: 주민번호와 성명이 일치하지 않습니다. www.niceid.co.kr 에서 실명정보를 재등록하시거나 NICE 고객센터(1600-1522)로 문의해주십시오.";
      case "3" ->
          "자료없음 오류: 주민번호가 조회되지 않습니다. www.niceid.co.kr 에서 실명정보를 등록하시거나  NICE 고객센터(1600-1522)로 문의해주십시오.";
      case "5" -> "주민번호 체크썸 오류: 주민번호 생성규칙에 맞지 않는 주민번호입니다.";
      case "9" -> "입력정보 오류: 입력정보가 누락되었거나 정상이 아닙니다.(성명, 주민번호 확인 -> 일부 고객 : 차단 (하루 정도 후에 풀림)";
      case "10" -> "사이트 코드 오류: 사이트코드를 대문자로 입력해주십시오. / 혹은 내-외국인 코드 달라서 발셍";
      case "11" -> "정지된 사이트코드 -> 계약 확인";
      case "12" -> "비밀번호 확인";
      case "21" -> "입력정보 형식 오류: 입력정보의 자릿수를 확인해주십시오. (주민번호:13자리, 패스워드: 8자리)";
      case "31", "32", "34", "44" ->
          "통신오류: 당사 서비스 IP를 방화벽에 등록해주십시오.<br>IP:203.234.219.72<br>port:81~85(총 5개)";
      case "50" ->
          "명의도용차단 오류: 명의도용차단 서비스 이용 중인 주민번호입니다. www.credit.co.kr에서 명의도용차단 서비스 해제 후 재시도 하시거나 NICE고객센터(1600-1522)로 문의해주십시오.";
      case "60", "61", "62", "63" ->
          "네트워크 장애: 당사 서비스 IP와의 연결상태를 확인해주십시오.<br>IP:203.234.219.72<br>port:81~85(총 5개)";
      default -> "알 수 없는 결과 코드 -> 사이트코드, 비밀번호, 성명, 주민등록번호(입력값) 확인" + returnCode;
    };
  }
}
