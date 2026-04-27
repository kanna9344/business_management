package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "Select user from User user where lower(user.username)=lower(:username)")
	public User findUser(@Param(value = "username") String username);

	@Query(value = "Select user.id from User user where lower(user.username)=lower(:username)")
	public Integer findUserId(@Param(value = "username") String username);

}
