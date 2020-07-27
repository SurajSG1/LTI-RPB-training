package com.movie.service.application.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.movie.service.application.document.MovieMultiplexDetails;

@Repository
public interface MovieMultiplexRepository extends CrudRepository<MovieMultiplexDetails, String> {

	List<MovieMultiplexDetails> findByUserId(String userId);

	List<MovieMultiplexDetails> findAllByMovieId(String id);

	List<MovieMultiplexDetails> findAllByMultiplexId(String multiplexId);

	List<MovieMultiplexDetails> findAllByMultiplexIdAndScreenName(String multiplexId, String screenName);

	void deleteAllByMovieId(String movieId);

	Optional<MovieMultiplexDetails> findByMultiplexId(String multiplexId);

	void deleteAllByMultiplexId(String multiplexId);

}
