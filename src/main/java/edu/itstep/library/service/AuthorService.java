package edu.itstep.library.service;

import edu.itstep.library.dto.AuthorDto;
import edu.itstep.library.entity.Author;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Page<Author> listAuthors(Optional<Integer> page, String search, String sort, int evalPage, int evalPageSize);

    void addAuthor(AuthorDto authorDto) throws IOException;

    void remove(Integer id);

    byte[] image(Integer id) throws IOException;

    Author findById(String id);

    List<Author> search(String search);
}
