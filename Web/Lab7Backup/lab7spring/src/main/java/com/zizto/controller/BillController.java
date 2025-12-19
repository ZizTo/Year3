package com.zizto.controller;

import com.zizto.model.Bill;
import com.zizto.repository.BillRepository;
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
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @GetMapping
    public String listBills(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Bill> bills = billRepository.findAll(pageable);

        model.addAttribute("bills", bills.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bills.getTotalPages());
        model.addAttribute("totalItems", bills.getTotalElements());

        return "bills/list";
    }

    @GetMapping("/new")
    public String newBill(Model model) {
        model.addAttribute("bill", new Bill());
        model.addAttribute("subscribers", subscriberRepository.findAll());
        return "bills/form";
    }

    @PostMapping
    public String saveBill(@ModelAttribute Bill bill) {
        billRepository.save(bill);
        return "redirect:/bills";
    }

    @GetMapping("/pay/{id}")
    public String payBill(@PathVariable Integer id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            bill.setPaid(true);
            billRepository.save(bill);
        }
        return "redirect:/bills";
    }

    @GetMapping("/delete/{id}")
    public String deleteBill(@PathVariable Integer id) {
        billRepository.deleteById(id);
        return "redirect:/bills";
    }
}
