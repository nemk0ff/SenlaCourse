package dao;

import java.util.Optional;
import model.Item;
import org.hibernate.Session;

public interface GenericDao<T extends Item> {
  void update(Session session, T entity);

  Optional<T> find(Session session, Long id);
}
