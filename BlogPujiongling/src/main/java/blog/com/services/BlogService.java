package blog.com.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	// ブログ登録
	public BlogEntity saveBlog(String blogTitle, String categoryName, String blogImage, String blogArticle,
			Long userId) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		userId = user.getUserId();
		/*
		 * BlogEntity blogEntity = new BlogEntity(); blogEntity.setBlogTitle(blogTitle);
		 * blogEntity.setCategoryName(categoryName); blogEntity.setBlogImage(blogImage);
		 * blogEntity.setBlogArticle(blogArticle); blogEntity.setUser(user);
		 * blogEntity.setPostedAt(LocalDateTime.now());
		 */
		BlogEntity savedBlog = blogDao
				.save(new BlogEntity(blogTitle, categoryName, blogImage, blogArticle, userId, LocalDateTime.now()));
		return savedBlog;
	}

	// ブログ一覧
	public List<BlogEntity> getBlogByUserId(Long userId) {
		if (userId == null) {
			return null;
		} else {
			return blogDao.findBlogByUserId(userId);
		}
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
	// product_id == null false
	// そうでない場合、更新処理をして、true
	public boolean editBlog(Long blogId, String blogTitle, String categoryName, String blogImage, String blogArticle,
			Long userId) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		userId = user.getUserId();
		if (blogId == null) {
			return false;
		} else {
			BlogEntity blogEntity = new BlogEntity(blogTitle, categoryName, blogImage, blogArticle, userId,
					LocalDateTime.now());
			BlogEntity savedBlog = blogDao.save(blogEntity);
			return true;
		}

	}
}