package blog.com.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.com.models.dao.CommentDao;
import blog.com.models.entity.CommentEntity;
import jakarta.transaction.Transactional;

@Service
public class CommentService {

	/*-----------------------------------11.23更新-----------------------------------*/

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private BlogService blogSerivce;

	// blog_idに基づいてコメントを取得
	public List<CommentEntity> getCommentsByBlogId(Long blogId) {
		return commentDao.findCommentByBlogId(blogId);
	}

	// コメントを保存するメソッド
	public CommentEntity saveComment(String commentContent, String reviewer, LocalDateTime commentAt, Long userId,
			Long blogId) {
		// BlogServiceのメソッドを使ってuserId,blogId,userNameを取得
		userId = blogSerivce.getCurrentUser().getUserId();
		blogId = blogSerivce.getBlogPost(blogId).getBlogId();
		reviewer = blogSerivce.getCurrentUser().getUserName();
		// 現時点を指定
		commentAt = LocalDateTime.now();
		// コメントをDBに保存する
		CommentEntity saveComment = commentDao
				.save(new CommentEntity(commentContent, reviewer, commentAt, userId, blogId));
		return saveComment;
	}

	// コメントを削除するメソッド
	@Transactional
	public boolean deleteComment(Long commentId, Long userId) {
		userId = blogSerivce.getCurrentUser().getUserId();
		CommentEntity comment = commentDao.findByCommentId(commentId);
		// もしコメントが存在し、
		// かつコメントは削除処理をするユーザーが作成した場合
		if (commentId != null && comment.getUserId().equals(userId)) {
			// コメントを削除
			commentDao.deleteByCommentId(commentId);
			return true;
		} else {
			return false;
		}
	}

}
