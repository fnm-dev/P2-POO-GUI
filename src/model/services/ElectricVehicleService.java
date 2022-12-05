package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ElectricVehicleDao;
import model.entities.ElectricVehicle;

public class ElectricVehicleService {
	
	private ElectricVehicleDao dao = DaoFactory.createElectricVehicleDao();
	
	public List<ElectricVehicle> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(ElectricVehicle obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}
	
	public void remove(ElectricVehicle obj) {
		dao.deleteById(obj.getId());
	}

}
