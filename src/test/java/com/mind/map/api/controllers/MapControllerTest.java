package com.mind.map.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mind.map.api.domain.AddLeafResponse;
import com.mind.map.api.domain.CreateMapRequest;
import com.mind.map.api.domain.Leaf;
import com.mind.map.api.domain.Map;
import com.mind.map.api.domain.NodeResponse;
import com.mind.map.api.domain.ReadMapResponse;
import com.mind.map.api.exceptions.AlreadyExistException;
import com.mind.map.api.exceptions.NotFoundException;
import com.mind.map.api.services.MapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MapController.class)
class MapControllerTest {
    @MockBean
    MapService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Nested
    @DisplayName("Given a CreateMap CMD")
    class CreateMap {
        @Test
        @DisplayName("Happy path")
        void testCreateMapHappyPath() throws Exception {
            CreateMapRequest request = createMapRequest();
            Map map = new Map();
            map.setName(request.getId());

            when(service.createMap(any(CreateMapRequest.class))).thenReturn(map);
            mockMvc.perform(post("/map")
                    .content(mapper.writeValueAsString(request))
                    .contentType("application/json"))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.name").value("my-map"));

        }

        @Test
        @DisplayName("Case duplicate Key")
        void testCreateMapCaseDuplicateKey() throws Exception {
            CreateMapRequest request = createMapRequest();
            Map map = new Map();
            map.setName(request.getId());

            when(service.createMap(any(CreateMapRequest.class))).thenThrow(AlreadyExistException.class);
            mockMvc.perform(post("/map")
                    .content(mapper.writeValueAsString(request))
                    .contentType("application/json"))
                    .andExpect(status().is4xxClientError());

        }

        @Test
        @DisplayName("Case Unexpected exception")
        void testCreateMapCaseUnexpectedException() throws Exception {
            CreateMapRequest request = createMapRequest();
            Map map = new Map();
            map.setName(request.getId());

            when(service.createMap(any(CreateMapRequest.class))).thenThrow(NullPointerException.class);
            mockMvc.perform(post("/map")
                    .content(mapper.writeValueAsString(request))
                    .contentType("application/json"))
                    .andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("Given a GetMap CMD")
    class GetMap {
        @Test
        @DisplayName("Happy Path")
        void testGetMapHappyPath() throws Exception {
            ReadMapResponse response =  readMapResponse();
            when(service.getMap(any(String.class))).thenReturn(response);

            mockMvc.perform(get("/map/my-map")
                    .contentType("application/json"))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("Case map was not found")
        void testGetMapCaseMapNotFound() throws Exception {
            when(service.getMap(any(String.class))).thenThrow(NotFoundException.class);

            mockMvc.perform(get("/map/my-map")
                    .contentType("application/json"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Given a PrintMap CMD")
    class PrintMap {
        @Test
        @DisplayName("Happy Path")
        void testGetMapHappyPath() throws Exception {
            String pretty = "root/\n\tname1/\n\tname2/\n\t\tname21/\n\t\tname22/\n";
            when(service.printMap(any(String.class))).thenReturn(pretty);

            mockMvc.perform(get("/map/my-map/pretty")
                    .contentType("application/json"))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("Case map was not found")
        void testGetMapCaseMapNotFound() throws Exception {
            when(service.printMap(any(String.class))).thenThrow(NotFoundException.class);

            mockMvc.perform(get("/map/my-map/pretty")
                    .contentType("application/json"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Given an AddLeaf CMD")
    class AddLeaf {
        @Test
        @DisplayName("Happy path")
        void testAddLeafHappyPath() throws Exception {
            Leaf request = Leaf.builder()
                    .path("i/like/fruits")
                    .text("text text")
                    .build();
            AddLeafResponse response = AddLeafResponse.builder()
                                        .id("e7bfca6d-6067-46f9-bdfb-0aca01fa6476")
                                        .build();
            when(service.addLeaf(any(String.class), any(Leaf.class))).thenReturn(response);
            mockMvc.perform(post("/map/my-map/leaf")
                    .content(mapper.writeValueAsString(request))
                    .contentType("application/json"))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.id").value("e7bfca6d-6067-46f9-bdfb-0aca01fa6476"));
        }

        @Test
        @DisplayName("Case Unexpected error happened")
        void testAddLeafUnexpected() throws Exception {
            Leaf request = Leaf.builder()
                    .path("i/like/fruits")
                    .text("text text")
                    .build();

            when(service.addLeaf(any(String.class), any(Leaf.class))).thenThrow(NullPointerException.class);
            mockMvc.perform(post("/map/my-map/leaf")
                    .content(mapper.writeValueAsString(request))
                    .contentType("application/json"))
                    .andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("Given a ReadLeaf CMD")
    class ReadLeaf {
        @Test
        @DisplayName("Happy path")
        void testReadLeafCaseHappyPath() throws Exception {
            Leaf leaf =  Leaf.builder()
                    .path("i/love/fruits")
                    .text("text text")
                    .build();
            when(service.readLeaf(any(String.class), any(String.class))).thenReturn(leaf);

            mockMvc.perform(get("/map/my-map/leaf/e7bfca6d-6067-46f9-bdfb-0aca01fa6476")
                    .contentType("application/json"))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.path").value("i/love/fruits"))
                    .andExpect(jsonPath("$.text").value("text text"));
            ;
        }

        @Test
        @DisplayName("Case map doesn't exit, then thrown NotFoundException")
        void testReadLeafCaseMapDoNotExis() throws Exception {
            when(service.readLeaf(any(String.class), any(String.class))).thenThrow(NotFoundException.class);

            mockMvc.perform(get("/map/my-map/leaf/e7bfca6d-6067-46f9-bdfb-0aca01fa6476")
                    .contentType("application/json"))
                    .andExpect(status().is4xxClientError())
            ;
        }
    }

    private CreateMapRequest createMapRequest() {
        CreateMapRequest request = new CreateMapRequest();
        request.setId("my-map");
        return request;
    }

    private ReadMapResponse readMapResponse() {
        return ReadMapResponse.builder()
                .nodes(nodeResponses())
                .build();
    }

    private List<NodeResponse> nodeResponses() {
        List<NodeResponse> nodeResponses = new ArrayList<>();
        NodeResponse nodeResponse1 = NodeResponse.builder()
                .name("name1")
                .path("i/name1")
                .nodes(new ArrayList<>())
                .build();

        NodeResponse nodeResponse21 = NodeResponse.builder()
                .name("name21")
                .path("i/name2/n1")
                .text("reason21")
                .nodes(new ArrayList<>())
                .build();
        NodeResponse nodeResponse22 = NodeResponse.builder()
                .name("name22")
                .path("i/name2/n2")
                .text("reason22")
                .nodes(new ArrayList<>())
                .build();
        NodeResponse nodeResponse2 = NodeResponse.builder()
                .name("name2")
                .path("i/name2")
                .nodes(Arrays.asList(nodeResponse21,nodeResponse22))
                .build();

        NodeResponse node = NodeResponse.builder()
                .name("i")
                .path("i")
                .nodes(new ArrayList<>())
                .build();

        node.getNodes().add(nodeResponse1);
        node.getNodes().add(nodeResponse2);

        NodeResponse root = NodeResponse.builder()
                .name("my-map")
                .path("")
                .nodes(new ArrayList<>())
                .build();

        root.getNodes().add(node);

        nodeResponses.add(root);
        return nodeResponses;
    }
}