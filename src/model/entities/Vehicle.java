package model.entities;

import java.util.Objects;

public abstract class Vehicle {

    private Integer id;
    private Integer typeId;
    private String model;
    private String brand;
    private String manufacturingYear;
    private String engineHorsePower;
    private Category category;

    public Vehicle() {
    }

    public Vehicle(Integer id, Integer typeId, String model, String brand, String manufacturingYear,
                   String engineHorsePower, Category category) {
        this.typeId = typeId;
        this.model = model;
        this.brand = brand;
        this.manufacturingYear = manufacturingYear;
        this.engineHorsePower = engineHorsePower;
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(String manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getEngineHorsePower() {
        return engineHorsePower;
    }

    public void setEngineHorsePower(String engineHorsePower) {
        this.engineHorsePower = engineHorsePower;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id.equals(vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", model='" + model + '\'' +
                ", brand='" + brand + '\'' +
                ", manufacturingYear='" + manufacturingYear + '\'' +
                ", engineHorsePower='" + engineHorsePower + '\'' +
                ", category=" + category;
    }
}
