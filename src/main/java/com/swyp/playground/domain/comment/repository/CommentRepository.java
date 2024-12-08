package com.swyp.playground.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.playground.domain.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    public List<Comment> findAllByMatchId(Long matchId);
}
