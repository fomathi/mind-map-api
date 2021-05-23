package com.mind.map.api.domain;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Leaf {
    @NotBlank
    private String path;
    @NotBlank
    private String text;
}
