package edu.itstep.library.repository;

import edu.itstep.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findAllByTitleContainsOrDescriptionContains(String name, String desc, Pageable pageable);
    Page<Book> findAll(Pageable pageable);
}
