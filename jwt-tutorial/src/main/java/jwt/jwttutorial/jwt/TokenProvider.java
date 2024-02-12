package jwt.jwttutorial.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jwt.jwttutorial.dto.AccessTokenDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@NoArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    /**
     * 보안을 위해 HS256 알고리즘에 적합한 보안 키로 변경
     */
    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Jwt객체 생성(accessToken, refreshToken)
     */
    public Jwt createJwt(Map<String, Object> claims){
        String accessToken = createToken(claims, getExpireDateAccessToken());
        String refreshToken = createToken(new HashMap<>(), getExpireDateRefreshToken());
        return Jwt.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * RefreshToken으로 AccessToken 새로 발급
     */
    public AccessTokenDto createAccessToken(Map<String, Object> claims){
        String accessToken = createToken(claims, getExpireDateAccessToken());
        return new AccessTokenDto(accessToken);
    }

    /**
     * JWT 토큰에서 Claim 정보 추출하는 함수
     */
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT 토큰 생성 함수
     */
    public String createToken(Map<String, Object> claims, Date expireDate){
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    /**
     * AccessToken 만료 기간 설정(30분)
     */
    private Date getExpireDateAccessToken() {
        long expireTime = 1000 * 60;
        return new Date(System.currentTimeMillis() + expireTime);
    }

    /**
     * RefreshToken 만료 기간 설정(3일)
     */
    private Date getExpireDateRefreshToken() {
        long expireTime = 1000 * 60 * 60 * 24 * 7;
        return new Date(System.currentTimeMillis() + expireTime);
    }

}