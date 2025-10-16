package main.repo;

import java.util.*;
public interface Repository<T,ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
