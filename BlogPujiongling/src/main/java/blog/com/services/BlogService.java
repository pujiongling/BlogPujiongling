package blog.com.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	// ブログ一覧（登録したブログ全ての一覧）
	public List<BlogEntity> getAllBlog() {
		return blogDao.findAll();
	}

	// ブログ一覧（個人ホームページ）
	public List<BlogEntity> getBlogByUserId(Long userId) {
		if (userId == null) {
			return null;
		} else {
			return blogDao.findBlogByUserId(userId);
		}
	}

	// ブログ登録
	public BlogEntity saveBlog(String blogTitle, String categoryName, String blogImage, String blogArticle,
			Long userId) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		userId = user.getUserId();
		BlogEntity savedBlog = blogDao
				.save(new BlogEntity(blogTitle, categoryName, blogImage, blogArticle, userId, LocalDateTime.now()));
		return savedBlog;
	}

	// 編集する情報を表示
	public BlogEntity getBlogPost(Long blogId) {
		if (blogId == null) {
			return null;
		} else {
			return blogDao.findByBlogId(blogId);
		}
	}

	// 編集した内容を保存するメソッド
	public boolean editBlog(Long blogId, String blogTitle, String categoryName, String blogImage, String blogArticle,
			Long userId) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		userId = user.getUserId();
		if (blogId == null) {
			return false;
		} else {
			BlogEntity editingBlog = blogDao.findByBlogId(blogId);
			editingBlog.updateBlog(blogTitle, categoryName, blogImage, blogArticle, userId);
			blogDao.save(editingBlog);
			return true;
		}

	}

	// ブログを削除するメソッド
	@Transactional
	public boolean deleteBlog(Long blogId) {
		// blog_idを使ってブログが存在しているかを確認
		if (blogId == null) {
			return false;
		} else {
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
		}
	}

	// ブログを削除したあと、ファイルを削除する処理
	public void deleteFile(String fileName) {
		try {
			Files.deleteIfExists(Path.of("src/main/resources/static/blog-img/" + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}