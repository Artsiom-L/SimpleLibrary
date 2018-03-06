package edu.itstep.library.controller;

import edu.itstep.library.dto.CommentDto;
import edu.itstep.library.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void add(@RequestBody CommentDto commentDto) {
        commentService.addComment(commentDto);
    }

    @GetMapping("/{id}/remove")
    public String remove(@PathVariable Integer id, HttpServletRequest request) {
        commentService.remove(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}
