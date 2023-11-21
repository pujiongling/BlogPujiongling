package blog.com.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.com.models.entity.BlogEntity;

@Repository
public interface BlogDao extends JpaRepository<BlogEntity, Long> {
	// 保存
	BlogEntity save(BlogEntity blogEntity);

	// ユーザーが登録したブログを全部取得
	List<BlogEntity> findAll();

	// user_idに基づいてブログを取得する
	@Query("SELECT b FROM BlogEntity b WHERE b.userId = :userId")
	List<BlogEntity> findBlogByUserId(@Param("userId") Long userId);

	// blog_idに基づいてブログを取得する
	BlogEntity findByBlogId(Long blogId);

	int deleteByBlogId(Long blogId);

}
