package edu.itstep.library.service.impl;

import edu.itstep.library.dto.AuthorDto;
import edu.itstep.library.entity.Author;
import edu.itstep.library.entity.User;
import edu.itstep.library.repository.AuthorRepository;
import edu.itstep.library.repository.UserRepository;
import edu.itstep.library.service.AuthorService;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {
    AuthorRepository authorRepository;
    UserRepository userRepository;
    private String imagesPath;
    private RestTemplate restTemplate;
    private JdbcTemplate jdbcTemplate;
    private static final String DEFAULT_IMAGE =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/P_author.svg/2000px-P_author.svg.png";

    public AuthorServiceImpl(AuthorRepository authorRepository,
                             UserRepository userRepository,
                             @Value("${images.path}") String imagesPath,
                             RestTemplate restTemplate,
                             JdbcTemplate jdbcTemplate) {
        this.authorRepository = authorRepository;
        this.userRepository = userRepository;
        this.imagesPath = imagesPath;
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<Author> listAuthors(Optional<Integer> page, String search,
                                    String sort, int evalPage, int evalPageSize) {
        Pageable pageable = new PageRequest(evalPage, evalPageSize, new Sort(sort));
        if (search.isEmpty()) {
            return authorRepository.findAll(pageable);
        } else {
            return authorRepository.
                    findAllByLastNameContainsOrFirstNameContainsOrNicknameContains(search, search, pageable);
        }
    }

    @Override
    public void addAuthor(AuthorDto authorDto) throws IOException {
        Author author = new Author();
        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());
        author.setMiddleName(authorDto.getMiddleName());
        author.setNickname(authorDto.getNickname());
        author.setHomepage(authorDto.getHomepage());
        author.setEmail(authorDto.getEmail());
        author.setGender((byte) authorDto.getGender());
        author.setBirthDate(LocalDate.parse(authorDto.getBirthDate()));

        UserDetails userDetails = UserDetails.class.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);

        author.setUser(user);
        authorRepository.save(author);

        byte[] image = null;
        try {
            image = authorDto.getImage().getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null && image.length != 0) {
            Files.write(Paths.get(imagesPath, author.getId().toString() + "-author"),
                        authorDto.getImage().getBytes());
        }
    }

    /*private LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }*/

    @Override
    public void remove(Integer id) {
        authorRepository.delete(id);
    }

    @Override
    public byte[] image(Integer id) throws IOException {
        if (imageExists(id)) {
            return Files.readAllBytes(Paths.get(imagesPath, id.toString() + "-author"));
        } else if (imageExists("default-author")) {
            return Files.readAllBytes(Paths.get(imagesPath, "default-author"));
        } else {
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(DEFAULT_IMAGE,
                    HttpMethod.GET,
                    new HttpEntity<Void>(new HttpHeaders()),
                    byte[].class);
            Files.write(Paths.get(imagesPath + "/default-author"), responseEntity.getBody());
            return Files.readAllBytes(Paths.get(imagesPath, "default-author"));        }
    }

    @Override
    public Author findById(String id) {
        return authorRepository.findOne(Integer.valueOf(id));
    }

    @Override
    public List<Author> search(String search) {
        search = search.trim();
        String sql = "SELECT * FROM authors WHERE last_name LIKE '%" + search + "%' OR first_name LIKE '%" +
                search + "%' OR nickname LIKE '%" + search + "%'";
        List<Author> books = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Author.class));
        /*return books
                .stream()
                .filter(this::imageExists)
                .collect(Collectors.toList());*/
        return books;
    }

    private boolean imageExists(Integer id) {
        return Files.exists(Paths.get(imagesPath, id.toString() + "-author"));
    }
    private boolean imageExists(String id) {
        return Files.exists(Paths.get(imagesPath, id));
    }

    private boolean imageExists(Author author) {
        return imageExists(author.getId());
    }
}
