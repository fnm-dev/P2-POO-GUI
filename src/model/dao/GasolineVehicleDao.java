package model.dao;

import model.entities.Category;
import model.entities.GasolineVehicle;
import model.entities.Vehicle;

import java.util.List;

public interface GasolineVehicleDao {

    void insert(GasolineVehicle gv);
    void update(GasolineVehicle gv);
    void deleteById(Integer id);
    GasolineVehicle findById(Integer id);
    List<GasolineVehicle> findAll();
    List<GasolineVehicle> findByCategory(Category category);
}
