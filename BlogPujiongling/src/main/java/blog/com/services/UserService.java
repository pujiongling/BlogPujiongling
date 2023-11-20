package blog.com.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.com.models.dao.UserDao;
import blog.com.models.entity.UserEntity;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	// ユーザー情報を保存する処理のメソッド
	// 存在していない場合は登録する true
	// 既にテーブルに入力されたメールが存在していたら登録しない false
	public boolean createUser(String userName, String userEmail, String userPassword) {
		if (userDao.findByUserEmail(userEmail) == null) {
			LocalDateTime createdAt = LocalDateTime.now();
			userDao.save(new UserEntity(userName, userEmail, userPassword, createdAt));
			return true;
		} else {
			return false;
		}
	}

	public UserEntity checkLogin(String userEmail, String userPassword) {
		UserEntity userEntity = userDao.findByUserEmailAndUserPassword(userEmail, userPassword);
		if (userEntity == null) {
			return null;
		} else {
			return userEntity;
		}
	}
}
