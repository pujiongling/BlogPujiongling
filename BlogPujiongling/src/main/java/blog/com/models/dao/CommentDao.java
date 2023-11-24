package blog.com.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.com.models.entity.CommentEntity;

@Repository
public interface CommentDao extends JpaRepository<CommentEntity, Long> {

	// save
	CommentEntity save(CommentEntity commentEntity);

	// comment_idに基づいてコメント取得
	CommentEntity findByCommentId(Long commentId);

	// blog_idに基づいてコメントを取得
	@Query("SELECT c FROM CommentEntity c WHERE c.blogId = :blogId")
	List<CommentEntity> findCommentByBlogId(@Param("blogId") Long blogId);

	// comment_idに基づいて削除
	int deleteByCommentId(Long commentId);

}
