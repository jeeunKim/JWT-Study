package jwt.jwttutorial.service;

import jwt.jwttutorial.dto.LoginDto;
import jwt.jwttutorial.dto.TokenDto;
import jwt.jwttutorial.entity.User;
import jwt.jwttutorial.jwt.TokenProvider;
import jwt.jwttutorial.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jwt.jwttutorial.util.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

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

    public void loginCheck(LoginDto loginDto){
        Optional<User> optionalUser = userRepository.findByUsername(loginDto.getUsername());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    public void logout(String refreshToken){
        /*
            Access Token 도 삭제해야함
            밑에는 Refresh Token 삭제
         */
        Optional<User> optionalUser = userRepository.findByRefreshToken(refreshToken);
        optionalUser.ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });

    }
}
