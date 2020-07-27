package com.multiplex.service.application.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.multiplex.service.application.document.MultiplexDetails;

@Repository
public interface MultiplexAppRepository extends CrudRepository<MultiplexDetails, String> {

	List<MultiplexDetails> findByMultiplexNameIgnoreCaseStartsWith(String searchString);

}
