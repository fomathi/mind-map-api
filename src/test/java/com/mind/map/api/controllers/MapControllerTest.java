package com.mind.map.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mind.map.api.domain.CreateMapRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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