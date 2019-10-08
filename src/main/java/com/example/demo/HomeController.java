package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    BookRepository bookRepository;

    @RequestMapping("/")
    public String listBooks(Model model) {
        model.addAttribute("book", bookRepository);
        return "list";
    }

    @GetMapping("/add")
    public String bookForm(Model model) {
        model.addAttribute("book", new Book());
        return "bookform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Book book, BindingResult result) {
        if (result.hasErrors()) {
            return "bookform";
        }
        bookRepository.save(book);
        return "redirect:/";
    }

    @PostMapping("/processAuthor")
    public String processAuthorSearch(Model model, @RequestParam(name="search") String search) {
        model.addAttribute("book", bookRepository.findBookByAuthor(search));
        return "searchlist";
    }

    @PostMapping("/processTitle")
    public String processTitleSearch(Model model, @RequestParam(name="search") String search) {
        model.addAttribute("book", bookRepository.findBookByTitle(search));
        return "searchlist";
    }

    @RequestMapping(value="/managebooks/{id}", params="action")
    public String manageBooks(@PathVariable("id") long id, Model model, @RequestParam(value="action") String action) {
        String returnStr = "redirect:/";

        if (action.equals("edit")) {
            if (bookRepository.findById(id).isPresent()) {
                model.addAttribute("book", bookRepository.findById(id).get());
                returnStr = "bookform";
            }
        }
        else if (action.equals("delete")) {
            bookRepository.deleteById(id);
        }
        else if (action.equals("details")) {
            if (bookRepository.findById(id).isPresent()) {
                model.addAttribute("book", bookRepository.findById(id).get());
                returnStr = "list";
            }
        }
        return returnStr;
    }
}
