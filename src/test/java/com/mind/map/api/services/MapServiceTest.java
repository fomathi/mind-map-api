package com.mind.map.api.services;

import com.mind.map.api.MapRepository;
import com.mind.map.api.domain.AddLeafResponse;
import com.mind.map.api.domain.CreateMapRequest;
import com.mind.map.api.domain.Leaf;
import com.mind.map.api.domain.Map;
import com.mind.map.api.domain.Node;
import com.mind.map.api.domain.ReadMapResponse;
import com.mind.map.api.exceptions.AlreadyExistException;
import com.mind.map.api.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapServiceTest {
    private final MapRepository repository = mock(MapRepository.class);
    private final Helper helper = new Helper();

    private final MapService service = new MapService(repository, helper);

    @Nested
    @DisplayName("Given a createMap CMD")
    class CreateMap {
        @Test
        @DisplayName("Given CreateMapRequest and request is correct, then create Map")
        void testCreateMapCasEverythingOk() {
            CreateMapRequest request = createMapRequest();
            Map map = service.createMap(request);
            assertEquals("my-map", map.getName());
        }

        @Test
        @DisplayName("Given CreateMapRequest and map already exist, then throw AlreadyExistException")
        void testCreateMapCasMapAlreadyExist() {
            CreateMapRequest request = createMapRequest();
            when(repository.insert(any(Map.class))).thenThrow(DuplicateKeyException.class);
            assertThrows(AlreadyExistException.class, ()->{
                service.createMap(request);
            });
        }
    }

    @Nested
    @DisplayName("Given a GetMap CMD")
    class GetMap {
        @Test
        @DisplayName("Case map doesn't exist, then throw NotFoundException")
        void testGetMapCaseMapDoNotExist() {
            when(repository.findByName(any(String.class))).thenReturn(null);
            assertThrows(NotFoundException.class, ()->{
                service.getMap("my-map");
            });
        }

        @Test
        @DisplayName("Case map exist, then return the whole map")
        void testGetMapCaseMapExist() {
            Map map = map();
            when(repository.findByName(any(String.class))).thenReturn(map);

            ReadMapResponse response = service.getMap("my-map");
            assertEquals(1, response.getNodes().size());
            assertEquals("my-map", response.getNodes().get(0).getName());
            assertEquals(null, response.getNodes().get(0).getText());
            assertEquals(1, response.getNodes().get(0).getNodes().size());
            assertEquals("i", response.getNodes().get(0).getNodes().get(0).getName());
            assertEquals(2, response.getNodes().get(0).getNodes().get(0).getNodes().size());
            assertEquals("name2", response.getNodes().get(0).getNodes().get(0).getNodes().get(1).getName());
            assertEquals("i/name2", response.getNodes().get(0).getNodes().get(0).getNodes().get(1).getPath());
            assertEquals("name21", response.getNodes().get(0).getNodes().get(0).getNodes().get(1).getNodes().get(0).getName());
            assertEquals("name22", response.getNodes().get(0).getNodes().get(0).getNodes().get(1).getNodes().get(1).getName());
        }
    }

    @Nested
    @DisplayName("Given a printMap CMD")
    class PrintMap {
        @Test
        @DisplayName("Given a Map object return a pretty print string")
        void testPrintMap() {
            Map map = map();
            when(repository.findByName(any(String.class))).thenReturn(map);

            String pretty = service.printMap("my-map");
            assertEquals("root/\n\ti/\n\t\tname1/\n\t\tname2/\n\t\t\tname21/\n\t\t\tname22/\n", pretty);
        }
    }

    @Nested
    @DisplayName("Given an addLeaf CMD")
    class AddLeaf {
        @Test
        @DisplayName("Case map was nof found, throw NotFoundException")
        void testAddLeafCaseMapNotFound() {
            when(repository.findByName(any(String.class))).thenReturn(null);
            Leaf request = Leaf.builder().build();
            assertThrows(NotFoundException.class, ()->{
                service.addLeaf("my-map", request);
            });
        }

        @Test
        @DisplayName("Case map exit, add leaf")
        void testAddLeafCaseMapExist() {
            Map map = map();
            when(repository.findByName(any(String.class))).thenReturn(map);
            Leaf request = Leaf.builder()
                    .path("u/know")
                    .text("text text")
                    .build();
            AddLeafResponse response = service.addLeaf("my-map", request);
            System.out.println(map);
            assertEquals(7, map.getNodes().size());
            assertEquals("my-map", map.getName());
            assertEquals("u", map.getNodes().get(5).getName());
            assertEquals("", map.getNodes().get(5).getParentPath());
            assertEquals("know", map.getNodes().get(6).getName());
            assertEquals("u", map.getNodes().get(6).getParentPath());
            assertEquals("text text", map.getNodes().get(6).getText());
        }
    }

    @Nested
    @DisplayName("Given a readLeaf CMD")
    class ReadLeaf {
        @Test
        @DisplayName("Case map does not exits, then throw NotFoundException")
        void testReadLeafCaseMapDoesNotExist() {
            when(repository.findByName(any(String.class))).thenReturn(null);
            assertThrows(NotFoundException.class, ()->{
                service.readLeaf("my-map", "i/name1");
            });
        }

        @Test
        @DisplayName("Case leaf was not found, then throw NotFoundException")
        void testReadLeafCaseLeafDoesNotExist() {
            Map map = map();
            when(repository.findByName(any(String.class))).thenReturn(map);
            assertThrows(NotFoundException.class, ()->{
                service.readLeaf("my-map", "I/Know");
            });
        }

        @Test
        @DisplayName("Case leaf is found, then return leaf")
        void testReadLeafCaseLeafExist() {
            Map map = map();
            when(repository.findByName(any(String.class))).thenReturn(map);
            Leaf leaf = service.readLeaf("my-map", "e7bfca6d-6067-46f9-bdfb-0aca01fa6476");
            assertEquals("i/name2/name22", leaf.getPath());
            assertEquals("text text", leaf.getText());
        }
    }

    private Map map() {
        Map map = new Map();
        map.setName("my-map");
        map.setNodes(mapNodes());
        return map;
    }

    private CreateMapRequest createMapRequest() {
        CreateMapRequest request = new CreateMapRequest();
        request.setId("my-map");
        return request;
    }

    private List<Node> mapNodes() {
        List<Node> nodes = new ArrayList<>();
        Node root = Node.builder()
                .parentPath("")
                .name("i")
                .id(UUID.randomUUID().toString())
                .build();

        Node name1 = Node.builder()
                .parentPath("i")
                .name("name1")
                .id(UUID.randomUUID().toString())
                .build();

        Node name2 = Node.builder()
                .parentPath("i")
                .name("name2")
                .id(UUID.randomUUID().toString())
                .build();

        Node name21 = Node.builder()
                .parentPath("i/name2")
                .name("name21")
                .id(UUID.randomUUID().toString())
                .build();

        Node name22 = Node.builder()
                .parentPath("i/name2")
                .name("name22")
                .text("text text")
                .id("e7bfca6d-6067-46f9-bdfb-0aca01fa6476")
                .build();

        nodes.add(root);
        nodes.add(name1);
        nodes.add(name2);
        nodes.add(name21);
        nodes.add(name22);

        return nodes;
    }
}