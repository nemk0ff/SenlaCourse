public class Main {
    public static void main(String[] args) {
        IAssemblyLine myLine = new AssemblyLineLaptops();

        IProduct myLaptop = myLine.assembleProduct(new Laptop());


    }
}