package com.back.domain.post.repository;

import com.back.domain.post.entity.Post;
import com.back.domain.post.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostOrderByIdAsc(Post post);

    long countByParent(PostComment parent);

    long countByPost(Post post);

    @Modifying(clearAutomatically = true)
    @Query("delete from PostComment c where c.post.id = :postId and c.parent is not null")
    void deleteRepliesByPostId(@Param("postId") long postId);

    @Modifying(clearAutomatically = true)
    @Query("delete from PostComment c where c.post.id = :postId and c.parent is null")
    void deleteRootsByPostId(@Param("postId") long postId);
}
