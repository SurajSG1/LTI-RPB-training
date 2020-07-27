package com.multiplex.service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MovieMultiplexDetailsDto {
	private String id;
	private MovieDetailsDto movie;
	private MultiplexDetailsDto multiplex;
	private String screenName;
	private MultiplexUserDetailDto userDetail;
}
