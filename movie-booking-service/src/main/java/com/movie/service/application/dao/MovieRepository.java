package com.movie.service.application.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.movie.service.application.document.MovieDetails;

@Repository
public interface MovieRepository extends CrudRepository<MovieDetails, String> {

	List<MovieDetails> findByMovieNameIgnoreCaseStartsWith(String searchString);

}
