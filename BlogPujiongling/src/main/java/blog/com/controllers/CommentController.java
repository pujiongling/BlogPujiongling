package blog.com.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.com.services.BlogService;
import blog.com.services.CommentService;

@Controller
public class CommentController {

	/*-----------------------------------11.23更新-----------------------------------*/

	@Autowired
	private CommentService commentService;

	@Autowired
	private BlogService blogService;

	// コメントを保存する
	@PostMapping("/blog/comment")
	public String commentRegister(@RequestParam String commentContent, String reviewer, LocalDateTime commentAt,
			Long userId, Long blogId, Model model) {
		// ログイン状態を判定し、画面にログイン情報を渡す
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		if (blogService.checkUserLoggedIn()) {
			// もしログイン中の場合
			// コメントを保存
			commentService.saveComment(commentContent, reviewer, commentAt, userId, blogId);
			return "redirect:/blog/detail/" + blogId;
		} else {
			// 未ログインの場合の処理
			model.addAttribute("blogProcess", "ゲストはコメントできません。");
			model.addAttribute("login", "ログインまたは");
			model.addAttribute("register", "新規登録してください");
			return "blog_process.html";
		}
	}

	// コメントを削除するメソッド
	@PostMapping("/comment/delete/process")
	public String deleteCommentProcess(@RequestParam Long commentId, Long userId, Long blogId, Model model) {
		// ログイン状態を判定し、画面にログイン情報を渡す
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		if (blogService.checkUserLoggedIn()) {
			if (commentService.deleteComment(commentId, userId)) {
				// もしログイン中の場合
				// コメント削除処理
				model.addAttribute("blogProcess", "コメントを削除しました");
				return "blog_process.html";
			} else {
				model.addAttribute("blogProcess", "削除失敗しました。後でもう一度お試しください。");
				return "blog_process.html";
			}
		} else {
			// 未ログインの場合の処理
			model.addAttribute("blogProcess", "ゲストはブログを編集できません。");
			model.addAttribute("login", "ログインまたは");
			model.addAttribute("register", "新規登録してください");
			return "blog_process.html";
		}

	}
}
