package com.example.demo.controller;

import com.example.demo.entity.Distributeur;
import com.example.demo.service.DistributeurService;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth/distributeur")
public class DistributeurController {
    private final DistributeurService service;

    public DistributeurController(DistributeurService service) {
        this.service = service;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public Distributeur saveDistributeur(@Valid @RequestBody Distributeur distributeur) {
        System.out.println("saved");
        return service.saveDistributeur(distributeur);
    }
    @GetMapping("/findAll")
    public List<Distributeur> findAllDistributeurs (){
        return service.findAllDistributeurs();
    }
    @GetMapping("/findById/{id}")
    public Distributeur findById(@PathVariable Integer id) {
        return service.findById(id);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
