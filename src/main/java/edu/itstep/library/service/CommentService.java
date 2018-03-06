package edu.itstep.library.service;

import edu.itstep.library.dto.CommentDto;

public interface CommentService {
    void addComment(CommentDto commentDto);
    void remove(Integer id);
}
