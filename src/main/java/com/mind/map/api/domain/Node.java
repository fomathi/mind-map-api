package com.mind.map.api.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class Node {
    private String id;
    private String name;
    private String parentPath;
    private String text;

    public String getPath() {
        StringBuilder path = new StringBuilder(this.getParentPath());
        if(!getParentPath().equals("")) {
            path.append("/");
        }
        path.append(this.getName());

        return path.toString();
    }

    public String getText() {
        String text = (this.text == null) ? "" : this.text;
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return name.equals(node.name) && parentPath.equals(node.parentPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentPath);
    }
}
