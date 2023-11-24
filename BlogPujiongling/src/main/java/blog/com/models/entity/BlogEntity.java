package blog.com.models.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

	@NonNull
	private String blogTitle;
	@NonNull
	private String categoryName;
	@NonNull
	private String blogImage;
	@NonNull
	private String blogArticle;

	@NonNull
	private Long userId;

	@NonNull
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
