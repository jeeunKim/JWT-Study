package jwt.jwttutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessTokenDto {
    private String accessToken;
}
