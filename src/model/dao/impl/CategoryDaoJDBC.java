package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.CategoryDao;
import model.entities.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoJDBC implements CategoryDao {

    private Connection conn;

    public CategoryDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Category cat) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(
                    "INSERT INTO Category " +
                            "(Name) " +
                            "VALUE " +
                            "(?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, cat.getName());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    cat.setId(id);
                }
                DB.closeResultSet(rs);
            } else{
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Category cat) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "UPDATE Category " +
                            "SET Name = ?" +
                            "WHERE Id = ?"
            );

            st.setString(1, cat.getName());
            st.setInt(2, cat.getId());

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
                    "DELETE FROM Category WHERE Id = ?"
            );

            st.setInt(1, id);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Category findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT * FROM Category " +
                            "WHERE Id = ?"
            );

            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next()){
                Category cat = new Category();
                cat.setId(rs.getInt("Id"));
                cat.setName(rs.getString("Name"));
                return cat;
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
    public List<Category> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT * FROM Category ORDER BY Name"
            );

            rs = st.executeQuery();

            List<Category> list = new ArrayList<>();
            while(rs.next()){
                Category cat = new Category();
                cat.setId(rs.getInt("Id"));
                cat.setName(rs.getString("Name"));
                list.add(cat);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }
}
