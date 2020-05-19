package com.karankumar.bookproject.backend.repository;

import com.karankumar.bookproject.backend.model.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
}
