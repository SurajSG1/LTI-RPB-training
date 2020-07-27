package com.multiplex.service.application.service;

import java.util.List;

import com.multiplex.service.application.dto.MovieMultiplexSearchResultDto;
import com.multiplex.service.application.dto.MultiplexDetailsDto;

public interface MultiplexDetailsService {

	List<MultiplexDetailsDto> getAllMultiplexs();

	MultiplexDetailsDto addMultiplex(MultiplexDetailsDto multiplexDto, String userId);

	MultiplexDetailsDto getMultiplexById(String multiplexId);

	MultiplexDetailsDto updateMultiplex(String multiplexId, MultiplexDetailsDto multiplexDto, String userId);

	boolean deleteMultiplexById(String multiplexId, String userId);

	List<MovieMultiplexSearchResultDto> searchMultiplexes(String searchString);

}
