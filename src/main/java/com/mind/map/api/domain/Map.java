package com.mind.map.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "mind_map")
public class Map {
    @Id
    @JsonIgnore
    private String id;

    @Field(value = "name")
    @NotBlank
    @Indexed(unique=true)
    private String name;

    private List<Node> nodes = new ArrayList<>();
}
