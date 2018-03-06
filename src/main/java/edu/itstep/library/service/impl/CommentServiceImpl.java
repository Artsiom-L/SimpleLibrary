package edu.itstep.library.service.impl;

import edu.itstep.library.dto.CommentDto;
import edu.itstep.library.entity.Comment;
import edu.itstep.library.repository.BookRepository;
import edu.itstep.library.repository.CommentRepository;
import edu.itstep.library.service.CommentService;
import edu.itstep.library.service.SecurityService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private BookRepository bookRepository;
    private SecurityService securityService;

    public CommentServiceImpl(CommentRepository commentRepository, BookRepository bookRepository, SecurityService securityService) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
        this.securityService = securityService;
    }

    @Override
    public void addComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setUser(securityService.getCurrentUser());

        comment.setBook(bookRepository.findOne(commentDto.getBookId()));

        commentRepository.save(comment);
    }

    @Override
    public void remove(Integer id) {
        commentRepository.delete(id);
    }
}
