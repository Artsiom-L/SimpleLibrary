package edu.itstep.library.controller;

import edu.itstep.library.dto.BookFilterDto;
import edu.itstep.library.entity.Author;
import edu.itstep.library.entity.Book;
import edu.itstep.library.service.AuthorService;
import edu.itstep.library.service.BookService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class SearchController {
    private BookService bookService;
    private AuthorService authorService;

    public SearchController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @InitBinder
    public void bindingPreparation(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        CustomDateEditor orderDateEditor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, orderDateEditor);
    }

    @PostMapping("/search")
    public String search(Model model,
                         @ModelAttribute("filter") BookFilterDto filter) {
        List<Book> books = bookService.search(filter);
        model.addAttribute("filter", filter);
        model.addAttribute("books", books);
        return "search";
    }

    @GetMapping("/search")
    public String searchRedirect() {
        return "redirect:/";
    }

    @RequestMapping(value = "/author-search", method = RequestMethod.GET)
    public String getTags(@RequestParam(value = "search", required = false) String searchTerm, final ModelMap model) {
        List<Author> authorList = new ArrayList<>();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            authorList = authorService.search(searchTerm);
        }
        System.out.println(searchTerm);
        for (Author author : authorList) {
            System.out.println(author.toString());
        }

        model.addAttribute("search", searchTerm);
        model.addAttribute("catalog", authorList);

        return "authorSearch";

    }

}
