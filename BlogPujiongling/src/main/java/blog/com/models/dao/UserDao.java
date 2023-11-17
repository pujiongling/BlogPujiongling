package blog.com.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import blog.com.models.entity.UserEntity;

@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {
	// 保存処理 save
	UserEntity save(UserEntity userEntity);

	// 1行だけしかレコードは取得することができない
	// SELECT * FROM users WHERE user_email
	UserEntity findByUserEmail(String adminEmail);

	// 1行だけしかレコードは取得することができない
	// SELECT * FROM users WHERE user_email = ? AND user_password = ?
	UserEntity findByUserEmailAndUserPassword(String userEmail, String userPassword);

}
