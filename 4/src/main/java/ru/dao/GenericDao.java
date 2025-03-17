package ru.dao;

import java.util.Optional;
import ru.model.Item;

public interface GenericDao<T extends Item> {
  void update(T entity);

  Optional<T> find(Long id);
}
