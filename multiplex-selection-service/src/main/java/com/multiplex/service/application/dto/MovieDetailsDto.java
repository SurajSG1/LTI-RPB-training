package com.multiplex.service.application.dto;

import java.time.LocalDate;

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
public class MovieDetailsDto {
	private String id;
	private String movieName;
	private String movieCategory;
	private String movieProducer;
	private String movieDirector;
	private LocalDate releaseDate;
}
