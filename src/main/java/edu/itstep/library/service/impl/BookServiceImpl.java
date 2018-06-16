package edu.itstep.library.service.impl;

import edu.itstep.library.dto.BookDto;
import edu.itstep.library.dto.BookFilterDto;
import edu.itstep.library.entity.Book;
import edu.itstep.library.entity.User;
import edu.itstep.library.repository.AuthorRepository;
import edu.itstep.library.repository.BookRepository;
import edu.itstep.library.repository.UserRepository;
import edu.itstep.library.service.BookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private String imagesPath;
    private RestTemplate restTemplate;
    private JdbcTemplate jdbcTemplate;
    private String filesPath;
    private AuthorRepository authorRepository;
    private static final String DEFAULT_IMAGE =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Book3.svg/495px-Book3.svg.png";

    public BookServiceImpl(BookRepository bookRepository,
                           UserRepository userRepository,
                           @Value("${images.path}") String imagesPath,
                           RestTemplate restTemplate, JdbcTemplate jdbcTemplate,
                           @Value("${files.path}") String filesPath, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.imagesPath = imagesPath;
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.filesPath = filesPath;
        this.authorRepository = authorRepository;
        checking();
    }

    /**
     * checking on existence of the folders
     */
    private void checking() {
        Path filePath = Paths.get(filesPath);
        Path imagePath = Paths.get(imagesPath);
        if (!Files.exists(filePath)) filePath.toFile().mkdirs();
        if (!Files.exists(imagePath)) imagePath.toFile().mkdirs();
    }

    @Override
    public Page<Book> listBooks(Optional<Integer> page, String search, String sort, int evalPage, int evalPageSize) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.equals("modified")) direction = Sort.Direction.DESC;
        Pageable pageable = new PageRequest(evalPage, evalPageSize, new Sort(direction, sort));
        if (search.isEmpty()) {
            return bookRepository.findAll(pageable);
        } else {
            return bookRepository.findAllByTitleContainsOrDescriptionContains(search, search, pageable);
        }
    }

    @Override
    public void addBook(BookDto bookDto, Integer authorId) throws IOException {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setLanguage(bookDto.getLanguage());
        book.setCountPages(bookDto.getCountPages());
        book.setAuthor(authorRepository.findOne(authorId));
        book.setYear(Year.parse(bookDto.getYear()).toString());
        int fileSizeInKB = Math.toIntExact(bookDto.getFile().getSize() / 1024);
        book.setFileSize(fileSizeInKB);
        book.setFileExtension(getFileExtension(bookDto.getFile()));

        UserDetails userDetails = UserDetails.class.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);

        book.setUser(user);

        bookRepository.save(book);

        byte[] image = null;
        byte[] file = null;
        try {
            image = bookDto.getImage().getBytes();
            file = bookDto.getFile().getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null && image.length != 0) {
            Files.write(Paths.get(imagesPath, book.getId().toString() + "-book"), bookDto.getImage().getBytes());
        }
        if (file != null && file.length != 0) {
            String extension = book.getFileExtension();
            if (extension.equals(""))
                Files.write(Paths.get(filesPath, book.getId().toString()), bookDto.getFile().getBytes());
            else Files.write(Paths.get(filesPath, book.getId().toString() + "." + extension),
                    bookDto.getFile().getBytes());
        }
    }

    private static String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex != -1 && lastIndex != 0)
            return fileName.substring(lastIndex + 1);
        else return "";
    }


    @Override
    public void remove(Integer id) {
        deleteAttachments(id);
        bookRepository.delete(id);
    }

    private void deleteAttachments(Integer id) {
        Book book = bookRepository.findOne(id);
        String extension = book.getFileExtension();
        try {
            Files.deleteIfExists(Paths.get(imagesPath + "/" + id.toString()));
            if (extension.equals("")) Files.deleteIfExists(Paths.get(filesPath + "/" + id.toString()));
            else Files.deleteIfExists(Paths.get(filesPath + "/" + id.toString() + "." + extension));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] image(Integer id) throws IOException {
        if (imageExists(id)) {
            return Files.readAllBytes(Paths.get(imagesPath, id.toString() + "-book"));
        } else if (imageExists("default-book")) {
            return Files.readAllBytes(Paths.get(imagesPath, "default-book"));
        } else {
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(DEFAULT_IMAGE,
                    HttpMethod.GET,
                    new HttpEntity<Void>(new HttpHeaders()),
                    byte[].class);
            Files.write(Paths.get(imagesPath + "/default-book"), responseEntity.getBody());
            return Files.readAllBytes(Paths.get(imagesPath, "default-book"));
        }
    }

    @Override
    public Book findById(String id) {
        return bookRepository.findOne(Integer.valueOf(id));
    }

    @Override
    public List<Book> search(BookFilterDto filter) {
        String sql = "SELECT * FROM books WHERE ";
        sql += "(year BETWEEN " + filter.getMinYear() + " AND " + filter.getMaxYear() + ")";
        if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
            if (filter.isStrict()) {
                sql += " AND ";
            } else {
                sql += " OR ";
            }
            sql += "title LIKE '%" + filter.getTitle().trim() + "%'";
        }
        if (filter.getDescription() != null && !filter.getDescription().trim().isEmpty()) {
            if (filter.isStrict()) {
                sql += " AND ";
            } else {
                sql += " OR ";
            }
            sql += "description LIKE '%" + filter.getDescription().trim() + "%'";
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            if (filter.isStrict()) {
                sql += " AND ";
            } else {
                sql += " OR ";
            }
            sql += "(modified BETWEEN '" + new java.sql.Timestamp(filter.getStartDate().getTime()) + "'"
                    + " AND '" + new java.sql.Timestamp(filter.getEndDate().getTime()) + "')";
        }
        if (filter.getStartDate() != null) {
            if (filter.isStrict()) {
                sql += " AND ";
            } else {
                sql += " OR ";
            }
            sql += "modified > '" + new java.sql.Timestamp(filter.getStartDate().getTime()) + "'";
        }
        if (filter.getEndDate() != null) {
            if (filter.isStrict()) {
                sql += " AND ";
            } else {
                sql += " OR ";
            }
            sql += "modified < '" + new java.sql.Timestamp(filter.getEndDate().getTime()) + "'";
        }
        sql += " ORDER BY " + filter.getSortBy();
        List<Book> books = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Book.class));
        if (filter.isOnlyWithImages()) {
            return books
                    .stream()
                    .filter(this::imageExists)
                    .collect(Collectors.toList());
        } else {
            return books;
        }
    }

    private boolean imageExists(Book book) {
        return imageExists(book.getId());
    }

    private boolean imageExists(Integer id) {
        return Files.exists(Paths.get(imagesPath, id.toString() + "-book"));
    }

    private boolean imageExists(String id) {
        return Files.exists(Paths.get(imagesPath, id));
    }
}
