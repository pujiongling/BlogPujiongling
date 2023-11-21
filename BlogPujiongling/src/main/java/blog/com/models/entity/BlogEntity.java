package blog.com.models.entity;

import java.time.LocalDateTime;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "blog")
public class BlogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long blogId;

	@Nonnull
	private String blogTitle;
	@Nonnull
	private String categoryName;
	@Nonnull
	private String blogImage;
	@Nonnull
	private String blogArticle;

	@Nonnull
	private Long userId;

	@Nonnull
	private LocalDateTime postedAt;

	public void updateBlog(String blogTitle, String categoryName, String blogImage, String blogArticle, Long userId) {
		this.blogTitle = blogTitle;
		this.categoryName = categoryName;
		this.blogImage = blogImage;
		this.blogArticle = blogArticle;
		this.userId = userId;
		this.postedAt = LocalDateTime.now();
	}

}
