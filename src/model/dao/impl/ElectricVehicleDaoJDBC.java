package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.ElectricVehicleDao;
import model.entities.Category;
import model.entities.ElectricVehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectricVehicleDaoJDBC implements ElectricVehicleDao {

    private Connection conn;

    public ElectricVehicleDaoJDBC(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(ElectricVehicle ev) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "INSERT INTO Vehicle (Type_Id, Category_Id, Model, Brand, Manufacturing_Year, Engine_Horsepower) VALUE " +
                            "(?, ?, ?, ?, ?, ?) ",
                    Statement.RETURN_GENERATED_KEYS
            );

            st.setInt(1, ev.getTypeId());
            st.setInt(2, ev.getCategory().getId());
            st.setString(3, ev.getModel());
            st.setString(4, ev.getBrand());
            st.setString(5, ev.getManufacturingYear());
            st.setString(6, ev.getEngineHorsePower());

            int rowsAffected = st.executeUpdate();

            if(rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    ev.setId(id);
                }
                DB.closeResultSet(rs);
            } else{
                throw new DbException("Unexpected error! No rows affected!");
            }

            st = conn.prepareStatement(
                    "INSERT INTO ElectricVehicle (Id, Battery_Capacity, Number_of_Batteries) VALUE " +
                            "(?, ?, ?)"
            );

            st.setInt(1, ev.getId());
            st.setString(2, ev.getBatteryCapacity());
            st.setInt(3, ev.getNumberOfBatteries());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(ElectricVehicle ev) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "UPDATE Vehicle " +
                            "SET Model = ?, Brand = ?, Manufacturing_Year = ?, Engine_Horsepower = ?, Category_Id = ? " +
                            "WHERE Id = ?"
            );

            st.setString(1, ev.getModel());
            st.setString(2, ev.getBrand());
            st.setString(3, ev.getManufacturingYear());
            st.setString(4, ev.getEngineHorsePower());
            st.setInt(5, ev.getCategory().getId());
            st.setInt(6, ev.getId());

            st.executeUpdate();

            st = conn.prepareStatement(
                    "UPDATE ElectricVehicle " +
                            "SET Battery_Capacity = ?, Number_of_Batteries = ? " +
                            "WHERE Id = ?"
            );

            st.setString(1, ev.getBatteryCapacity());
            st.setInt(2, ev.getNumberOfBatteries());
            st.setInt(3, ev.getId());

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
    public ElectricVehicle findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT V.Id, V.Type_Id, V.Category_Id, C.Name AS Category_Name, V.Model, V.Brand, V.Manufacturing_Year, V.Engine_Horsepower, " +
                            "EV.Battery_Capacity, EV.Number_of_Batteries " +
                            "FROM ElectricVehicle EV INNER JOIN Vehicle V " +
                            "ON EV.Id = V.Id INNER JOIN Category C " +
                            "ON V.Category_Id = C.Id " +
                            "WHERE V.Id = ?"
            );

            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()){
                Category cat = instantiateCategory(rs);
                ElectricVehicle ev = instantiateElectricVehicle(rs, cat);
                return ev;
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
    public List<ElectricVehicle> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT V.Id, V.Type_Id, V.Category_Id, C.Name AS Category_Name, V.Model, V.Brand, V.Manufacturing_Year, V.Engine_Horsepower, " +
                            "EV.Battery_Capacity, EV.Number_of_Batteries " +
                            "FROM ElectricVehicle EV INNER JOIN Vehicle V " +
                            "ON EV.Id = V.Id INNER JOIN Category C " +
                            "ON V.Category_Id = C.Id " +
                            "ORDER BY Model"
            );

            rs = st.executeQuery();

            List<ElectricVehicle> list = new ArrayList<>();
            Map<Integer, Category> map = new HashMap<>();

            while (rs.next()){
                Category cat = map.get(rs.getInt("Category_Id"));
                if (cat == null){
                    cat = instantiateCategory(rs);
                    map.put(rs.getInt("Category_Id"), cat);
                }

                ElectricVehicle ev = instantiateElectricVehicle(rs, cat);
                list.add(ev);
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
    public List<ElectricVehicle> findByCategory(Category category) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT V.Id, V.Type_Id, V.Category_Id, C.Name AS Category_Name, V.Model, V.Brand, V.Manufacturing_Year, V.Engine_Horsepower, " +
                            "EV.Battery_Capacity, EV.Number_of_Batteries " +
                            "FROM ElectricVehicle EV INNER JOIN Vehicle V " +
                            "ON EV.Id = V.Id INNER JOIN Category C " +
                            "ON V.Category_Id = C.Id " +
                            "WHERE Category_Id = ? " +
                            "ORDER BY Model"
            );

            st.setInt(1, category.getId());

            rs = st.executeQuery();

            List<ElectricVehicle> list = new ArrayList<>();
            Map<Integer, Category> map = new HashMap<>();

            while (rs.next()) {
                Category cat = map.get(rs.getInt("Category_Id"));
                if (cat == null) {
                    cat = instantiateCategory(rs);
                    map.put(rs.getInt("Category_Id"), cat);
                }

                ElectricVehicle ev = instantiateElectricVehicle(rs, cat);
                list.add(ev);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private ElectricVehicle instantiateElectricVehicle(ResultSet rs, Category cat) throws SQLException {
        ElectricVehicle ev = new ElectricVehicle();
        ev.setId(rs.getInt("Id"));
        ev.setTypeId(rs.getInt("Type_Id"));
        ev.setModel(rs.getString("Model"));
        ev.setBrand(rs.getString("Brand"));
        ev.setManufacturingYear(rs.getString("Manufacturing_Year"));
        ev.setEngineHorsePower(rs.getString("Engine_Horsepower"));
        ev.setBatteryCapacity(rs.getString("Battery_Capacity"));
        ev.setNumberOfBatteries(rs.getInt("Number_of_Batteries"));
        ev.setCategory(cat);
        return ev;
    }

    private Category instantiateCategory(ResultSet rs) throws SQLException {
        Category cat = new Category();
        cat.setId(rs.getInt("Category_Id"));
        cat.setName(rs.getString("Category_Name"));
        return cat;
    }
}
