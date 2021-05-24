package com.mind.map.api.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReadMapResponse {
    private List<NodeResponse> nodes;
}
