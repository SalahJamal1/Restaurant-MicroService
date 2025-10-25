package com.menu.app.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping
    public List<Items> findAllByCategory(@RequestParam String category) {
        return service.findAllByCategory(category);
    }

    @GetMapping("/{id}")
    public Items findById(@PathVariable Integer id) {
        return service.findById(id);
    }

}
