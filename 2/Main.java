import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        FoodWarehouse foodWarehouse = new FoodWarehouse(1000);
        foodWarehouse.addProduct(new Milk(100));
        foodWarehouse.addProduct(new Apple(450));
        foodWarehouse.addProduct(new Banana(450));
        System.out.println(foodWarehouse.getFullness());
    }


    public static abstract class Product{
        public Integer weight = 0;
        Product(Integer weight) { this.weight = weight; }
    }

    public static abstract class MilkyProduct extends Product{
        MilkyProduct(Integer weight){ super(weight); }
    }

    public static class Milk extends MilkyProduct{
        Milk(Integer weight){ super(weight); }
    }

    public static abstract class Fruit extends Product{
        Fruit(Integer weight){ super(weight); }
    }

    public static class Apple extends Fruit{
        Apple(Integer weight){ super(weight); }
    }

    public static class Banana extends Fruit{
        Banana(Integer weight){ super(weight); }
    }


    public static class FoodWarehouse {
        private Integer capacity = 1000;
        private Integer fullness = 0;

        private Vector<Product> products = new Vector<>();

        public FoodWarehouse(Integer capacity) {
            this.capacity = capacity;
        }

        public void addProduct(Product product) throws RuntimeException {
            if(fullness + product.weight <= capacity){
                products.add(product);
                fullness += product.weight;
            }
            else{
                throw new RuntimeException("Не хватает места на складе");
            }
        }

        public Integer getFullness() {
            return fullness;
        }
    }
}