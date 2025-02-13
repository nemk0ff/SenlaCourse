package model;

/**
 * {@code BookStatus} - Перечисление, определяющее возможные статусы книги в магазине.
 * Возможные значения:
 * <ul>
 *   <li>{@link #AVAILABLE} - Книга есть в наличии и доступна для продажи.</li>
 *   <li>{@link #NOT_AVAILABLE} - Книга отсутствует в наличии.</li>
 * </ul>
 */
public enum BookStatus {
  AVAILABLE,
  NOT_AVAILABLE
}
