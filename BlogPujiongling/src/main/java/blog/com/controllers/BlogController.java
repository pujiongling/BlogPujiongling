package blog.com.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
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

	@Autowired
	private BlogService blogService;

	// ログインしている人の情報を取得
	@Autowired
	private HttpSession session;

	// 個人ブログの一覧を取得する
	@GetMapping("/blog/list")
	public String getBlogList(Model model) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login";
		} else {
			Long userId = user.getUserId();
			List<BlogEntity> blogList = blogService.getBlogByUserId(userId);
			model.addAttribute("blogList", blogList);
			model.addAttribute("userName", user.getUserName());
			return "blog_list.html";
		}
	}

	// ブログ登録画面の表示
	@GetMapping("/blog/register")
	public String getBlogRegisterPage(Model model) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login";
		} else {
			model.addAttribute("userName", user.getUserName());
			return "blog_register.html";
		}
	}
	// ブログの登録

	@PostMapping("/blog/register/process")
	public String blogRegister(@RequestParam String blogTitle, @RequestParam String categoryName,
			@RequestParam MultipartFile blogImage, @RequestParam String blogArticle, Model model) {
		UserEntity user = (UserEntity) session.getAttribute("user");

		if (user == null) {
			return "redirect:/login";
		} else {
			Long userId = user.getUserId();
			String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
					+ blogImage.getOriginalFilename();
			if (blogService.saveBlog(blogTitle, categoryName, fileName, blogArticle, userId) != null) {
				try {
					Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "redirect:/blog/list";
			} else {
				model.addAttribute("error", "保存失敗しました。後でもう一度お試しください。");
				return "redirect:/blog/register";
			}

		}
	}

	// ブログ編集画面を表示する
	@GetMapping("/blog/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId, Model model) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login";
		} else {
			BlogEntity blogList = blogService.getBlogPost(blogId);
			if (blogList == null) {
				return "redirect:/blog/list";
			} else {
				model.addAttribute("userName", user.getUserName());
				model.addAttribute("blogList", blogList);
			}
			return "blog_edit.html";
		}
	}

	// 更新処理をするメソッド
	// 画像の場合 @RequestParam MultipartFile productImage
	@PostMapping("/blog/edit/process")
	public String blogEditProcess(@RequestParam Long blogId, @RequestParam String blogTitle,
			@RequestParam String categoryName, @RequestParam MultipartFile blogImage, @RequestParam String blogArticle,
			Model model) {
		UserEntity user = (UserEntity) session.getAttribute("user");

		if (user == null) {
			return "redirect:/login";
		} else {
			Long userId = user.getUserId();
			String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
					+ blogImage.getOriginalFilename();
			if (blogService.editBlog(blogId, blogTitle, categoryName, fileName, blogArticle, userId)) {
				try {
					Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "redirect:/blog/list";
			} else {
				model.addAttribute("error", "保存失敗しました。後でもう一度お試しください。");
				return "redirect:/blog/edit/" + blogId;
			}

		}

	}
}
