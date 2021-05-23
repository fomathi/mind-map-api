package com.mind.map.api.services;

import com.mind.map.api.MapRepository;
import com.mind.map.api.domain.AddLeafResponse;
import com.mind.map.api.domain.CreateMapRequest;
import com.mind.map.api.domain.Leaf;
import com.mind.map.api.domain.Map;
import com.mind.map.api.domain.Node;
import com.mind.map.api.domain.NodeResponse;
import com.mind.map.api.domain.ReadMapResponse;
import com.mind.map.api.exceptions.AlreadyExistException;
import com.mind.map.api.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Service
@Slf4j
public class MapService {
    private final MapRepository repository;

    public MapService(MapRepository repository) {
        this.repository = repository;
    }

    public Map createMap(CreateMapRequest request) {
        try {
            Map map = new Map();
            map.setName(request.getId());
            repository.insert(map);
            return map;
        } catch (DuplicateKeyException ex) {
            throw new AlreadyExistException(String.format("A map with a name=%s already exist", request.getId()));
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ReadMapResponse getMap(String mapName) {
        Map map = repository.findByName(mapName);
        List<Node> mapNodes = map.getNodes();
        String rootPath = "";

        NodeResponse nodeResponse = NodeResponse.builder()
                                    .nodes(new ArrayList<>())
                                    .text(map.getName())
                                    .build();
        addChildNodes(nodeResponse, mapNodes, rootPath);

        return ReadMapResponse.builder()
                .nodes(Arrays.asList(nodeResponse))
                .build();
    }

    public String printMap(String map) {
        ReadMapResponse mapResponse = getMap(map);
        List<NodeResponse> nodes = mapResponse.getNodes();
        StringBuilder pretty = new StringBuilder();
        pretty.append("root/\r");
        int tab = 0;
        printChildNodes(pretty, nodes.get(0).getNodes(), tab);
        return pretty.toString();
    }

    private void printChildNodes(StringBuilder pretty, List<NodeResponse> nodeResponses, int tab) {
        tab += 1;
        for(NodeResponse node: nodeResponses) {
            for(int i=0; i<tab; i++) {
                pretty.append("\t");
            }
            pretty.append(node.getName());
            pretty.append("/");
            pretty.append("\r");

            List<NodeResponse> child = node.getNodes();
            if(child.size() > 0) {
                printChildNodes(pretty, node.getNodes(), tab);
            }
        }
    }

    private void addChildNodes(NodeResponse nodeResponse, List<Node> mapNodes, String rootPath) {
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

    private NodeResponse nodeToNodeResponse(Node node) {
        return NodeResponse.builder()
                .path(node.getPath())
                .text(node.getText())
                .name(node.getName())
                .nodes(new ArrayList<>())
                .build();
    }

    public AddLeafResponse addLeaf(String mapName, Leaf request) {
        Map map = repository.findByName(mapName);
        if (map == null) {
            throw new NotFoundException(String.format("Unable to find a map with name=%s", mapName));
        }

        String leafId = null;
        String[] paths = request.getPath().split("/");
        List<Node> nodes = map.getNodes();
        StringBuilder parentPath = new StringBuilder();
        int pointer = 1;
        int pathsLength = paths.length;
        for (String path : paths) {
            leafId = UUID.randomUUID().toString();
            parentPath.append("/" + path);
            String text = null;
            if(isLeafNode(pathsLength, pointer)) {
                text = request.getText();
            }
            createIfNotExistNode(nodes, parentPath.toString(), path, leafId, text);

            pointer += 1;
        }
        map.setNodes(nodes);
        repository.save(map);

        return AddLeafResponse.builder().id(leafId).build();
    }

    public Leaf readLeaf(String mapName, String leafId) {
        Map map = repository.findByName(mapName);
        if (map == null) {
            throw new NotFoundException(String.format("Unable to find the leaf. A map with name=%s doesn't exist", mapName));
        }

        List<Node> nodes = map.getNodes();

        for(Node node: nodes) {
            if(node.getId().equals(leafId)) {
                return Leaf.builder()
                        .path(node.getPath())
                        .text(node.getText())
                        .build();
            }
        }

        throw new NotFoundException(String.format("Unable to find the leaf with Id=%s", leafId));
    }

    private void createIfNotExistNode(List<Node> nodes,
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
        String path = (parentPath.lastIndexOf("/") <= 0) ? "" : parentPath.substring(1, parentPath.lastIndexOf("/"));
        return path;
    }

    private boolean isLeafNode(int pathsLength, int pointer) {
        return pointer == pathsLength;
    }

    private boolean nodeExist(List<Node> mapNodes, Node node) {
        return mapNodes.contains(node);
    }

}
