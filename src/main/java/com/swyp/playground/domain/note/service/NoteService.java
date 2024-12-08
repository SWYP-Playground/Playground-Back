package com.swyp.playground.domain.note.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swyp.playground.domain.note.domain.Note;
import com.swyp.playground.domain.note.dto.WriteNoteDto;
import com.swyp.playground.domain.note.repository.NoteRepository;
import com.swyp.playground.domain.parent.repository.ParentRepository;

@Service
public class NoteService {
    
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ParentRepository parentRepository;
    
    /* -- POST -- */
    public Note sendNote(Note note) {
        return noteRepository.save(note);
    }

    /* -- GET -- */
    public List<Note> getAllNotes(String email) {
        String targetId = parentRepository.findByEmail(email).get().getNickname();
        return noteRepository.findAllByTargetId(targetId);
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
        .orElseThrow(() -> new NullPointerException("해당 쪽지가 존재하지 않습니다."));
    }

    /* -- UPDATE -- */
    public void patchNoteById(Long id, WriteNoteDto newNote, String email) {
        try {
            Note prevNote = noteRepository.findById(id)
                            .orElseThrow(() -> new NullPointerException("해당 쪽지가 존재하지 않습니다."));
            // 실제 작성자 검증 로직
            if (prevNote.getWrittenBy() == parentRepository.findByEmail(email).get().getNickname()) {
                if (prevNote.getContent() != newNote.getContent()) { // 변경사항 있을 시에만 업데이트
                    prevNote.setContent(newNote.getContent());
                    noteRepository.save(prevNote);
                }
            } else {
                throw new IllegalAccessError("작성자만 수정할 수 있습니다.");
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /* -- DELETE -- */
    public void deleteNote(Long id, String email) {
        String nickname = parentRepository.findByEmail(email).get().getNickname();
        String writtenBy = noteRepository.findById(id).get().getWrittenBy();
        if (nickname.equals(writtenBy)) {
            noteRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }
    }
}
