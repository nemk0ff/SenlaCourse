public interface IProductPart {
}

class Monitor implements IProductPart{
    Monitor(){
        System.out.println("Monitor is built");
    }
}

class Package implements IProductPart{
    Package(){
        System.out.println("Package is built");
    }
}

class Motherboard implements IProductPart{
    Motherboard(){
        System.out.println("Motherboard is built");
    }
}

