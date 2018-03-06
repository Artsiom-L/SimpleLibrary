package edu.itstep.library.service;

import edu.itstep.library.dto.BookDto;
import edu.itstep.library.dto.BookFilterDto;
import edu.itstep.library.entity.Book;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Page<Book> listBooks(Optional<Integer> page, String search, String sort, int evalPage, int evalPageSize);

    void addBook(BookDto bookDto, Integer authorId) throws IOException;

    void remove(Integer id);

    byte[] image(Integer id) throws IOException;

    Book findById(String id);

    List<Book> search(BookFilterDto filter);
}
