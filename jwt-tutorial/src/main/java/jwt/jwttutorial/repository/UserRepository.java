package jwt.jwttutorial.repository;

import jwt.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
