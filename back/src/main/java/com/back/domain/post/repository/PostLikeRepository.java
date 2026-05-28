package com.back.domain.post.repository;

import com.back.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByMember_IdAndPost_Id(long memberId, long postId);

    boolean existsByMember_IdAndPost_Id(long memberId, long postId);

    long countByPost_Id(long postId);

    @Modifying(clearAutomatically = true)
    @Query("delete from PostLike pl where pl.post.id = :postId")
    void deleteByPostId(@Param("postId") long postId);
}
