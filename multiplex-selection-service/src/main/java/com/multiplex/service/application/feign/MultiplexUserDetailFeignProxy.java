package com.multiplex.service.application.feign;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.multiplex.service.application.dto.MultiplexUserDetailDto;


@FeignClient(name = "user-microservice")
@RibbonClient(name = "user-microservice")
public interface MultiplexUserDetailFeignProxy {
	
	@GetMapping("/api/user/{userId}")
	public ResponseEntity<MultiplexUserDetailDto> getUserDetails(@PathVariable("userId") String userId);
}
