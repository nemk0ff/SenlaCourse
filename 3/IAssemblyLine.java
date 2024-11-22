public interface IAssemblyLine {
    IProduct assembleProduct(IProduct product);
}

class AssemblyLineLaptops implements  IAssemblyLine{
    AssemblyLineLaptops(){
        System.out.println("AssemblyLineLaptops is created!");
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        ILineStep firstStep = new StepPackage();
        IProductPart firstPart = firstStep.buildProductPart();

        ILineStep secondStep = new StepMotherboard();
        IProductPart secondPart = secondStep.buildProductPart();

        ILineStep thirdStep = new StepMonitor();
        IProductPart thirdPart = thirdStep.buildProductPart();

        product.installFirstPart(firstPart);
        product.installSecondPart(secondPart);
        product.installThirdPart(thirdPart);

        System.out.println("AssembleProduct is finished");
        return product;
    }
}