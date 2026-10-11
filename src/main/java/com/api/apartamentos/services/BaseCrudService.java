package com.api.apartamentos.services;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseCrudService<T> {
    protected final JpaRepository<T, Long> repository;
    protected BaseCrudService(JpaRepository<T, Long> repository) { this.repository = repository; }
    public List<T> findAll() { return repository.findAll(); }
    public Optional<T> findById(Long id) { return repository.findById(id); }
    public T save(T entity) { return repository.save(entity); }
    public void deleteById(Long id) { repository.deleteById(id); }
}
