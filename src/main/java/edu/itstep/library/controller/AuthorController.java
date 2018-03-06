package edu.itstep.library.controller;

import edu.itstep.library.dto.AuthorDto;
import edu.itstep.library.entity.Author;
import edu.itstep.library.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/author")
public class AuthorController {
    AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/add")
    public String authorForm(Model model) {
        model.addAttribute("author", new AuthorDto());
        return "authorAdd";
    }

    @PostMapping("/add")
    public String addAuthor(@ModelAttribute("author") AuthorDto authorDto) throws IOException {
        authorService.addAuthor(authorDto);
        return "redirect:/";
    }

    @GetMapping("/{id}/remove")
    public String remove(@PathVariable Integer id) {
        authorService.remove(id);
        return "redirect:/";
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Integer id) throws IOException {
        return ResponseEntity.ok(authorService.image(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String searchAuthorById(@PathVariable String id, final ModelMap model) throws IOException {
        Author author = null;

        if (id != null && !id.isEmpty()) {
            author = authorService.findById(id);
        }
        model.addAttribute("author", author);
        model.addAttribute("images", image(Integer.valueOf(id)));

        return "author";
    }

}
