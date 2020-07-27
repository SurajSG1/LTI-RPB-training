package com.movie.service.application.dto;

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
public class MovieMultiplexSearchResultDetailsDto {
	
	private String movieName;
	private String multiplexName;
	private String address;
	private String screenName;
}
