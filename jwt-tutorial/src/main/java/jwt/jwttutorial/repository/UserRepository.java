package jwt.jwttutorial.repository;

import jwt.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    //사용자 이름으로 데이터베이스에서 사용자를 조회하고, 연결된 권한 정보(authorities)를 함께 로드합니다.
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);


}
