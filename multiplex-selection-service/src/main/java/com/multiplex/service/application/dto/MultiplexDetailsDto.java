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
public class MultiplexDetailsDto {
	private String id;
	private String multiplexName;
	private String address;
	private String numberOfScreens;
}
