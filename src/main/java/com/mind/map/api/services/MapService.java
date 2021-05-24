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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MapService {
    private final MapRepository repository;
    private final Helper helper;

    public MapService(MapRepository repository, Helper helper) {
        this.repository = repository;
        this.helper = helper;
    }

    public Map createMap(CreateMapRequest request) {
        try {
            Map map = new Map();
            map.setName(request.getId());
            repository.insert(map);
            return map;
        } catch (DuplicateKeyException ex) {
            throw new AlreadyExistException(String.format("A map with a name=%s already exist", request.getId()));
        }
    }

    public ReadMapResponse getMap(String mapName) {
        Map map = repository.findByName(mapName);
        if (map == null) {
            throw new NotFoundException(String.format("Unable to find a map with name=%s", mapName));
        }
        List<Node> mapNodes = map.getNodes();
        String rootPath = "";

        NodeResponse nodeResponse = NodeResponse.builder()
                                    .nodes(new ArrayList<>())
                                    .name(map.getName())
                                    .build();
        helper.addChildNodes(nodeResponse, mapNodes, rootPath);

        return ReadMapResponse.builder()
                .nodes(Collections.singletonList(nodeResponse))
                .build();
    }

    public String printMap(String map) {
        ReadMapResponse mapResponse = getMap(map);
        List<NodeResponse> nodes = mapResponse.getNodes();
        StringBuilder pretty = new StringBuilder();
        pretty.append("root/\n");
        int tab = 0;
        helper.printChildNodes(pretty, nodes.get(0).getNodes(), tab);
        return pretty.toString();
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
            parentPath.append("/");
            parentPath.append(path);
            String text = null;
            if(helper.isLeafNode(pathsLength, pointer)) {
                text = request.getText();
            }
            helper.createIfNotExistNode(nodes, parentPath.toString(), path, leafId, text);

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

}
