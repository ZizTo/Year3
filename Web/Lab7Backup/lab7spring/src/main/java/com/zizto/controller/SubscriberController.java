package com.zizto.controller;

import com.zizto.model.Subscriber;
import com.zizto.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subscribers")
public class SubscriberController {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @GetMapping
    public String listSubscribers(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fullName"));
        Page<Subscriber> subscribers = subscriberRepository.findAll(pageable);

        model.addAttribute("subscribers", subscribers.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", subscribers.getTotalPages());
        model.addAttribute("totalItems", subscribers.getTotalElements());
        
        return "subscribers/list";
    }

    @GetMapping("/new")
    public String newSubscriber(Model model) {
        model.addAttribute("subscriber", new Subscriber());
        return "subscribers/form";
    }

    @PostMapping
    public String saveSubscriber(@ModelAttribute Subscriber subscriber) {
        subscriberRepository.save(subscriber);
        return "redirect:/subscribers";
    }

   @GetMapping("/edit/{id}")
    public String editSubscriber(@PathVariable Integer id, Model model) {
        Subscriber subscriber = subscriberRepository.findById(id).orElse(null);
        if (subscriber != null) {
            model.addAttribute("subscriber", subscriber);
        } else {
            model.addAttribute("subscriber", new Subscriber());
        }
        return "subscribers/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSubscriber(@PathVariable Integer id) {
        subscriberRepository.deleteById(id);
        return "redirect:/subscribers";
    }
}
