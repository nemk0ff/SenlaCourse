public class StepPackage implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        return new Package();
    }
}