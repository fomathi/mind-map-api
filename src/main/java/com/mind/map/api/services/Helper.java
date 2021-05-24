package com.mind.map.api.services;

import com.mind.map.api.domain.Node;
import com.mind.map.api.domain.NodeResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Component
public class Helper {
    public void printChildNodes(StringBuilder pretty, List<NodeResponse> nodeResponses, int tab) {
        tab += 1;
        for(NodeResponse node: nodeResponses) {
            for(int i=0; i<tab; i++) {
                pretty.append("\t");
            }
            pretty.append(node.getName());
            pretty.append("/");
            pretty.append("\n");
            List<NodeResponse> child = node.getNodes();
            if(!child.isEmpty()) {
                printChildNodes(pretty, node.getNodes(), tab);
            }
        }
    }

    public void addChildNodes(NodeResponse nodeResponse, List<Node> mapNodes, String rootPath) {
        java.util.Map<String, NodeResponse> nodeResponseMap = new TreeMap<>();
        nodeResponse.setPath(rootPath);
        for(Node node: mapNodes) {
            if(node.getParentPath().equals(rootPath)) {
                NodeResponse currentNodeResponse = nodeToNodeResponse(node);
                nodeResponse.getNodes().add(currentNodeResponse);
                nodeResponseMap.put(node.getPath(), currentNodeResponse);
            }
        }

        nodeResponseMap.forEach((path, currentNodeResponse) -> addChildNodes(currentNodeResponse, mapNodes, path));
    }

    public NodeResponse nodeToNodeResponse(Node node) {
        return NodeResponse.builder()
                .path(node.getPath())
                .text(node.getText())
                .name(node.getName())
                .nodes(new ArrayList<>())
                .build();
    }

    public void createIfNotExistNode(List<Node> nodes,
                                      String parentPath,
                                      String path,
                                      String id,
                                      String text) {
        String pPath = buildParentPath(parentPath);
        Node currentNode = Node.builder()
                .name(path)
                .id(id)
                .parentPath(pPath)
                .text(text)
                .build();
        boolean nodeExist = nodeExist(nodes, currentNode);
        if(!nodeExist) {
            nodes.add(currentNode);
        }
    }

    private String buildParentPath(String parentPath) {
        return (parentPath.lastIndexOf("/") <= 0) ? "" : parentPath.substring(1, parentPath.lastIndexOf("/"));
    }

    public boolean isLeafNode(int pathsLength, int pointer) {
        return pointer == pathsLength;
    }

    public boolean nodeExist(List<Node> mapNodes, Node node) {
        return mapNodes.contains(node);
    }
}
