package com.example.nice_module.dto.namecheck;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NameCheckRequestDto {
  @Schema(description = "주민등록번호 또는 외국인 등록번호", example = "900101-1234567")
  private String sJumin;

  @Schema(description = "성함", example = "홍길동")
  private String sName;

  @Schema(description = "요청 번호 (세션 기반 또는 JWT와 매핑)", example = "REQ123456")
  private String sRequestNumber;

  @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR...")
  private String token;
}
