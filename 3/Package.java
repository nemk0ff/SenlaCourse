public class Package implements IProductPart{
    private String color = "Black";

    Package(String color){
        this.color = color;
        System.out.println("Package is built");
    }

    Package(){
        System.out.println("Package is built");
    }
}