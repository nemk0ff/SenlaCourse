package ru.dao.impl;

import org.hibernate.SessionFactory;
import ru.dao.GenericDao;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.model.Item;

@Slf4j
@NoArgsConstructor
public abstract class HibernateAbstractDao<T extends Item> implements GenericDao<T> {
  private Class<T> type;
  protected SessionFactory sessionFactory;

  HibernateAbstractDao(Class<T> type) {
    this.type = type;
  }

  @Override
  public void update(T entity) {
    log.debug("Перезаписываем информацию о сущности: {}", entity.getInfoAbout());
    sessionFactory.getCurrentSession().merge(entity);
    log.debug("Информация о сущности успешно перезаписана: {}", entity.getId());
  }

  @Override
  public Optional<T> find(Long id) {
    log.debug("Поиск сущности {} [{}]...", type.getName(), id);
    try {
      Optional<T> entity = Optional.ofNullable(sessionFactory.getCurrentSession().get(type, id));
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
