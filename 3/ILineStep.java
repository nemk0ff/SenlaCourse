public interface ILineStep {
    IProductPart buildProductPart();
}

class StepMonitor implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        return new Monitor();
    }
}

class StepMotherboard implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        return new Motherboard();
    }
}

class StepPackage implements  ILineStep{
    @Override
    public IProductPart buildProductPart() {
        return new Package();
    }
}
