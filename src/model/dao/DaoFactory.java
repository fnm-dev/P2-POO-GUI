package model.dao;

import db.DB;
import model.dao.impl.CategoryDaoJDBC;
import model.dao.impl.ElectricVehicleDaoJDBC;
import model.dao.impl.GasolineVehicleDaoJDBC;

public class DaoFactory {

    public static ElectricVehicleDao createElectricVehicleDao(){
        return new ElectricVehicleDaoJDBC(DB.getConnection());
    }

    public static GasolineVehicleDao createGasolineVehicleDao(){
        return new GasolineVehicleDaoJDBC(DB.getConnection());
    }

    public static CategoryDao createCategoryDao(){
        return new CategoryDaoJDBC(DB.getConnection());
    }
}
