package com.movie.service.application.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.movie.service.application.dao.MovieMultiplexRepository;
import com.movie.service.application.dao.MovieRepository;
import com.movie.service.application.document.MovieDetails;
import com.movie.service.application.document.MovieMultiplexDetails;
import com.movie.service.application.dto.MovieDetailsDto;
import com.movie.service.application.dto.MovieMultiplexDetailsDto;
import com.movie.service.application.dto.MovieMultiplexDto;
import com.movie.service.application.dto.MovieMultiplexSearchResultDetailsDto;
import com.movie.service.application.dto.MultiplexDetailsDto;
import com.movie.service.application.dto.MovieUserDetailDto;
import com.movie.service.application.exception.MovieCustomException;
import com.movie.service.application.feign.MovieMultiplexFeignProxy;
import com.movie.service.application.feign.MovieUserDetailFeignProxy;

@Service
public class MovieAppServiceImpl implements MovieAppService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private MovieRepository repository;
	private MovieMultiplexRepository movieMultiplexRepository;
	private MovieMultiplexFeignProxy multiplexFeignProxy;
	private MovieUserDetailFeignProxy userDetailFeignProxy;

	public MovieAppServiceImpl(MovieRepository repository, MovieMultiplexRepository movieMultiplexRepository,
			MovieMultiplexFeignProxy multiplexFeignProxy, MovieUserDetailFeignProxy userDetailFeignProxy) {
		this.repository = repository;
		this.movieMultiplexRepository = movieMultiplexRepository;
		this.multiplexFeignProxy = multiplexFeignProxy;
		this.userDetailFeignProxy = userDetailFeignProxy;
	}

	@Override
	public MovieDetailsDto addMovie(MovieDetailsDto movieDto, String userId) throws MovieCustomException {

		try {
			validateUser(userId);
			if (movieDto.getMovieName() == null || movieDto.getMovieName().isEmpty())
				throw new Exception("movie name can not be null or empty");

			// converting moviDto to Movie document
			MovieDetails movie = new MovieDetails(null, movieDto.getMovieName(), movieDto.getMovieCategory(),
					movieDto.getMovieProducer(), movieDto.getMovieDirector(), movieDto.getReleaseDate());

			// saving movie to movie_micro database
			movie = repository.save(movie);

			// again converting Movie document to movieDto
			movieDto = new MovieDetailsDto(movie.getId(), movie.getMovieName(), movie.getMovieCategory(),
					movie.getMovieProducer(), movie.getMovieDirector(), movie.getReleaseDate());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieDto;
	}

	@Override
	public List<MovieDetailsDto> getAllMovies() throws MovieCustomException {
		ArrayList<MovieDetailsDto> movieList;
		try {
			Iterator<MovieDetails> movies = repository.findAll().iterator();
			movieList = new ArrayList<MovieDetailsDto>();
			while (movies.hasNext()) {
				MovieDetails movie = movies.next();
				MovieDetailsDto movieDto = new MovieDetailsDto(movie.getId(), movie.getMovieName(), movie.getMovieCategory(),
						movie.getMovieProducer(), movie.getMovieDirector(), movie.getReleaseDate());
				movieList.add(movieDto);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieList;
	}

	@Override
	public MovieDetailsDto getMovieById(String movieId) throws MovieCustomException {
		MovieDetailsDto movieDto;
		try {
			Optional<MovieDetails> movieInDb = repository.findById(movieId);
			if (!movieInDb.isPresent())
				throw new Exception("Error during fetching movie details: " + movieId);

			MovieDetails movie = movieInDb.get();
			movieDto = new MovieDetailsDto(movie.getId(), movie.getMovieName(), movie.getMovieCategory(),
					movie.getMovieProducer(), movie.getMovieDirector(), movie.getReleaseDate());

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieDto;
	}

	@Override
	public MovieDetailsDto updateMovie(String movieId, MovieDetailsDto movieDto, String userId) throws MovieCustomException {
		try {
			validateUser(userId);
			Optional<MovieDetails> movieInDb = repository.findById(movieId);
			if (!movieInDb.isPresent())
				throw new Exception("Error while updating movie details, movieId: " + movieId);

			if (movieDto.getMovieName() == null || movieDto.getMovieName().isEmpty())
				throw new Exception("movie name can not be null or empty");

			MovieDetails movie = movieInDb.get();
			movie.setMovieName(movieDto.getMovieName());
			movie.setMovieCategory(movieDto.getMovieCategory());
			movie.setMovieDirector(movieDto.getMovieDirector());
			movie.setMovieProducer(movieDto.getMovieProducer());
			movie.setReleaseDate(movieDto.getReleaseDate());

			movie = repository.save(movie);

			movieDto = new MovieDetailsDto(movie.getId(), movie.getMovieName(), movie.getMovieCategory(),
					movie.getMovieProducer(), movie.getMovieDirector(), movie.getReleaseDate());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieDto;
	}

	@Override
	public boolean deleteMovieById(String movieId, String userId) throws MovieCustomException {
		try {
			validateUser(userId);
			Optional<MovieDetails> movieInDb = repository.findById(movieId);
			if (movieInDb.isPresent()) {
				repository.deleteById(movieId);
				movieMultiplexRepository.deleteAllByMovieId(movieId);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
	}

	@Override
	public MovieMultiplexDetailsDto addMovieToMultiplex(String userId, MovieMultiplexDto movieMultiplexDto)
			throws MovieCustomException {
		MovieMultiplexDetailsDto movieMultiplexDetailsDto;
		try {
			validateUser(userId);
			validateScreenNumber(movieMultiplexDto);

			// convert movieMultiplexDto to MovieMultiplex document
			if (movieMultiplexDto.getMovieId() != null && movieMultiplexDto.getMultiplexId() != null) {
				MovieMultiplexDetails movieMultiplex = new MovieMultiplexDetails(null, movieMultiplexDto.getMovieId(),
						movieMultiplexDto.getMultiplexId(), movieMultiplexDto.getScreenName(), userId);

				movieMultiplex = movieMultiplexRepository.save(movieMultiplex);

				movieMultiplexDetailsDto = getMovieMultiplexDetailsDto(movieMultiplex);
				return movieMultiplexDetailsDto;
			} else {
				throw new MovieCustomException("movie Name or Multiplex Name can not be null");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
	}

	@Override
	public List<MovieMultiplexDetailsDto> getAllAllotedMovieMultiplex() throws MovieCustomException {

		List<MovieMultiplexDetailsDto> movieAndMultiplexList = new ArrayList<MovieMultiplexDetailsDto>();
		try {
			Iterator<MovieMultiplexDetails> moviesAndMultiplexes = this.movieMultiplexRepository.findAll().iterator();
			while (moviesAndMultiplexes.hasNext()) {
				MovieMultiplexDetails movieMultiplex = moviesAndMultiplexes.next();
				MovieMultiplexDetailsDto movieMultiplexDetailsDto = getMovieMultiplexDetailsDto(movieMultiplex);
				movieAndMultiplexList.add(movieMultiplexDetailsDto);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieAndMultiplexList;
	}

	@Override
	public List<MovieMultiplexDetailsDto> getAllotedMovieMultiplexByUserId(String userId) throws MovieCustomException {

		List<MovieMultiplexDetailsDto> movieAndMultiplexList = new ArrayList<MovieMultiplexDetailsDto>();
		try {
			validateUser(userId);
			List<MovieMultiplexDetails> moviesAndMultiplexes = this.movieMultiplexRepository.findByUserId(userId);
			for (MovieMultiplexDetails movieMultiplex : moviesAndMultiplexes) {
				MovieMultiplexDetailsDto movieMultiplexDetailsDto = getMovieMultiplexDetailsDto(movieMultiplex);
				movieAndMultiplexList.add(movieMultiplexDetailsDto);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieAndMultiplexList;
	}

	private MovieMultiplexDetailsDto getMovieMultiplexDetailsDto(MovieMultiplexDetails movieMultiplex) {

		// getting movieDto by movieId saved to MovieMultiplex collection
		MovieDetailsDto movieDto = this.getMovieById(movieMultiplex.getMovieId());

		// calling multiplex-microservice api through feignClient proxy interface
		MultiplexDetailsDto multiplexDto = this.multiplexFeignProxy.getMultiplexById(movieMultiplex.getMultiplexId())
				.getBody();

		// calling user-microservice api through feignClient proxy interface
		MovieUserDetailDto userDetailDto = this.userDetailFeignProxy.getUserDetails(movieMultiplex.getUserId()).getBody();

		// convert movieMultiplex document to movieMultiplexDetailDto
		return new MovieMultiplexDetailsDto(movieMultiplex.getId(), movieDto, multiplexDto,
				movieMultiplex.getScreenName(), userDetailDto);
	}

	private void validateUser(String userId) throws Exception {
		// validation for user
		MovieUserDetailDto userDetailDto = this.userDetailFeignProxy.getUserDetails(userId).getBody();
		if (!userDetailDto.getRole().equals("ADMIN"))
			throw new Exception("To Perform this operation you need ADMIN privilege");
	}

	@Override
	public List<MovieMultiplexSearchResultDetailsDto> searchMovies(String searchString) throws MovieCustomException {
		List<MovieMultiplexSearchResultDetailsDto> movieMultiplexList = new ArrayList<MovieMultiplexSearchResultDetailsDto>();
		try {
			List<MovieDetailsDto> movies = getMoviesBySearchString(searchString);

			for (MovieDetailsDto movie : movies) {
				List<MovieMultiplexDetails> movieMultiplexListInDb = movieMultiplexRepository.findAllByMovieId(movie.getId());
				for (MovieMultiplexDetails movieMultiplex : movieMultiplexListInDb) {
					MultiplexDetailsDto multiplex = this.multiplexFeignProxy.getMultiplexById(movieMultiplex.getMultiplexId())
							.getBody();
					movieMultiplexList.add(new MovieMultiplexSearchResultDetailsDto(movie.getMovieName(),
							multiplex.getMultiplexName(), multiplex.getAddress(), movieMultiplex.getScreenName()));
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieMultiplexList;
	}

	private List<MovieDetailsDto> getMoviesBySearchString(String searchString) throws Exception {
		if (searchString == null || searchString.isEmpty())
			throw new Exception("search string can not be null or empty");

		List<MovieDetails> movies = repository.findByMovieNameIgnoreCaseStartsWith(searchString);
		List<MovieDetailsDto> movieList = new ArrayList<MovieDetailsDto>();

		for (MovieDetails movie : movies) {
			movieList.add(new MovieDetailsDto(movie.getId(), movie.getMovieName(), movie.getMovieCategory(),
					movie.getMovieProducer(), movie.getMovieDirector(), movie.getReleaseDate()));
		}
		return movieList;
	}

	@Override
	public List<MovieMultiplexSearchResultDetailsDto> getAllotedMovieMultiplexByMultiplexId(String multiplexId)
			throws MovieCustomException {
		List<MovieMultiplexSearchResultDetailsDto> movieMultiplexList = new ArrayList<MovieMultiplexSearchResultDetailsDto>();
		try {
			List<MovieMultiplexDetails> movieMultiplexes = movieMultiplexRepository.findAllByMultiplexId(multiplexId);
			for (MovieMultiplexDetails movieMultiplex : movieMultiplexes) {
				MovieDetailsDto movie = this.getMovieById(movieMultiplex.getMovieId());
				MultiplexDetailsDto multiplex = this.multiplexFeignProxy.getMultiplexById(movieMultiplex.getMultiplexId())
						.getBody();
				movieMultiplexList.add(new MovieMultiplexSearchResultDetailsDto(movie.getMovieName(),
						multiplex.getMultiplexName(), multiplex.getAddress(), movieMultiplex.getScreenName()));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieMultiplexList;
	}

	@Override
	public boolean deleteAllottedRecordsByMultiplexId(String multiplexId, String userId) throws MovieCustomException {
		try {
			validateUser(userId);
			List<MovieMultiplexDetails> movieMultiplexList = movieMultiplexRepository.findAllByMultiplexId(multiplexId);
			if (movieMultiplexList.size() > 0) {
				movieMultiplexRepository.deleteAllByMultiplexId(multiplexId);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
	}

	@Override
	public boolean deleteAllottedRecordById(String id, String userId) throws MovieCustomException {
		try {
			validateUser(userId);
			Optional<MovieMultiplexDetails> recordInDb = movieMultiplexRepository.findById(id);
			if (recordInDb.isPresent()) {
				movieMultiplexRepository.deleteById(id);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
	}

	@Override
	public MovieMultiplexDetailsDto updateMovieMultiplex(String id, MovieMultiplexDto movieMultiplexDto, String userId)
			throws MovieCustomException {

		MovieMultiplexDetailsDto movieMultiplexDetailsDto;
		try {
			validateUser(userId);

			// validation for screen allotment when updating
			List<MovieMultiplexDetails> movieMultiplexList = movieMultiplexRepository.findAllByMultiplexIdAndScreenName(
					movieMultiplexDto.getMultiplexId(), movieMultiplexDto.getScreenName());

			Optional<MovieMultiplexDetails> currentDataInDb = movieMultiplexRepository.findById(id);
			MovieMultiplexDetails currentData = currentDataInDb.get();

			for (MovieMultiplexDetails movieMultiplexInDb : movieMultiplexList) {
				MultiplexDetailsDto multiplex = this.multiplexFeignProxy.getMultiplexById(movieMultiplexInDb.getMultiplexId())
						.getBody();
				MovieDetailsDto movie = this.getMovieById(movieMultiplexInDb.getMovieId());
				if (movieMultiplexInDb.getMultiplexId().equals(movieMultiplexDto.getMultiplexId())
						&& movieMultiplexInDb.getScreenName().equals(movieMultiplexDto.getScreenName())) {

					// checking with current record
					if (!currentData.getMovieId().equals(movieMultiplexInDb.getMovieId())
							|| !currentData.getMultiplexId().equals(movieMultiplexInDb.getMultiplexId())
							|| !currentData.getScreenName().equals(movieMultiplexInDb.getScreenName())) {
						throw new Exception(movie.getMovieName().toUpperCase() + " is already alloted to "
								+ movieMultiplexDto.getScreenName() + " of "
								+ multiplex.getMultiplexName().toUpperCase() + ", " + multiplex.getAddress());
					}
				}
			}
			// convert movieMultiplexDto to MovieMultiplex document
			if (movieMultiplexDto.getMovieId() != null && movieMultiplexDto.getMultiplexId() != null) {
				MovieMultiplexDetails movieMultiplex = new MovieMultiplexDetails(id, movieMultiplexDto.getMovieId(),
						movieMultiplexDto.getMultiplexId(), movieMultiplexDto.getScreenName(), userId);
				movieMultiplex = movieMultiplexRepository.save(movieMultiplex);

				movieMultiplexDetailsDto = getMovieMultiplexDetailsDto(movieMultiplex);
				return movieMultiplexDetailsDto;
			} else {
				throw new MovieCustomException("movie Name or Multiplex Name can not be null");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
	}

	private void validateScreenNumber(MovieMultiplexDto movieMultiplexDto) throws Exception {

		// validation for screen allotment when adding
		List<MovieMultiplexDetails> movieMultiplexList = movieMultiplexRepository.findAllByMultiplexIdAndScreenName(
				movieMultiplexDto.getMultiplexId(), movieMultiplexDto.getScreenName());

		for (MovieMultiplexDetails movieMultiplexInDb : movieMultiplexList) {
			MultiplexDetailsDto multiplex = this.multiplexFeignProxy.getMultiplexById(movieMultiplexInDb.getMultiplexId())
					.getBody();
			MovieDetailsDto movie = this.getMovieById(movieMultiplexInDb.getMovieId());
			if (movieMultiplexInDb.getMultiplexId().equals(movieMultiplexDto.getMultiplexId())
					&& movieMultiplexInDb.getScreenName().equals(movieMultiplexDto.getScreenName())) {
				throw new Exception(movie.getMovieName().toUpperCase() + " is already alloted to "
						+ movieMultiplexDto.getScreenName() + " of " + multiplex.getMultiplexName().toUpperCase() + ", "
						+ multiplex.getAddress());
			}
		}
	}

	@Override
	public MovieMultiplexDetailsDto getMovieMultiplexById(String id) {
		MovieMultiplexDetailsDto movieMultiplexDetailsDto;
		try {
			Optional<MovieMultiplexDetails> movieMultiplexInDb = movieMultiplexRepository.findById(id);
			if (!movieMultiplexInDb.isPresent())
				throw new Exception("Error during fetching movie multiplex details: " + id);

			MovieMultiplexDetails movieMultiplex = movieMultiplexInDb.get();
			movieMultiplexDetailsDto = getMovieMultiplexDetailsDto(movieMultiplex);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MovieCustomException(e.getMessage());
		}
		return movieMultiplexDetailsDto;
	}

}
