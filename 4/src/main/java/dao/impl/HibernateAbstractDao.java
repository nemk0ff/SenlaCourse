package dao.impl;

import dao.GenericDao;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Item;
import org.hibernate.Session;

@Slf4j
@NoArgsConstructor
public abstract class HibernateAbstractDao<T extends Item> implements GenericDao<T> {
  private Class<T> type;

  HibernateAbstractDao(Class<T> type) {
    this.type = type;
  }

  @Override
  public void update(Session session, T entity) {
    log.debug("Перезаписываем информацию о сущности: {}", entity.getInfoAbout());
    session.update(entity);
    log.debug("Информация о сущности успешно перезаписана: {}", entity.getId());
  }

  @Override
  public Optional<T> find(Session session, Long id) {
    log.debug("Поиск сущности {} [{}]...", type.getName(), id);
    try {
      Optional<T> entity = Optional.ofNullable(session.get(type, id));
      if (entity.isEmpty()) {
        log.debug("Сущность {} [{}] не найдена.", type.getName(), id);
      } else {
        log.debug("Сущность {} [{}] найдена: {}", type.getName(), id, entity.get().getInfoAbout());
      }
      return entity;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при поиске сущности типа "
          + type.getName() + " с id " + id + ": " + e.getMessage(), e);
    }
  }
}
