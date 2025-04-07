package com.example.nice_module.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {
  private static final String SECRET_KEY = "qwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiop"; // 하드코딩 후 설정파일 등에 저장
//  private static final String SECRET_KEY = "256bytes 키값"; // 하드코딩 후 설정파일 등에 저장
  private static final long EXPIRATION_TIME = 5 * 60 * 1000; // 5분

  public static String createToken(String sRequestNumber) {
    return Jwts.builder()
               .setSubject("nameCheck")
               .claim("sRequestNumber", sRequestNumber)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
               .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
               .compact();
  }

  public static String extractRequestNumber(String token) {
    Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();
    return claims.get("sRequestNumber", String.class);
  }
}

