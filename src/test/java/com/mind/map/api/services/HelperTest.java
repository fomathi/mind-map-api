package com.mind.map.api.services;

import com.mind.map.api.domain.Node;
import com.mind.map.api.domain.NodeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HelperTest {
    Helper helper = new Helper();

    @Nested
    @DisplayName("Given a PrintChildNodes CMD")
    class PrintChildNodes {
        @Test
        @DisplayName("Given a Map, pretty print the whole tree of the map")
        void testPrintChildNodes() {
            List<NodeResponse> nodeResponses = nodeResponses();
            StringBuilder pretty = new StringBuilder();
            pretty.append("root/\n");
            helper.printChildNodes(pretty, nodeResponses, 0);
            assertEquals("root/\n\tname1/\n\tname2/\n\t\tname21/\n\t\tname22/\n", pretty.toString());
        }
    }

    @Nested
    @DisplayName("Given an addChildNodes CMD")
    class addChildNodes {
        @Test
        @DisplayName("Given a list of Nodes, build a mind Map from that list")
        void addChildNodes() {
            List<Node> mapNodes = mapNodes();
            NodeResponse nodeResponse = NodeResponse.builder()
                                        .nodes(new ArrayList<>())
                                        .name("my-map")
                                        .build();
            String rootPath = "";
            helper.addChildNodes(nodeResponse, mapNodes, rootPath);
            System.out.println(nodeResponse);
            assertEquals("my-map", nodeResponse.getName());
            assertEquals(1, nodeResponse.getNodes().size());

            NodeResponse root = nodeResponse.getNodes().get(0);
            assertEquals(2, root.getNodes().size());
            assertEquals("name1", root.getNodes().get(0).getName());
            assertEquals("i/name1", root.getNodes().get(0).getPath());

            assertEquals(2, root.getNodes().get(1).getNodes().size());
            assertEquals("name21", root.getNodes().get(1).getNodes().get(0).getName());
            assertEquals("i/name2/name22", root.getNodes().get(1).getNodes().get(1).getPath());
        }
    }

    @Nested
    @DisplayName("GIVEN a NodeToNodeResponse CMD")
    class NodeToNodeResponse {
        @Test
        @DisplayName("Test convert Node to NodeResponse")
        void testNodeToNodeResponse() {
            Node node = Node.builder()
                    .id("e7bfca6d-6067-46f9-bdfb-0aca01fa6476")
                    .name("my-node")
                    .parentPath("root")
                    .text("text1")
                    .build();

            NodeResponse response = helper.nodeToNodeResponse(node);
            assertEquals("my-node", response.getName());
            assertEquals("text1", response.getText());
            assertEquals("root/my-node", response.getPath());
            assertEquals(0, response.getNodes().size());
        }
    }

    @Nested
    @DisplayName("Given a CreateIfNotExistNode CMD")
    class CreateIfNotExistNode {
        @Test
        @DisplayName("Create node case Node already exist, then it's not added")
        void testCreateIfNotExistNodeCaseNodeExist() {
            List<Node> nodes = mapNodes();
            String parentPath = "/";
            String path = "i";
            String id = UUID.randomUUID().toString();
            helper.createIfNotExistNode(nodes, parentPath, path, id, "");
            assertEquals(5, nodes.size());
            assertEquals("i", nodes.get(0).getName());
        }

        @Test
        @DisplayName("Create node case Node doesn't exist, then it's  added")
        void testCreateIfNotExistNodeCaseNodeDoNotExist_1() {
            List<Node> nodes = mapNodes();
            String parentPath = "/";
            String path = "u";
            String id = UUID.randomUUID().toString();
            helper.createIfNotExistNode(nodes, parentPath, path, id, "");
            assertEquals(6, nodes.size());
            assertEquals("u", nodes.get(5).getName());
        }

        @Test
        @DisplayName("Create node case Node doesn't exist, then it's  added")
        void testCreateIfNotExistNodeCaseNodeDoNotExist_2() {
            List<Node> nodes = mapNodes();
            String parentPath = "/i/name1/";
            String path = "name11";
            String id = UUID.randomUUID().toString();
            helper.createIfNotExistNode(nodes, parentPath, path, id, "text text");
            System.out.println(nodes);
            assertEquals(6, nodes.size());
            assertEquals("name11", nodes.get(5).getName());
            assertEquals("text text", nodes.get(5).getText());
        }
    }

    @Nested
    @DisplayName("Given an isLeafNode CMD")
    class IsLeafNode {
        @Test
        @DisplayName("Given a node that has a child, then return false")
        void testIsLeafNodeCaseNodeHasChild() {
            int pathsLength = 5, pointer = 3;
            assertFalse(helper.isLeafNode(pathsLength, pointer));
        }

        @Test
        @DisplayName("Given a leaf node, then return true")
        void testIsLeafNodeCaseNodeDoNotHaveChild() {
            int pathsLength = 5, pointer = 5;
            assertTrue(helper.isLeafNode(pathsLength, pointer));
        }
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
                .id(UUID.randomUUID().toString())
                .build();

        nodes.add(root);
        nodes.add(name1);
        nodes.add(name2);
        nodes.add(name21);
        nodes.add(name22);

        return nodes;
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

        nodeResponses.add(nodeResponse1);
        nodeResponses.add(nodeResponse2);

        return nodeResponses;
    }
}