package model.dao;

import model.entities.Category;
import model.entities.ElectricVehicle;

import java.util.List;

public interface ElectricVehicleDao {

    void insert(ElectricVehicle ev);
    void update(ElectricVehicle ev);
    void deleteById(Integer id);
    ElectricVehicle findById(Integer id);
    List<ElectricVehicle> findAll();
    List<ElectricVehicle> findByCategory(Category category);
}
