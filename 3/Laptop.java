public class Laptop implements IProduct{
    private IProductPart Package;
    private IProductPart Motherboard;
    private IProductPart Monitor;

    Laptop(){
        System.out.println("Laptop is created!");
    }

    @Override
    public void installFirstPart(IProductPart productPart) {
        Package = productPart;
        System.out.println("Package is installed!");
    }

    @Override
    public void installSecondPart(IProductPart productPart) {
        Motherboard = productPart;
        System.out.println("Motherboard is installed!");
    }

    @Override
    public void installThirdPart(IProductPart productPart) {
        Monitor = productPart;
        System.out.println("Monitor is installed!");
    }
}
