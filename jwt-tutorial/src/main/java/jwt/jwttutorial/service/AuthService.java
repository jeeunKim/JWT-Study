package jwt.jwttutorial.service;

import jwt.jwttutorial.dto.TokenDto;
import jwt.jwttutorial.entity.User;
import jwt.jwttutorial.jwt.TokenProvider;
import jwt.jwttutorial.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public void updateRefreshToken(String username, String refreshToken){
        Optional<User> userOptional = userRepository.findByUsername(username);

        userOptional.ifPresent(user -> {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        });

    }

    public TokenDto refreshToken(String refreshToken){
        tokenProvider.validateToken(refreshToken);
        Optional<User> userOptional = userRepository.findByRefreshToken(refreshToken);
        AtomicReference<TokenDto> tokenDto = new AtomicReference<>(new TokenDto());

        userOptional.ifPresent(user -> {
            if(!(user.getRefreshToken().equals(refreshToken))){
                log.info("유효하지 않은 토큰입니다.");

            } else {
                Authentication authentication = tokenProvider.getAuthentication(refreshToken);
                tokenDto.set(tokenProvider.createToken(authentication));
                user.setRefreshToken(tokenDto.get().getRefreshToken());
                userRepository.save(user);
            }
        });

        return tokenDto.get();
    }
}
