package model.dao.impl;

import com.mysql.cj.protocol.Resultset;
import db.DB;
import db.DbException;
import model.dao.GasolineVehicleDao;
import model.entities.Category;
import model.entities.ElectricVehicle;
import model.entities.GasolineVehicle;
import model.entities.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GasolineVehicleDaoJDBC implements GasolineVehicleDao {

    private Connection conn;

    public GasolineVehicleDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(GasolineVehicle gv) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "INSERT INTO Vehicle (Type_Id, Category_Id, Model, Brand, Manufacturing_Year, Engine_Horsepower) VALUE " +
                            "(?, ?, ?, ?, ?, ?) ",
                    Statement.RETURN_GENERATED_KEYS
            );

            st.setInt(1, gv.getTypeId());
            st.setInt(2, gv.getCategory().getId());
            st.setString(3, gv.getModel());
            st.setString(4, gv.getBrand());
            st.setString(5, gv.getManufacturingYear());
            st.setString(6, gv.getEngineHorsePower());

            int rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    gv.setId(id);
                }
                DB.closeResultSet(rs);
            } else{
                throw new DbException("Unexpected error! No rows affected!");
            }

            st = conn.prepareStatement(
                    "INSERT INTO GasolineVehicle (Id, Fuel_Tank_Capacity, Number_of_Cylinders) VALUE " +
                            "(?, ?, ?)"
            );

            st.setInt(1, gv.getId());
            st.setString(2, gv.getFuelTankCapacity());
            st.setInt(3, gv.getNumberOfEngineCylinders());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(GasolineVehicle gv) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "UPDATE Vehicle " +
                            "SET Model = ?, Brand = ?, Manufacturing_Year = ?, Engine_Horsepower = ?, Category_Id = ? " +
                            "WHERE Id = ?"
            );

            st.setString(1, gv.getModel());
            st.setString(2, gv.getBrand());
            st.setString(3, gv.getManufacturingYear());
            st.setString(4, gv.getEngineHorsePower());
            st.setInt(5, gv.getCategory().getId());
            st.setInt(6, gv.getId());

            st.executeUpdate();

            st = conn.prepareStatement(
                    "UPDATE GasolineVehicle " +
                            "SET Fuel_Tank_Capacity = ?, Number_of_Cylinders = ? " +
                            "WHERE Id = ?"
            );

            st.setString(1, gv.getFuelTankCapacity());
            st.setInt(2, gv.getNumberOfEngineCylinders());
            st.setInt(3, gv.getId());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "DELETE FROM Vehicle WHERE Id = ?"
            );

            st.setInt(1, id);
            int rows = st.executeUpdate();
            if(rows == 0){
                throw new DbException("Id: "+ id +" doesn't exists!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public GasolineVehicle findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT V.Id, V.Type_Id, V.Category_Id, C.Name AS Category_Name, V.Model, V.Brand, V.Manufacturing_Year, V.Engine_Horsepower, " +
                            "GV.Fuel_Tank_Capacity, GV.Number_of_Cylinders " +
                            "FROM GasolineVehicle GV INNER JOIN Vehicle V " +
                            "ON GV.Id = V.Id INNER JOIN Category C " +
                            "ON V.Category_Id = C.Id " +
                            "WHERE V.Id = ?"
            );

            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()){
                Category cat = instantiateCategory(rs);
                GasolineVehicle gv = instantiateGasolineVehicle(rs, cat);
                return gv;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<GasolineVehicle> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT V.Id, V.Type_Id, V.Category_Id, C.Name AS Category_Name, V.Model, V.Brand, V.Manufacturing_Year, V.Engine_Horsepower, " +
                            "GV.Fuel_Tank_Capacity, GV.Number_of_Cylinders " +
                            "FROM GasolineVehicle GV INNER JOIN Vehicle V " +
                            "ON GV.Id = V.Id INNER JOIN Category C " +
                            "ON V.Category_Id = C.Id " +
                            "ORDER BY Model"
            );

            rs = st.executeQuery();

            List<GasolineVehicle> list = new ArrayList<>();
            Map<Integer, Category> map = new HashMap<>();

            while (rs.next()){
                Category cat = map.get(rs.getInt("Category_Id"));
                if (cat == null){
                    cat = instantiateCategory(rs);
                    map.put(rs.getInt("Category_Id"), cat);
                }

                GasolineVehicle gv = instantiateGasolineVehicle(rs, cat);
                list.add(gv);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<GasolineVehicle> findByCategory(Category category) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT V.Id, V.Type_Id, V.Category_Id, C.Name AS Category_Name, V.Model, V.Brand, V.Manufacturing_Year, V.Engine_Horsepower, " +
                            "GV.Fuel_Tank_Capacity, GV.Number_of_Cylinders " +
                            "FROM GasolineVehicle GV INNER JOIN Vehicle V " +
                            "ON GV.Id = V.Id INNER JOIN Category C " +
                            "ON V.Category_Id = C.Id " +
                            "WHERE Category_Id = ? " +
                            "ORDER BY Model"
            );

            st.setInt(1, category.getId());

            rs = st.executeQuery();

            List<GasolineVehicle> list = new ArrayList<>();
            Map<Integer, Category> map = new HashMap<>();

            while (rs.next()) {
                Category cat = map.get(rs.getInt("Category_Id"));
                if (cat == null) {
                    cat = instantiateCategory(rs);
                    map.put(rs.getInt("Category_Id"), cat);
                }

                GasolineVehicle gv = instantiateGasolineVehicle(rs, cat);
                list.add(gv);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private GasolineVehicle instantiateGasolineVehicle(ResultSet rs, Category cat) throws SQLException {
        GasolineVehicle gv = new GasolineVehicle();
        gv.setId(rs.getInt("Id"));
        gv.setTypeId(rs.getInt("Type_Id"));
        gv.setModel(rs.getString("Model"));
        gv.setBrand(rs.getString("Brand"));
        gv.setManufacturingYear(rs.getString("Manufacturing_Year"));
        gv.setEngineHorsePower(rs.getString("Engine_Horsepower"));
        gv.setFuelTankCapacity(rs.getString("Fuel_Tank_Capacity"));
        gv.setNumberOfEngineCylinders(rs.getInt("Number_of_Cylinders"));
        gv.setCategory(cat);
        return gv;
    }

    private Category instantiateCategory(ResultSet rs) throws SQLException {
        Category cat = new Category();
        cat.setId(rs.getInt("Category_Id"));
        cat.setName(rs.getString("Category_Name"));
        return cat;
    }
}
