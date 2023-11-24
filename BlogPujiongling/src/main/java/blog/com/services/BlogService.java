package blog.com.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
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

	// BlogDaoの依存性注入
	@Autowired
	private BlogDao blogDao;

	// HttpSessionの依存性注入
	@Autowired
	private HttpSession session;

	// 現在ログイン中のユーザー情報を取得メソッド
	public UserEntity getCurrentUser() {
		return (UserEntity) session.getAttribute("user");
	}

	// ユーザーのログイン状態を確認メソッド
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

	// ブログ一覧を取得メソッド
	public List<BlogEntity> getBlogList(Long userId, Model model) {
		List<BlogEntity> blogList;
		// ログイン状態を確認
		if (checkUserLoggedIn()) {
			// ログインいている場合、userIdに基づいて、個人ブログの一覧取得
			userId = getCurrentUser().getUserId();
			blogList = blogDao.findBlogByUserId(userId);
		} else {
			// ログインしていない場合、全てのブログ一覧を取得
			blogList = blogDao.findAll();
		}
		// 取得したブログを降順に並び替える、画面に転送
		Collections.sort(blogList, (blog1, blog2) -> blog2.getPostedAt().compareTo(blog1.getPostedAt()));
		model.addAttribute("blogList", blogList);
		return blogList;
	}

	// ブログ登録するメソッド
	public BlogEntity saveBlog(String blogTitle, String categoryName, String blogImage, String blogArticle, Long userId,
			Model model) {
		// 現在のユーザーIdを取得
		userId = getCurrentUser().getUserId();
		// 新しいブログエンティティを作成して保存
		BlogEntity savedBlog = blogDao
				.save(new BlogEntity(blogTitle, categoryName, blogImage, blogArticle, userId, LocalDateTime.now()));
		return savedBlog;
	}

	// 指定されたブログIDに基づいてブログ情報を取得
	public BlogEntity getBlogPost(Long blogId) {
		if (blogId != null) {
			return blogDao.findByBlogId(blogId);
		} else {
			return null;
		}
	}

	// 編集されたブログ内容を保存するメソッド
	public boolean editBlog(Long blogId, String blogTitle, String categoryName, String blogImage, String blogArticle,
			Long userId) {
		if (blogId != null) {
			userId = getCurrentUser().getUserId();
			// ブログIDに対応するBlogEntityをDBから取得
			BlogEntity editingBlog = blogDao.findByBlogId(blogId);
			// ブログを更新
			editingBlog.updateBlog(blogTitle, categoryName, blogImage, blogArticle, userId);
			// 更新されたブログを保存
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
		// 削除対象のブログIDが指定されているか確認
		if (blogId != null) {
			userId = getCurrentUser().getUserId();
			// 削除対象のBlogEntityをDBから取得
			BlogEntity deletedBlog = blogDao.findByBlogId(blogId);
			// 削除対象のfileNameを取得
			String fileName = deletedBlog.getBlogImage();
			// DBからブログを削除
			blogDao.deleteByBlogId(blogId);
			// 関連するファイルを削除
			deleteFile(fileName);
			return true;
		} else {
			return false;
		}
	}

	// 検索機能
	public List<BlogEntity> searchBlogList(String query, Long userId, Model model) {
		if (checkUserLoggedIn()) {
			userId = getCurrentUser().getUserId();
			// ログイン中のユーザーIDに基づいてクエリでブログを検索
			return blogDao.findByQueryAndUserId(query, userId);
		} else {
			// 未ログインの場合、クエリで全てのブログを検索
			return blogDao.findByQuery(query);
		}
	}

}