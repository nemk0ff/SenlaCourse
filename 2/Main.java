public class Main {
    public static void main(String[] args) {
        FoodWarehouse foodWarehouse = new FoodWarehouse(1000);
        foodWarehouse.addProduct(new Milk(100));
        foodWarehouse.addProduct(new Apple(450));
        foodWarehouse.addProduct(new Banana(450));
        System.out.println(foodWarehouse.getFullness());
    }
}