package main.repo;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

public class InMemoryRepository<T, ID> implements Repository<T, ID>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<ID, T> data = new HashMap<>();
    private final SerializableFunction<T, ID> idExtractor;

    public InMemoryRepository(SerializableFunction<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public void save(T entity) {
        data.put(idExtractor.apply(entity), entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(ID id) {
        data.remove(id);
    }
}

