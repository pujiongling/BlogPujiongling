package blog.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.com.services.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	// ログイン画面を表示
	@GetMapping("/login")
	public String getLoginPage() {
		return "user_login.html";
	}
	// ログイン処理
	// もし入力の内容がDBを検索した結果存在している場合
	// success.htmlを表示します htmlに入力されたメールアドレスを表示してください
	// そうでない場合
	// login.htmlを表示してください
	/*@PostMapping("/login/process")
	public String userLogin(@RequestParam String userEmail,@RequestParam String userPassword,Model model) {
		if(userService.createUser(userName, userEmail, userPassword)) {
			
		}
	}
	*/
	
}
