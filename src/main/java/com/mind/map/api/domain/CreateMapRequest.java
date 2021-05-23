package com.mind.map.api.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateMapRequest {
    @NotBlank
    private String id;
}
