package com.back.domain.post.repository;

import com.back.domain.post.entity.PostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {

    Optional<PostCommentLike> findByMember_IdAndPostComment_Id(long memberId, long postCommentId);

    boolean existsByMember_IdAndPostComment_Id(long memberId, long postCommentId);

    long countByPostComment_Id(long postCommentId);

    @Modifying(clearAutomatically = true)
    @Query("delete from PostCommentLike pcl where pcl.postComment.id = :commentId")
    void deleteByPostCommentId(@Param("commentId") long commentId);

    @Modifying(clearAutomatically = true)
    @Query("delete from PostCommentLike pcl where pcl.postComment.post.id = :postId")
    void deleteByPostId(@Param("postId") long postId);
}
