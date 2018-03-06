package edu.itstep.library.repository;

import edu.itstep.library.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Page<Author> findAllByLastNameContainsOrFirstNameContainsOrNicknameContains(String name,
                                                                                String desc, Pageable pageable);

    List<Author> findAuthorsByLastNameContainsOrFirstNameContainsOrNicknameContains(String lastName, String firstName, String nickname);

    Page<Author> findAll(Pageable pageable);
}
