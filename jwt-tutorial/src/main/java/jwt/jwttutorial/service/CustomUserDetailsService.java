package jwt.jwttutorial.service;

import jwt.jwttutorial.entity.User;
import jwt.jwttutorial.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    @Transactional
    /*
        데이터 베이스에서 유저를 가져옴
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + "-> DB에서 찾을 수 없습니다."));
    }

    /*
    가져온 유저 데이터가 활성화가 상태라면 -> 유저 정보와 패스워드, 권한정보로 userdatails.User객체를 생성해서 리턴
    */
    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if(!user.isActivated()) {
            throw new RuntimeException((username + " -> 활성화되어 있지 않습니다."));
        }

        // grantedAuthorities 리스트에는 사용자가 가진 각각의 권한을 표현하는 GrantedAuthority 객체가 포함되어 있습니다.
        // 이 리스트는 UserDetails 객체를 생성할 때 사용되며, 사용자의 권한 정보를 포함하는 중요한 부분
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}
