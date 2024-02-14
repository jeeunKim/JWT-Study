package jwt.jwttutorial.controller;

import jakarta.validation.Valid;
import jwt.jwttutorial.dto.LoginDto;
import jwt.jwttutorial.dto.RefreshTokenDto;
import jwt.jwttutorial.dto.TokenDto;
import jwt.jwttutorial.exception.DuplicateMemberException;
import jwt.jwttutorial.jwt.JwtFilter;
import jwt.jwttutorial.jwt.TokenProvider;
import jwt.jwttutorial.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthService authService;

    /*
     로그인 - 토큰 발급
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {
        authService.loginCheck(loginDto);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = tokenProvider.createToken(authentication);

        authService.updateRefreshToken(loginDto.getUsername(), tokenDto.getRefreshToken());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer" + tokenDto.getAccessToken());
        httpHeaders.add("Refresh-Token",tokenDto.getRefreshToken());

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }

    @PatchMapping("/refresh")
    public ResponseEntity<TokenDto> tokenRefresh(@RequestBody RefreshTokenDto refreshTokenDto){
        TokenDto tokenDto = authService.refreshToken(refreshTokenDto.getRefreshToken());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer" + tokenDto.getAccessToken());
        httpHeaders.add("Refresh-Token",tokenDto.getRefreshToken());

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        authService.logout(refreshTokenDto.getRefreshToken());

        return ResponseEntity.ok("Logout Success");
    }
}
