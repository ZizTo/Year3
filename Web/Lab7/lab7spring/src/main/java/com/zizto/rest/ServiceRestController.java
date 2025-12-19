package com.zizto.rest;

import com.zizto.model.Service;
import com.zizto.repository.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceRestController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = (List<Service>) serviceRepository.findAll();
        log.info("GET /api/services - получено {} услуг", services.size());
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Integer id) {
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            log.info("GET /api/services/{} - найдена", id);
            return ResponseEntity.ok(service.get());
        } else {
            log.warn("GET /api/services/{} - не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service saved = serviceRepository.save(service);
        log.info("POST /api/services - создана услуга: {}", saved.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Integer id, @RequestBody Service service) {
        Optional<Service> existing = serviceRepository.findById(id);
        if (existing.isPresent()) {
            service.setId(id);
            Service updated = serviceRepository.save(service);
            log.info("PUT /api/services/{} - обновлена: {}", id, updated.getName());
            return ResponseEntity.ok(updated);
        } else {
            log.warn("PUT /api/services/{} - не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Integer id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            log.info("DELETE /api/services/{} - удалена", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("DELETE /api/services/{} - не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }
}
