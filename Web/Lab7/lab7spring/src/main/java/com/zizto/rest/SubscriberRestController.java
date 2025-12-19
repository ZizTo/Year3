package com.zizto.rest;

import com.zizto.model.Subscriber;
import com.zizto.repository.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/subscribers")
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriberRestController {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @GetMapping
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        List<Subscriber> subscribers = (List<Subscriber>) subscriberRepository.findAll();
        log.info("GET /api/subscribers - получено {} абонентов", subscribers.size());
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscriber> getSubscriberById(@PathVariable Integer id) {
        Optional<Subscriber> subscriber = subscriberRepository.findById(id);
        if (subscriber.isPresent()) {
            log.info("GET /api/subscribers/{} - найден", id);
            return ResponseEntity.ok(subscriber.get());
        } else {
            log.warn("GET /api/subscribers/{} - не найден", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        Subscriber saved = subscriberRepository.save(subscriber);
        log.info("POST /api/subscribers - создан абонент: {}", saved.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscriber> updateSubscriber(@PathVariable Integer id, @RequestBody Subscriber subscriber) {
        Optional<Subscriber> existing = subscriberRepository.findById(id);
        if (existing.isPresent()) {
            subscriber.setId(id);
            Subscriber updated = subscriberRepository.save(subscriber);
            log.info("PUT /api/subscribers/{} - обновлен: {}", id, updated.getFullName());
            return ResponseEntity.ok(updated);
        } else {
            log.warn("PUT /api/subscribers/{} - не найден", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Integer id) {
        if (subscriberRepository.existsById(id)) {
            subscriberRepository.deleteById(id);
            log.info("DELETE /api/subscribers/{} - удален", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("DELETE /api/subscribers/{} - не найден", id);
            return ResponseEntity.notFound().build();
        }
    }
}
