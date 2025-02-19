package model;

/**
 * {@code Item} - Интерфейс, определяющий базовые методы для объектов Book, Order и Request,
 * которые могут быть идентифицированы уникальным ID и предоставлять информацию о себе.
 * Этот интерфейс предназначен для взаимодействия со всеми сущностями,
 * чтобы обеспечить единообразный способ получения информации и идентификации.
 */
public interface Item {
  String getInfoAbout();

  Long getId();
}
