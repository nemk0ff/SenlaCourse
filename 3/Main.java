public class Main {
    public static void main(String[] args) {
        ILineStep firstStep = new StepPackage();
        ILineStep secondStep = new StepMotherboard();
        ILineStep thirdStep = new StepMonitor();

        IAssemblyLine myLine = new AssemblyLineLaptops(firstStep, secondStep, thirdStep);

        IProduct myLaptop = myLine.assembleProduct(new Laptop());

    }
}