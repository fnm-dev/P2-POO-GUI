package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.GasolineVehicleDao;
import model.entities.GasolineVehicle;

public class GasolineVehicleService {
	
	private GasolineVehicleDao dao = DaoFactory.createGasolineVehicleDao();
	
	public List<GasolineVehicle> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(GasolineVehicle obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}
	
	public void remove(GasolineVehicle obj) {
		dao.deleteById(obj.getId());
	}

}
