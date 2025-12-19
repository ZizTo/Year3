package com.zizto.controller;

import com.zizto.model.Service;
import com.zizto.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public String listServices(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Service> services = serviceRepository.findAll(pageable);

        model.addAttribute("services", services.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", services.getTotalPages());
        model.addAttribute("totalItems", services.getTotalElements());

        return "services/list";
    }

    @GetMapping("/new")
    public String newService(Model model) {
        model.addAttribute("service", new Service());
        return "services/form";
    }

    @PostMapping
    public String saveService(@ModelAttribute Service service) {
        serviceRepository.save(service);
        return "redirect:/services";
    }

    @GetMapping("/edit/{id}")
    public String editService(@PathVariable Integer id, Model model) {
        Service service = serviceRepository.findById(id).orElse(null);
        if (service != null) {
            model.addAttribute("service", service);
        } else {
            model.addAttribute("service", new Service());
        }
        return "services/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Integer id) {
        serviceRepository.deleteById(id);
        return "redirect:/services";
    }
}
