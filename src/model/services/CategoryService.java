package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.CategoryDao;
import model.entities.Category;

public class CategoryService {
	
	private CategoryDao dao = DaoFactory.createCategoryDao();
	
	public List<Category> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Category obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}
	
	public void remove(Category obj) {
		dao.deleteById(obj.getId());
	}

}
