package com.menu.app.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Items, Integer> {
    List<Items> findAllByCategory(String category);
}
