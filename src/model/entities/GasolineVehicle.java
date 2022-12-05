package model.entities;

public class GasolineVehicle extends Vehicle{

    private String fuelTankCapacity;
    private Integer numberOfEngineCylinders;

    public GasolineVehicle(Integer id, Integer typeId, String model, String brand, String manufacturingYear,
                           String engineHorsePower, Category category, String fuelTankCapacity,
                           Integer numberOfEngineCylinders) {

        super(id, typeId, model, brand, manufacturingYear, engineHorsePower, category);
        this.fuelTankCapacity = fuelTankCapacity;
        this.numberOfEngineCylinders = numberOfEngineCylinders;
    }

    public GasolineVehicle() {
    }

    public String getFuelTankCapacity() {
        return fuelTankCapacity;
    }

    public void setFuelTankCapacity(String fuelTankCapacity) {
        this.fuelTankCapacity = fuelTankCapacity;
    }

    public Integer getNumberOfEngineCylinders() {
        return numberOfEngineCylinders;
    }

    public void setNumberOfEngineCylinders(Integer numberOfEngineCylinders) {
        this.numberOfEngineCylinders = numberOfEngineCylinders;
    }

    @Override
    public String toString() {
        return super.toString() + " fuelTankCapacity = " + fuelTankCapacity + " numberOfEngineCylinders = " + numberOfEngineCylinders + '}';
    }
}
