public class AssemblyLineLaptops implements  IAssemblyLine{
    private ILineStep firstStep;
    private ILineStep secondStep;
    private ILineStep thirdStep;

    AssemblyLineLaptops(ILineStep firstStep, ILineStep secondStep, ILineStep thirdStep){
        this.firstStep = firstStep;
        this.secondStep = secondStep;
        this.thirdStep = thirdStep;
        System.out.println("AssemblyLineLaptops is created!");
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        IProductPart firstPart = firstStep.buildProductPart();

        IProductPart secondPart = secondStep.buildProductPart();

        IProductPart thirdPart = thirdStep.buildProductPart();

        product.installFirstPart(firstPart);
        product.installSecondPart(secondPart);
        product.installThirdPart(thirdPart);

        System.out.println("AssembleProduct is finished");
        return product;
    }
}