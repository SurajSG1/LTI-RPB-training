package com.movie.service.application.feign;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.movie.service.application.dto.MultiplexDetailsDto;

@FeignClient(name="multiplex-microservice")
@RibbonClient(name="multiplex-microservice")
public interface MovieMultiplexFeignProxy {
	
	@GetMapping("/api/multiplex/{multiplexId}")
	public ResponseEntity<MultiplexDetailsDto> getMultiplexById(@PathVariable("multiplexId") String multiplexId);
	
}
