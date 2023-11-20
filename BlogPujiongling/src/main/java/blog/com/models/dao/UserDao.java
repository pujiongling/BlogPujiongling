package blog.com.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import blog.com.models.entity.UserEntity;

@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {

	// 登録したユーザー情報をDBに保存する
	// INSERT INTO users (user_id, user_name, user_email, user_password, creater_at)
	UserEntity save(UserEntity userEntity);

	// メールアドレスを条件としてDBに検索
	// SELECT * FROM users WHERE user_email
	UserEntity findByUserEmail(String userEmail);

	// メールアドレスとパスワードを条件としてDBに検索
	// SELECT * FROM users WHERE user_email = ? AND user_password = ?
	UserEntity findByUserEmailAndUserPassword(String userEmail, String userPassword);

	// ユーザの情報一覧を取得
	// SELECT * FROM users
	List<UserEntity> findAll();

}
