package blog.com.models.entity;

import java.time.LocalDateTime;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "comment")
public class CommentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long commentId;
	@Nonnull
	private String commentContent;
	@Nonnull
	private String reviewer;
	@Nonnull
	private LocalDateTime commentAt;
	@Nonnull
	private Long userId;
	@Nonnull
	private Long blogId;

}
