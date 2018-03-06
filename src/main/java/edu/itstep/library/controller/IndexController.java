package edu.itstep.library.controller;

import edu.itstep.library.dto.BookFilterDto;
import edu.itstep.library.entity.Book;
import edu.itstep.library.entity.Pager;
import edu.itstep.library.service.BookService;
import edu.itstep.library.service.CounterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class IndexController {
    private BookService bookService;
    private CounterService counterService;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 10;
    private static final int[] PAGE_SIZES = {10, 15, 30, 50};
    private String filesPath;

    public IndexController(BookService bookService, CounterService counterService,
                           @Value("${files.path}") String filesPath) {
        this.bookService = bookService;
        this.counterService = counterService;

        this.filesPath = filesPath;
    }

    @GetMapping("/")
    public String index(Model model,
                        HttpSession session,
                        @RequestParam("pageSize") Optional<Integer> pageSize,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam(name = "search", defaultValue = "") String search,
                        @RequestParam(name = "sort", defaultValue = "modified") String sort) {
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<Book> books = bookService.listBooks(page, search.trim(), sort, evalPage, evalPageSize);
        Pager pager = new Pager(books.getTotalPages(), books.getNumber(), BUTTONS_TO_SHOW);
        model.addAttribute("books", books);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("search", search);
        model.addAttribute("filter", new BookFilterDto());
        model.addAttribute("pager", pager);
        model.addAttribute("pageSizes", PAGE_SIZES);
        model.addAttribute("selectedPageSize", evalPageSize);

        model.addAttribute("counter", counterService.incrementAndGet());

        return "index";
    }

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public StreamingResponseBody getSteamingFile(HttpServletResponse response,
                                                 @PathVariable String id) throws IOException {
        response.setContentType("application/text");
        Book book = bookService.findById(id);
        InputStream inputStream = null;
        if (!book.getFileExtension().equals("")) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + id + "." +
                    book.getFileExtension() + "\"");
            inputStream = new FileInputStream(new File(String.valueOf(Paths.get(filesPath, id + "." +
                    book.getFileExtension()))));
        } else {
            inputStream = new FileInputStream(new File(String.valueOf(Paths.get(filesPath, id))));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + id + "\"");
        }
        InputStream finalInputStream = inputStream;
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = finalInputStream.read(data, 0, data.length)) != -1) {
                System.out.println("Writing some bytes..");
                outputStream.write(data, 0, nRead);
            }
        };
    }


}
