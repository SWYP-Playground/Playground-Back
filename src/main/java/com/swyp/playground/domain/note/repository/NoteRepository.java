package com.swyp.playground.domain.note.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.playground.domain.note.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // 자신에게 온 쪽지 모두 조회
    public List<Note> findAllByTargetNickname(String targetNickname);
}
