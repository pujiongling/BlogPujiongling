package blog.com.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import blog.com.models.entity.BlogEntity;
import blog.com.models.entity.UserEntity;
import blog.com.services.BlogService;
import jakarta.servlet.http.HttpSession;

@Controller
public class BlogController {

	// BlogServiceクラスを読み込んでメソッドを使えるために
	// BlogControllerのメンバ変数blogServiceとして宣言する
	@Autowired
	private BlogService blogService;

	// ユーザーIDに基づいてブログ一覧を取得し、表示する。
	@GetMapping("/blog/list")
	public String getBlogList(Long userId, Model model) {
		// ユーザーがログインしているかを確認
		if (blogService.checkUserLoggedIn()) {
			// ログイン中の場合の処理
			// userIdに基づいて、個人ブログの一覧取得、ユーザー名を画面に渡す
			model.addAttribute("userName", blogService.getCurrentUser().getUserName());
		} else {
			// 未ログインの場合の処理
			// ゲストとして、ブログを一覧できる
			model.addAttribute("userName", "ゲスト");
		}
		// ユーザーのログイン状態に応じて、navigatorのフィールドを設定
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		blogService.getBlogList(userId, model);
		return "blog_list.html";
	}

	// ブログの登録画面を表示する
	@GetMapping("/blog/register")
	public String getBlogRegisterPage(Long userId, Model model) {
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		// ユーザーがログインしているかを確認
		if (blogService.checkUserLoggedIn()) {
			// ログイン中の場合の処理
			// ユーザー名を画面に渡す、ブログの登録画面を表示
			model.addAttribute("userName", blogService.getCurrentUser().getUserName());
			return "blog_register.html";
		} else {
			// 未ログインの場合の処理
			// 登録できないのメーセージを画面に示
			model.addAttribute("blogProcess", "ゲストはブログを登録できません。");
			model.addAttribute("login", "ログインまたは");
			model.addAttribute("register", "新規登録してください");
			return "blog_process.html";
		}
	}

	// ブログの登録内容を保存する
	@PostMapping("/blog/register/process")
	public String blogRegister(@RequestParam String blogTitle, @RequestParam String categoryName,
			@RequestParam MultipartFile blogImage, @RequestParam String blogArticle, Long userId, Model model) {
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		// ユーザーがログインしているかを確認
		if (blogService.checkUserLoggedIn()) {
			// ログイン中の場合の処理
			// 現在の日時情報を "yyyy-MM-dd-HH-mm-ss-" フォーマットで取得し、ファイル名に利用
			model.addAttribute("userName", blogService.getCurrentUser().getUserName());
			String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
					+ blogImage.getOriginalFilename();
			// ブログの保存処理を呼び出し、結果を確認
			if (blogService.saveBlog(blogTitle, categoryName, fileName, blogArticle, userId, model) != null) {
				// ブログの内容を保存成功した場合、受け取ったファイルを保存
				blogService.saveBlogImage(blogImage, fileName);
				model.addAttribute("blogProcess", "新しい記事を追加しました");
				return "blog_process.html";
			} else {
				// ブログの編集内容を保存失敗した場合
				model.addAttribute("blogProcess", "保存失敗しました。後でもう一度お試しください。");
				return "blog_process.html";
			}
		} else {
			// 未ログインの場合の処理
			model.addAttribute("blogProcess", "アカウントがログアウトされました。再度ログインしてください。");
			return "blog_process.html";
		}
	}

	/*-----------------------------------11.22更新-----------------------------------*/
	// ブログディテールを表示する
	@GetMapping("/blog/detail/{blogId}")
	public String getBlogDetailPage(@PathVariable Long blogId, Model model) {
		// ユーザーのログイン状態に応じて、navigatorのフィールドを設定
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		// ブログの詳細を取得する
		BlogEntity blogList = blogService.getBlogPost(blogId);
		model.addAttribute("blogList", blogList);
		return "blog_detail.html";
	}

	// ブログ編集画面を表示する
	@GetMapping("/blog/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId, Model model) {
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		// ユーザーがログインしているかを確認
		if (blogService.checkUserLoggedIn()) {
			// ログイン中の場合の処理
			// ブログの詳細を取得
			model.addAttribute("userName", blogService.getCurrentUser().getUserName());
			BlogEntity blogList = blogService.getBlogPost(blogId);
			model.addAttribute("blogList", blogList);
			return "blog_edit.html";
		} else {
			// 未ログインの場合の処理
			model.addAttribute("blogProcess", "ゲストはブログを編集できません。");
			model.addAttribute("login", "ログインまたは");
			model.addAttribute("register", "新規登録してください");
			return "blog_process.html";
		}
	}

	// ブログの編集内容を保存する
	@PostMapping("/blog/edit/process")
	public String blogEditProcess(@RequestParam Long blogId, @RequestParam String blogTitle,
			@RequestParam String categoryName, @RequestParam MultipartFile blogImage, @RequestParam String blogArticle,
			Model model) {
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		// ユーザーがログインしているかを確認
		if (blogService.checkUserLoggedIn()) {
			// ログイン中の場合の処理
			// ファイル名前を指定

			// String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new
			// Date())
			// + blogImage.getOriginalFilename();

			// 追加コード
			//
			String fileName;
			if (blogImage.getOriginalFilename().isEmpty()) {
				// ファイルが選択されていない場合、元のファイル名を使用
				fileName = blogService.getBlogPost(blogId).getBlogImage();
				model.addAttribute("blogProcess", "記事が変更されませんでした");
			} else {
				// 新しいファイルを選択した場合、ファイル名を生成
				fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
						+ blogImage.getOriginalFilename();
				// ブログの編集処理を呼び出し、結果を確認
				if (blogService.editBlog(blogId, blogTitle, categoryName, fileName, blogArticle, blogId)) {
					// ブログの編集内容を保存成功した場合、受け取ったファイルを保存
					blogService.saveBlogImage(blogImage, fileName);
					model.addAttribute("blogProcess", "記事を編集しました");

				} else {
					// ブログの編集内容を保存失敗した場合
					model.addAttribute("blogProcess", "編集失敗しました。後でもう一度お試しください。");
				}

			}
			return "blog_process.html";
		} else {
			// 未ログインの場合の処理
			model.addAttribute("blogProcess", "アカウントがログアウトされました。再度ログインしてください。");
			return "blog_process.html";
		}
	}

	// ブログを削除するメソッド
	@PostMapping("/blog/delete/process")
	public String blogDeteleProcess(@RequestParam Long blogId, Long userId, Model model) {
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		// ユーザーがログインしているかを確認
		if (blogService.checkUserLoggedIn()) {
			// ログイン中の場合の処理
			// userIdとblogIdに基づいて、ブログを削除
			if (blogService.deleteBlog(userId, blogId)) {
				model.addAttribute("blogProcess", "記事を削除しました");
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

	/*-----------------------------------11.22更新-----------------------------------*/
	// ブログの検索機能を処理し、結果を表示する
	@PostMapping("/search")
	public String search(@RequestParam String query, Long userId, Model model) {
		model.addAttribute("loggedIn", blogService.checkUserLoggedIn());
		// user_idと画面から受け取ったqueryに基づいての検索
		model.addAttribute("blogList", blogService.searchBlogList(query, userId, model));
		model.addAttribute("query", query);
		return "blog_search.html";
	}
}
