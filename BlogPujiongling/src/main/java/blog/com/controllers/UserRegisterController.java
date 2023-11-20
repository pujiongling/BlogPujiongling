package blog.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.com.services.UserService;

@Controller
public class UserRegisterController {

	// UserServiceクラスを読み込んでメソッドを使えるために
	// RegisterControllerのメンバ変数として宣言する
	@Autowired
	private UserService userService;

	// 登録画面を表示
	@GetMapping("/register")
	public String getUserRegisterPage() {
		return "user_register.html";
	}

	// 登録処理
	// 画面からユーザーが入力した情報を変数として受け取って
	@PostMapping("/register/process")
	public String userRegister(@RequestParam String userName, @RequestParam String userEmail,
			@RequestParam String userPassword, Model model) {
		// 入力の内容がServiceのメソッドを使ってDBを検索して、falseかtrueを戻して
		if (userService.createUser(userName, userEmail, userPassword)) {
			//もし登録している人が存在していない場合
			//welcome.htmlを表示する、画面に入力されたユーザー名を表示する
			model.addAttribute("userName", userName);
			return "user_welcome.html";
		} else {
			//もし登録している人が存在している場合
			//登録画面に戻ります、画面にエラーメッセージを表示する
			model.addAttribute("userExisted", "入力されたメールアドレスはすでに登録されている");
			return "user_register.html";
		}
	}

}
