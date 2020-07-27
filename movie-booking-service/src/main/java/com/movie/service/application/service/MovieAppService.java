package com.movie.service.application.service;

import java.util.List;

import com.movie.service.application.dto.MovieDetailsDto;
import com.movie.service.application.dto.MovieMultiplexDetailsDto;
import com.movie.service.application.dto.MovieMultiplexDto;
import com.movie.service.application.dto.MovieMultiplexSearchResultDetailsDto;

public interface MovieAppService {

	List<MovieDetailsDto> getAllMovies();

	MovieDetailsDto addMovie(MovieDetailsDto movieDto, String userId);

	MovieDetailsDto getMovieById(String movieId);

	MovieDetailsDto updateMovie(String movieId, MovieDetailsDto movieDto, String userId);

	boolean deleteMovieById(String movieId, String userId);

	MovieMultiplexDetailsDto addMovieToMultiplex(String userId, MovieMultiplexDto movieMultiplexDto);

	List<MovieMultiplexDetailsDto> getAllAllotedMovieMultiplex();

	List<MovieMultiplexDetailsDto> getAllotedMovieMultiplexByUserId(String userId);

	List<MovieMultiplexSearchResultDetailsDto> searchMovies(String searchString);

	List<MovieMultiplexSearchResultDetailsDto> getAllotedMovieMultiplexByMultiplexId(String multiplexId);

	boolean deleteAllottedRecordsByMultiplexId(String multiplexId, String userId);

	boolean deleteAllottedRecordById(String id, String userId);

	MovieMultiplexDetailsDto updateMovieMultiplex(String id, MovieMultiplexDto movieMultiplexDto, String userId);

	MovieMultiplexDetailsDto getMovieMultiplexById(String id);

}
