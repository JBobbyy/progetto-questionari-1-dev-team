package it.unimib.unimibmodules.repository;

import it.unimib.unimibmodules.model.Category;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO for the Category class.
 * @author Lorenzo Occhipinti
 * @version 0.2.0
 */
public interface CategoryDAO extends CrudRepository<Category, Integer> {

}
