public class StepMotherboard implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        return new Motherboard();
    }
}