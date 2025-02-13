package model;

/**
 * {@code RequestStatus} - Перечисление, определяющее возможные статусы запроса на книгу.
 * Возможные значения:
 * <ul>
 *   <li>{@link #OPEN} - Запрос создан и находится в ожидании обработки.</li>
 *   <li>{@link #CLOSED} - Запрос обработан и закрыт.</li>
 * </ul>
 */
public enum RequestStatus {
  OPEN, CLOSED
}
