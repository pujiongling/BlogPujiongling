package blog.com.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.com.models.entity.BlogEntity;

@Repository
public interface BlogDao extends JpaRepository<BlogEntity, Long> {
	// 保存処理
	BlogEntity save(BlogEntity blogEntity);

	// ユーザーが登録した全てのブログを取得する
	List<BlogEntity> findAll();

	// user_idに基づいてブログを取得する
	@Query("SELECT b FROM BlogEntity b WHERE b.userId = :userId")
	List<BlogEntity> findBlogByUserId(@Param("userId") Long userId);

	// blog_idに基づいてブログを取得する
	BlogEntity findByBlogId(Long blogId);

	// blog_idに基づいてブログを削除する
	int deleteByBlogId(Long blogId);

	// queryに基づいて全てのブログの検索
	// SELECT * FROM blog WHERE blogTitle like '%content%'
	@Query("SELECT b FROM BlogEntity b " + "WHERE b.blogTitle LIKE %:query% " + "OR b.categoryName LIKE %:query% "
			+ "OR b.blogArticle LIKE %:query%")
	List<BlogEntity> findByQuery(@Param("query") String query);

	// user_idとqueryに基づいての検索
	@Query("SELECT b FROM BlogEntity b " + "WHERE b.userId = :userId " + "AND (b.blogTitle LIKE %:query% "
			+ "OR b.categoryName LIKE %:query% " + "OR b.blogArticle LIKE %:query%)")
	List<BlogEntity> findByQueryAndUserId(@Param("query") String query, @Param("userId") Long userId);

	// user_idに基づいてユーザー名を取得
	@Query("SELECT u.userName FROM UserEntity u WHERE u.userId = :userId")
	String findUserNameByUserId(@Param("userId") Long userId);

}
