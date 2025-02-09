package model;

/**
 * {@code OrderStatus} - Перечисление, определяющее возможные статусы заказа.
 * Возможные значения:
 * <ul>
 *   <li>{@link #NEW} - Заказ создан, но еще не обработан.</li>
 *   <li>{@link #COMPLETED} - Заказ выполнен и завершен.</li>
 *   <li>{@link #CANCELED} - Заказ отменен.</li>
 * </ul>
 */
public enum OrderStatus {
  NEW, COMPLETED, CANCELED
}
