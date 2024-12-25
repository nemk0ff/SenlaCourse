package src.main;

import java.util.ArrayList;
import java.util.List;

public class FoodWarehouse {
    private Integer capacity = 1000;
    private Integer fullness = 0;

    private List<Product> products = new ArrayList<>();

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