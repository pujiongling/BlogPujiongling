package blog.com.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import blog.com.models.dao.BlogDao;
import blog.com.models.entity.BlogEntity;
import blog.com.models.entity.UserEntity;
import jakarta.servlet.http.HttpSession;

@Service
public class BlogService {
	@Autowired
	private BlogDao blogDao;

	@Autowired
	private HttpSession session;

	/*-----------------------------------11.22更新-----------------------------------*/
	// 現在のユーザー情報を取得
	public UserEntity getCurrentUser() {
		return (UserEntity) session.getAttribute("user");
	}

	// ユーザーがログインしているかを確認
	// ログインしている場合はユーザー情報を model に追加して true を返す
	// ログインしていない場合は false を返す。
	public boolean checkUserLoggedIn() {
		UserEntity user = getCurrentUser();
		if (user != null) {
			return true;
		}
		return false;
	}

	// fileの保存
	public void saveBlogImage(MultipartFile blogImage, String fileName) {
		try {
			Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ブログ一覧を取得
	// ログイン状態を確認
	// いている場合、userIdに基づいて、個人ブログの一覧取得
	// していない場合、全てのブログ一覧を取得
	public List<BlogEntity> getBlogList(Long userId, Model model) {
		List<BlogEntity> blogList;
		if (checkUserLoggedIn()) {
			userId = getCurrentUser().getUserId();
			blogList = blogDao.findBlogByUserId(userId);
		} else {
			blogList = blogDao.findAll();
		}
		/*
		 * 获取每篇博客的作者用户名，并添加到 model 中 for (BlogEntity blog : blogList) { String
		 * authorName = blogDao.findUserNameByUserId(userId); if (authorName != null) {
		 * 
		 * } }
		 */
		// 取得したブログを降順に並び替える
		Collections.sort(blogList, (blog1, blog2) -> blog2.getPostedAt().compareTo(blog1.getPostedAt()));
		model.addAttribute("blogList", blogList);
		return blogList;
	}

	// ブログ登録
	public BlogEntity saveBlog(String blogTitle, String categoryName, String blogImage, String blogArticle, Long userId,
			Model model) {
		userId = getCurrentUser().getUserId();
		BlogEntity savedBlog = blogDao
				.save(new BlogEntity(blogTitle, categoryName, blogImage, blogArticle, userId, LocalDateTime.now()));
		return savedBlog;
	}

	// 編集する情報を表示
	public BlogEntity getBlogPost(Long blogId) {
		if (blogId != null) {
			return blogDao.findByBlogId(blogId);
		} else {
			return null;
		}
	}

	// 編集した内容を保存するメソッド
	public boolean editBlog(Long blogId, String blogTitle, String categoryName, String blogImage, String blogArticle,
			Long userId) {
		if (blogId != null) {
			userId = getCurrentUser().getUserId();
			BlogEntity editingBlog = blogDao.findByBlogId(blogId);
			editingBlog.updateBlog(blogTitle, categoryName, blogImage, blogArticle, userId);
			blogDao.save(editingBlog);
			return true;
		} else {
			return false;
		}

	}

	// ファイルを削除する処理
	public void deleteFile(String fileName) {
		try {
			Files.deleteIfExists(Path.of("src/main/resources/static/blog-img/" + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ブログを削除するメソッド
	@Transactional
	public boolean deleteBlog(Long userId, Long blogId) {
		// blog_idを使ってブログが存在しているかを確認
		if (blogId != null) {
			userId = getCurrentUser().getUserId();
			// 削除したいブログが存在している場合
			// ブログの情報をdeletedBlogに
			BlogEntity deletedBlog = blogDao.findByBlogId(blogId);
			// 获取要删除的文件名
			String fileName = deletedBlog.getBlogImage();
			// 删除数据库中的记录
			blogDao.deleteByBlogId(blogId);
			// 删除关联的文件
			deleteFile(fileName);
			return true;
		} else {
			return false;
		}
	}

	/*-----------------------------------11.22更新-----------------------------------*/
	// 検索機能
	public List<BlogEntity> searchBlogList(String query, Long userId, Model model) {
		if (checkUserLoggedIn()) {
			userId = getCurrentUser().getUserId();
			return blogDao.findByQueryAndUserId(query, userId);
		} else {
			return blogDao.findByQuery(query);
		}
	}

}