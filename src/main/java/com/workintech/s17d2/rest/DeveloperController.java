
        package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers = new HashMap<>();

    private final Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();

        developers.put(1, new Developer(1, "Ali", 30000, Experience.JUNIOR));
        developers.put(2, new Developer(2, "Ayşe", 50000, Experience.MID));
        developers.put(3, new Developer(3, "Mehmet", 80000, Experience.SENIOR));
    }

    // GET: Tüm developer değerlerini listele
    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    // GET: ID ile developer getir
    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    // POST: Yeni developer ekle ve vergi sonrası maaşı hesapla
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer addDeveloper(@RequestBody Developer request) {

        int id = request.getId();
        String name = request.getName();
        double salary = request.getSalary();
        Experience experience = request.getExperience();

        double taxRate;
        Developer developer;

        switch (experience) {
            case JUNIOR:
                taxRate = taxable.getSimpleTaxRate();
                developer = new JuniorDeveloper(
                        id, name, salary - salary * taxRate / 100
                );
                break;

            case MID:
                taxRate = taxable.getMiddleTaxRate();
                developer = new MidDeveloper(
                        id, name, salary - salary * taxRate / 100
                );
                break;

            case SENIOR:
                taxRate = taxable.getUpperTaxRate();
                developer = new SeniorDeveloper(
                        id, name, salary - salary * taxRate / 100
                );
                break;

            default:
                throw new IllegalArgumentException("Invalid experience");
        }

        developers.put(id, developer);
        return developer;
    }

    // PUT: Developer bilgilerini güncelle
    @PutMapping("/{id}")
    public Developer updateDeveloper(
            @PathVariable int id,
            @RequestBody Developer request) {

        Developer developer;

        switch (request.getExperience()) {
            case JUNIOR:
                developer = new JuniorDeveloper(
                        id, request.getName(), request.getSalary()
                );
                break;

            case MID:
                developer = new MidDeveloper(
                        id, request.getName(), request.getSalary()
                );
                break;

            case SENIOR:
                developer = new SeniorDeveloper(
                        id, request.getName(), request.getSalary()
                );
                break;

            default:
                throw new IllegalArgumentException("Invalid experience");
        }

        developers.put(id, developer);
        return developer;
    }

    // DELETE: Developer kaydını sil
    @DeleteMapping("/{id}")
    public Developer deleteDeveloper(@PathVariable int id) {
        return developers.remove(id);
    }
}
