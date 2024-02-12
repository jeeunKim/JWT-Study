package jwt.jwttutorial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jwt.jwttutorial.entity.User;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @NotNull
    @Size(min = 3, max = 50)
    private String nickname;

    private Set<AuthorityDto> authorityDtoSet;

    public static UserDto from(User user) {
        if(user == null) return null;

        return UserDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                // User 객체의 권한 정보를 AuthorityDtoSet으로 변환하여 설정합니다.
                .authorityDtoSet(user.getAuthorities().stream()
                        // 권한 객체를 AuthorityDto로 변환하여 매핑합니다.
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        // 변환된 AuthorityDto 객체들을 Set으로 수집합니다.
                        .collect(Collectors.toSet()))
                .build();
    }
}
