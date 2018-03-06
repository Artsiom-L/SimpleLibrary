package edu.itstep.library.controller;

import edu.itstep.library.dto.BookDto;
import edu.itstep.library.entity.Book;
import edu.itstep.library.service.BookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/book")
public class BookController {
    private BookService bookService;
    private String filesPath;

    public BookController(BookService bookService,
                          @Value("${files.path}") String filesPath) {
        this.bookService = bookService;
        this.filesPath = filesPath;
    }


    @GetMapping("/add/{id}")
    public String bookForm(Model model, @PathVariable Integer id) {
        model.addAttribute("book", new BookDto());
        return "bookAdd";
    }

    @PostMapping("/add/{id}")
    public String addBook(@ModelAttribute("book") BookDto bookDto, @PathVariable Integer id) throws IOException {
        bookService.addBook(bookDto, id);
        return "redirect:/";
    }

    @GetMapping("/{id}/remove")
    public String remove(@PathVariable Integer id) {
        bookService.remove(id);
        return "redirect:/";
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Integer id) throws IOException {
        return ResponseEntity.ok(bookService.image(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String searchBookById(@PathVariable String id, final ModelMap model) throws IOException {
        Book book = null;

        if (id != null && !id.isEmpty()) {
            book = bookService.findById(id);
        }
        model.addAttribute("book", book);
        model.addAttribute("images", image(Integer.valueOf(id)));

        return "book";
    }

}
