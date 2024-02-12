package jwt.jwttutorial.jwt;

import lombok.Builder;
import lombok.Getter;


/*
TokenProvider랑 같이 묶이는 파일
 */
@Getter
public class Jwt {

    private String accessToken;
    private String refreshToken;

    @Builder
    public Jwt(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
