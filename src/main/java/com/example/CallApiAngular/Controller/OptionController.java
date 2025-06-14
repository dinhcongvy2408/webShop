package com.example.CallApiAngular.Controller;

import com.example.CallApiAngular.Service.OptionService;
import com.example.CallApiAngular.entity.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@CrossOrigin(origins = "http://localhost:4200")
public class OptionController {

    @Autowired
    private OptionService optionService;

    @GetMapping
    public ResponseEntity<List<Option>> getAllOptions() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }

    @GetMapping("/{href}")
    public ResponseEntity<Option> getOptionByHref(@PathVariable String href) {
        Option option = optionService.getOptionByHref(href);
        return option != null ? ResponseEntity.ok(option) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Option> createOption(@RequestBody Option option) {
        return ResponseEntity.ok(optionService.createOption(option));
    }

    @PutMapping
    public ResponseEntity<Option> updateOption(@RequestParam String href, @RequestBody Option option) {
    Option updated = optionService.updateOption(href, option);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{href}")
    public ResponseEntity<Void> deleteOption(@PathVariable String href) {
        optionService.deleteOption(href);
        return ResponseEntity.noContent().build();
    }
}
