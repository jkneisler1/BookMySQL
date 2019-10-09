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
        model.addAttribute("books", bookRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String bookForm(Model model) {
        model.addAttribute("book", new Book());
        return "bookform";
    }

    @PostMapping("/process")
    public String processForm(@ModelAttribute  Book book, @RequestParam(name="inStock") String inStock, BindingResult result) {
        /**if (result.hasErrors()) {                               // Check for errors
            return "bookform";
        }
        */
        if (inStock.equalsIgnoreCase("yes")) {      // Convert the HTML string inStock to a boolean
            book.setInStock(true);
        }
        else {
            book.setInStock(false);
        }

        if (result.hasErrors()) {                               // Check for errors
            return "bookform";
        }

        bookRepository.save(book);
        return "redirect:/";
    }

    @PostMapping("/processAuthor")
    public String processAuthorSearch(Model model, @RequestParam(name="search") String search) {
        model.addAttribute("books", bookRepository.findBookByAuthor(search));
        return "searchlist";
    }

    @PostMapping("/processTitle")
    public String processTitleSearch(Model model, @RequestParam(name="search") String search) {
        model.addAttribute("books", bookRepository.findBookByTitle(search));
        return "searchlist";
    }

    @RequestMapping(value="/managebooks/{id}", method=RequestMethod.POST, params="action")
    public String manageBooks(@PathVariable("id") long id, Model model, @RequestParam(value="action") String action) {
        String returnStr = "";

        if (action.equals("edit")) {
            if (bookRepository.findById(id).isPresent()) {
                model.addAttribute("book", bookRepository.findById(id).get());
                returnStr = "bookform";
            }
        }
        else if (action.equals("delete")) {
            bookRepository.deleteById(id);
            returnStr = "redirect:/";
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
