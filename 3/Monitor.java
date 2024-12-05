public class Monitor implements IProductPart{
    private int diagonal = 17;

    Monitor(){
        System.out.println("Monitor is built");
    }

    Monitor(int diagonal){
        this.diagonal = diagonal;
        System.out.println("Monitor is built");
    }
}