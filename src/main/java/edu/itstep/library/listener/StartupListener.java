package edu.itstep.library.listener;

import edu.itstep.library.entity.Author;
import edu.itstep.library.entity.Book;
import edu.itstep.library.entity.User;
import edu.itstep.library.repository.AuthorRepository;
import edu.itstep.library.repository.BookRepository;
import edu.itstep.library.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private AuthorRepository authorRepository;

    public StartupListener(BookRepository bookRepository, UserRepository userRepository,
                           AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        User user = userRepository.getOne(1);
        Author author = authorRepository.getOne(1);

        for (int i = 0; i < 20; i++) {
            Book book = new Book();
            book.setAuthor(author);
            book.setUser(user);
            book.setCountPages(100);
            book.setFileSize(0);
            book.setYear(Year.now().toString());
            book.setLanguage("ru");
            book.setTitle("Book " + i);
            book.setDescription("Description " + i);
            book.setFileExtension("");
            bookRepository.save(book);
        }
    }
}
