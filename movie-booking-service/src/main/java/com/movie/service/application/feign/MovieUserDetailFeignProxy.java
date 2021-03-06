package com.movie.service.application.feign;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.movie.service.application.dto.MovieUserDetailDto;

@FeignClient(name = "user-microservice")
@RibbonClient(name = "user-microservice")
public interface MovieUserDetailFeignProxy {
	
	@GetMapping("/api/user/{userId}")
	public ResponseEntity<MovieUserDetailDto> getUserDetails(@PathVariable("userId") String userId);
}
