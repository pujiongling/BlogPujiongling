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

	// ログインしている人の情報を取得
	@Autowired
	private HttpSession session;

	// 個人ブログの一覧を表示するメソッド
	@GetMapping("/blog/list")
	public String getBlogList(Model model) {
		// セッションからログインしている人の情報を取得
		UserEntity user = (UserEntity) session.getAttribute("user");
		// ユーザーがログインしているかを確認
		if (user == null) {
			// ユーザーがログインしていない場合
			return "redirect:/blog/list/all";
		} else {
			// ユーザーがログインしている場合
			// userからuserIdを取得する
			Long userId = user.getUserId();
			// user_idに基づいてブログの情報を取得する
			List<BlogEntity> blogList = blogService.getBlogByUserId(userId);
			// 取得したブログを降順に並び替える
			Collections.sort(blogList, (blog1, blog2) -> blog2.getPostedAt().compareTo(blog1.getPostedAt()));
			// 取得したブログ情報とユーザー情報とを画面に渡す ブログ一覧画面を表示
			model.addAttribute("blogList", blogList);
			model.addAttribute("userName", user.getUserName());
			return "blog_list.html";
		}
	}

	// 待完善
	// 登録したブログ全ての一覧を表示するメソッド
	@GetMapping("/blog/list/all")
	public String getBlogAllList(Model model) {
		// 登録したブログ全てを取得する
		List<BlogEntity> blogList = blogService.getAllBlog();
		model.addAttribute("blogList", blogList);
		return "blog_list.html";
	}

	// ブログの登録画面を表示するメソッド
	@GetMapping("/blog/register")
	public String getBlogRegisterPage(Model model) {
		// セッションからログインしている人の情報を取得
		UserEntity user = (UserEntity) session.getAttribute("user");
		// ユーザーがログインしているかを確認
		if (user == null) {
			// ユーザーがログインしていない場合 ログイン画面に転送
			return "redirect:/login";
		} else {
			// ユーザーがログインしている場合
			// ユーザー名を画面に渡す ブログ登録画面を表示
			model.addAttribute("userName", user.getUserName());
			return "blog_register.html";
		}
	}

	// ブログの登録内容を保存するメソッド
	@PostMapping("/blog/register/process")
	public String blogRegister(@RequestParam String blogTitle, @RequestParam String categoryName,
			@RequestParam MultipartFile blogImage, @RequestParam String blogArticle, Model model) {
		// セッションからログインしている人の情報を取得
		UserEntity user = (UserEntity) session.getAttribute("user");
		// ユーザーがログインしているかを確認
		if (user == null) {
			// ユーザーがログインしていない場合 ログイン画面に転送
			return "redirect:/login";
		} else {
			// ユーザーがログインしている場合
			// userからuserIdを取得する
			Long userId = user.getUserId();
			// SimpleDateFormatクラスを使用して
			// 現在の日時情報を "yyyy-MM-dd-HH-mm-ss-" フォーマットで取得
			// そして、blogImageから元のファイル名を取得、フォーマットされた日時文字列と連結して、fileName変数に代入
			String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
					+ blogImage.getOriginalFilename();
			// 画面から登録した内容をBlogServiceクラスの保存処理メソッドに渡し、保存成功したかを確認
			if (blogService.saveBlog(blogTitle, categoryName, fileName, blogArticle, userId) != null) {
				// try-catchで 受け取ったファイルを保存
				try {
					// blogImageのInputStreamからファイルをコピーして、指定されたパスに保存
					Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
				// ブログの登録が成功した場合、成功メーセージを画面に渡す
				model.addAttribute("blogProcess", "新しい記事を追加しました");
				return "blog_process.html";
			} else {
				// ブログの登録が失敗した場合、エラーメッセージを画面に渡す
				model.addAttribute("error", "保存失敗しました。後でもう一度お試しください。");
				return "redirect:/blog/register";
			}

		}
	}

	// ブログ編集画面を表示するメソッド
	@GetMapping("/blog/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId, Model model) {
		// セッションからログインしている人の情報を取得
		UserEntity user = (UserEntity) session.getAttribute("user");
		// ユーザーがログインしているかを確認
		if (user == null) {
			// ユーザーがログインしていない場合 ログイン画面に転送
			return "redirect:/login";
		} else {
			// ユーザーがログインしている場合
			// blogIdを基づいてブログの詳細内容を取得
			BlogEntity blogList = blogService.getBlogPost(blogId);
			if (blogList == null) {
				// ブログが存在していない場合
				return "redirect:/blog/list";
			} else {
				// ブログに詳細が取得した場合
				// ブログ情報とユーザー情報を画面に渡す
				model.addAttribute("userName", user.getUserName());
				model.addAttribute("blogList", blogList);
			}
			return "blog_edit.html";
		}
	}

	// ブログの編集内容を保存するメソッド
	@PostMapping("/blog/edit/process")
	public String blogEditProcess(@RequestParam Long blogId, @RequestParam String blogTitle,
			@RequestParam String categoryName, @RequestParam MultipartFile blogImage, @RequestParam String blogArticle,
			Model model) {
		// セッションからログインしている人の情報を取得
		UserEntity user = (UserEntity) session.getAttribute("user");
		// ユーザーがログインしているかを確認
		if (user == null) {
			// ユーザーがログインしていない場合 ログイン画面に転送
			return "redirect:/login";
		} else {
			// ユーザーがログインしている場合
			// userからuserIdを取得する
			Long userId = user.getUserId();
			// SimpleDateFormatクラスを使用して
			// 現在の日時情報を "yyyy-MM-dd-HH-mm-ss-" フォーマットで取得
			// そして、blogImageから元のファイル名を取得、フォーマットされた日時文字列と連結して、fileName変数に代入
			String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
					+ blogImage.getOriginalFilename();
			// 画面からのデータをBlogServiceクラスの編集処理メソッドに渡し、編集の内容を保存成功したかを確認
			if (blogService.editBlog(blogId, blogTitle, categoryName, fileName, blogArticle, userId)) {
				// ブログの保存が成功した場合
				// try-catchで 受け取ったファイルを保存
				try {
					// blogImageのInputStreamからファイルをコピーして、指定されたパスに保存
					Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 編集成功した場合、成功メーセージを画面に渡す
				model.addAttribute("blogProcess", "記事を編集しました");
				return "blog_process.html";
			} else {
				// 編集失敗した場合、エラーメッセージを画面に渡す
				model.addAttribute("error", "保存失敗しました。後でもう一度お試しください。");
				return "redirect:/blog/edit/" + blogId;
			}

		}

	}

	// 待完善
	// ブログを削除するメソッド
	@PostMapping("/blog/delete/process")
	public String blogEditProcess(@RequestParam Long blogId, Model model) {
		// セッションからログインしている人の情報を取得
		UserEntity user = (UserEntity) session.getAttribute("user");
		// ユーザーがログインしているかを確認
		if (user == null) {
			// ユーザーがログインしていない場合 ログイン画面に転送
			return "redirect:/login";
		} else {
			// 画面からのデータをBlogServiceクラスの削除処理メソッドに渡し、削除成功したかを確認
			if (blogService.deleteBlog(blogId)) {
				// 削除成功した場合、成功メーセージを画面に渡す
				model.addAttribute("blogProcess", "記事を削除しました");
				return "blog_process.html";
			} else {
				// 削除失敗した場合、エラーメッセージを画面に渡す
				model.addAttribute("error", "保存失敗しました。後でもう一度お試しください。");
				return "redirect:/blog/edit/" + blogId;
			}
		}
	}
}
