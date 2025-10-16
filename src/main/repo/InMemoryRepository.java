package main.repo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InMemoryRepository<T,ID> implements Repository<T,ID> {
    private final Map<ID, T> store = new ConcurrentHashMap<>();
    private final Function<T, ID> idExtractor;
    public InMemoryRepository(Function<T,ID> idExtractor){ this.idExtractor=idExtractor; }

    @Override public T save(T entity){ store.put(idExtractor.apply(entity), entity); return entity; }
    @Override public Optional<T> findById(ID id){ return Optional.ofNullable(store.get(id)); }
    @Override public List<T> findAll(){ return new ArrayList<>(store.values()); }
    @Override public void deleteById(ID id){ store.remove(id); }
}
