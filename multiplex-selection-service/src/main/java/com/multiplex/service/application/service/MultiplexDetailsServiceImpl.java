package com.multiplex.service.application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.multiplex.service.application.dao.MultiplexAppRepository;
import com.multiplex.service.application.document.MultiplexDetails;
import com.multiplex.service.application.dto.MovieMultiplexSearchResultDto;
import com.multiplex.service.application.dto.MultiplexDetailsDto;
import com.multiplex.service.application.dto.MultiplexUserDetailDto;
import com.multiplex.service.application.exception.MultiplexCustomException;
import com.multiplex.service.application.feign.MultiplexMovieFeignProxy;
import com.multiplex.service.application.feign.MultiplexUserDetailFeignProxy;

@Service
public class MultiplexDetailsServiceImpl implements MultiplexDetailsService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private MultiplexAppRepository repository;
	private MultiplexUserDetailFeignProxy userDetailFeignProxy;
	private MultiplexMovieFeignProxy movieFeignProxy;

	public MultiplexDetailsServiceImpl(MultiplexAppRepository repository, MultiplexUserDetailFeignProxy userDetailFeignProxy,
			MultiplexMovieFeignProxy movieFeignProxy) {
		this.repository = repository;
		this.userDetailFeignProxy = userDetailFeignProxy;
		this.movieFeignProxy = movieFeignProxy;
	}

	@Override
	public MultiplexDetailsDto addMultiplex(MultiplexDetailsDto multiplexDto, String userId) throws MultiplexCustomException {

		try {
			validateUser(userId);
			// validation for adding multiplex
			if (multiplexDto.getMultiplexName() == null || multiplexDto.getMultiplexName().isEmpty())
				throw new Exception("multiplex name can not be null or empty");

			// converting multiplexDto to multiplex document
			MultiplexDetails multiplex = new MultiplexDetails(null, multiplexDto.getMultiplexName(), multiplexDto.getAddress(),
					multiplexDto.getNumberOfScreens());

			// saving multiplex to multiplex_micro database
			multiplex = repository.save(multiplex);

			// again converting multiplex document to multiplexDto
			multiplexDto = new MultiplexDetailsDto(multiplex.getId(), multiplex.getMultiplexName(), multiplex.getAddress(),
					multiplex.getNumberOfScreens());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MultiplexCustomException(e.getMessage());
		}
		return multiplexDto;
	}

	@Override
	public List<MultiplexDetailsDto> getAllMultiplexs() throws MultiplexCustomException {
		ArrayList<MultiplexDetailsDto> multiplexList;
		try {
			Iterator<MultiplexDetails> multiplexs = repository.findAll().iterator();
			multiplexList = new ArrayList<MultiplexDetailsDto>();
			while (multiplexs.hasNext()) {
				MultiplexDetails multiplex = multiplexs.next();
				MultiplexDetailsDto multiplexDto = new MultiplexDetailsDto(multiplex.getId(), multiplex.getMultiplexName(),
						multiplex.getAddress(), multiplex.getNumberOfScreens());
				multiplexList.add(multiplexDto);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MultiplexCustomException(e.getMessage());
		}
		return multiplexList;
	}

	@Override
	public MultiplexDetailsDto getMultiplexById(String multiplexId) throws MultiplexCustomException {
		MultiplexDetailsDto multiplexDto;
		try {
			Optional<MultiplexDetails> multiplexInDb = repository.findById(multiplexId);
			if (!multiplexInDb.isPresent())
				throw new Exception("Error during fetching multiplex details" + multiplexId);
			MultiplexDetails multiplex = multiplexInDb.get();
			multiplexDto = new MultiplexDetailsDto(multiplex.getId(), multiplex.getMultiplexName(), multiplex.getAddress(),
					multiplex.getNumberOfScreens());

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MultiplexCustomException(e.getMessage());
		}

		return multiplexDto;
	}

	@Override
	public MultiplexDetailsDto updateMultiplex(String multiplexId, MultiplexDetailsDto multiplexDto, String userId)
			throws MultiplexCustomException {
		try {
			validateUser(userId);
			Optional<MultiplexDetails> multiplexInDb = repository.findById(multiplexId);
			if (!multiplexInDb.isPresent())
				throw new Exception("Error while updating multiplex details, multiplexId: " + multiplexId);

			if (multiplexDto.getMultiplexName() == null || multiplexDto.getMultiplexName().isEmpty())
				throw new Exception("multiplex name can not be null or empty");

			MultiplexDetails multiplex = multiplexInDb.get();
			multiplex.setMultiplexName(multiplexDto.getMultiplexName());
			multiplex.setAddress(multiplexDto.getAddress());
			multiplex.setNumberOfScreens(multiplexDto.getNumberOfScreens());

			multiplex = repository.save(multiplex);

			multiplexDto = new MultiplexDetailsDto(multiplex.getId(), multiplex.getMultiplexName(), multiplex.getAddress(),
					multiplex.getNumberOfScreens());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MultiplexCustomException(e.getMessage());
		}
		return multiplexDto;
	}

	@Override
	public boolean deleteMultiplexById(String multiplexId, String userId) throws MultiplexCustomException {
		try {
			validateUser(userId);
			Optional<MultiplexDetails> multiplexInDb = repository.findById(multiplexId);
			if (multiplexInDb.isPresent()) {
				repository.deleteById(multiplexId);
				this.movieFeignProxy.deleteAllottedRecordByMultiplexId(multiplexId, userId);
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MultiplexCustomException(e.getMessage());
		}
	}

	private void validateUser(String userId) throws Exception {
		// validation for user
		MultiplexUserDetailDto userDetailDto = this.userDetailFeignProxy.getUserDetails(userId).getBody();
		if (!userDetailDto.getRole().equals("ADMIN"))
			throw new Exception("To Perform this operation you need ADMIN privilege");
	}

	@Override
	public List<MovieMultiplexSearchResultDto> searchMultiplexes(String searchString) throws MultiplexCustomException {
		List<MovieMultiplexSearchResultDto> multiplexMovieList = new ArrayList<MovieMultiplexSearchResultDto>();
		try {
			List<MultiplexDetailsDto> multiplexes = getMultiplexBySearchString(searchString);

			for (MultiplexDetailsDto multiplex : multiplexes) {
				List<MovieMultiplexSearchResultDto> movieMultiplexListInDb = this.movieFeignProxy
						.getAllotedMovieMultiplexByMultiplexId(multiplex.getId()).getBody();
				for (MovieMultiplexSearchResultDto movieMultiplex : movieMultiplexListInDb) {
					multiplexMovieList.add(movieMultiplex);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new MultiplexCustomException(e.getMessage());
		}
		return multiplexMovieList;
	}

	private List<MultiplexDetailsDto> getMultiplexBySearchString(String searchString) throws Exception {

		if (searchString == null || searchString.isEmpty())
			throw new Exception("search string can not be null or empty");

		List<MultiplexDetails> multiplexes = repository.findByMultiplexNameIgnoreCaseStartsWith(searchString);
		List<MultiplexDetailsDto> multiplexList = new ArrayList<MultiplexDetailsDto>();

		for (MultiplexDetails multiplex : multiplexes) {
			multiplexList.add(new MultiplexDetailsDto(multiplex.getId(), multiplex.getMultiplexName(), multiplex.getAddress(),
					multiplex.getNumberOfScreens()));
		}
		return multiplexList;

	}

}
