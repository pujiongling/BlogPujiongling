package blog.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.com.models.entity.UserEntity;
import blog.com.services.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserLoginController {

	// UserServiceクラスを読み込んでメソッドを使えるために
	// UserLoginControllerのメンバ変数userServiceとして宣言する
	@Autowired
	private UserService userService;

	// Sessionが使えるようにする宣言
	@Autowired
	private HttpSession session;

	// ログイン画面を表示
	@GetMapping("/login")
	public String getLoginPage() {
		return "user_login.html";
	}

	// ログイン処理
	// 画面からユーザーが入力した情報を変数として受け取って
	@PostMapping("/login/process")
	public String userLogin(@RequestParam String userEmail, @RequestParam String userPassword, String userName,
			Model model) {
		// 入力の内容がServiceのメソッドを使ってDBを検索して、結果を変数userに格納する
		UserEntity user = userService.checkLogin(userEmail, userPassword);
		// もしuserが存在していない場合
		if (user == null) {
			// 画面にヒント渡す
			model.addAttribute("userMistake", "IDまたはパスワードが正しくありません。");
			// ログイン画面に戻ります
			return "user_login.html";
		} else {
			// もしuserが存在している場合
			// sessionを使ってログインしている人の情報を保存する
			session.setAttribute("user", user);
			// ログインしている人の情報共にブログ一覧画面に転送する
			return "redirect:/blog/list";
		}
	}

	// ログアウト処理
	@GetMapping("/logout")
	public String logout() {
		// セッションの無効化
		session.invalidate();
		return "redirect:/login";
	}

}
