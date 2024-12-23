public class Motherboard implements IProductPart{
    private String model = "Asus prime";

    Motherboard(){
        System.out.println("Motherboard is built");
    }

    Motherboard(String model){
        this.model = model;
        System.out.println("Motherboard is built");
    }
}