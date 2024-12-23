public class StepMonitor implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        return new Monitor();
    }
}
