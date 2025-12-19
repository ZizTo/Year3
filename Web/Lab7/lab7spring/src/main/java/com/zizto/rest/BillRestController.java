package com.zizto.rest;

import com.zizto.model.Bill;
import com.zizto.repository.BillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:4200")
public class BillRestController {

    @Autowired
    private BillRepository billRepository;

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = (List<Bill>) billRepository.findAll();
        log.info("GET /api/bills - получено {} счетов", bills.size());
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Integer id) {
        Optional<Bill> bill = billRepository.findById(id);
        if (bill.isPresent()) {
            log.info("GET /api/bills/{} - найден", id);
            return ResponseEntity.ok(bill.get());
        } else {
            log.warn("GET /api/bills/{} - не найден", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Bill> createBill(@RequestBody Bill bill) {
        Bill saved = billRepository.save(bill);
        log.info("POST /api/bills - создан счет ID: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bill> updateBill(@PathVariable Integer id, @RequestBody Bill bill) {
        Optional<Bill> existing = billRepository.findById(id);
        if (existing.isPresent()) {
            bill.setId(id);
            Bill updated = billRepository.save(bill);
            log.info("PUT /api/bills/{} - обновлен", id);
            return ResponseEntity.ok(updated);
        } else {
            log.warn("PUT /api/bills/{} - не найден", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Integer id) {
        if (billRepository.existsById(id)) {
            billRepository.deleteById(id);
            log.info("DELETE /api/bills/{} - удален", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("DELETE /api/bills/{} - не найден", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Bill> payBill(@PathVariable Integer id) {
        Optional<Bill> bill = billRepository.findById(id);
        if (bill.isPresent()) {
            Bill b = bill.get();
            b.setPaid(true);
            Bill updated = billRepository.save(b);
            log.info("PUT /api/bills/{}/pay - счет оплачен", id);
            return ResponseEntity.ok(updated);
        } else {
            log.warn("PUT /api/bills/{}/pay - не найден", id);
            return ResponseEntity.notFound().build();
        }
    }
}
