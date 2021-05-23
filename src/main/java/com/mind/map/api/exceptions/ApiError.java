package com.mind.map.api.exceptions;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString
public final class ApiError {
	private final String message;
}
