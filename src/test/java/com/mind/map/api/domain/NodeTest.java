package com.mind.map.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeTest {

    @Nested
    @DisplayName("Given a GetPath COMMAND")
    class GetPath {
        @Test
        @DisplayName("Case parentPath is root then do not append slash")
        void testGetPathCaseParentIsRoot() {
            Node node = getNode("", null);
            String parentPath = node.getPath();

            assertEquals("my-node", parentPath);
        }

        @Test
        @DisplayName("Case parentPath is not root then append withslash")
        void testGetPathCaseParentIsNotRoot() {
            Node node = getNode("like", null);
            String parentPath = node.getPath();

            assertEquals("like/my-node", parentPath);
        }
    }

    @Nested
    @DisplayName("Given a GetText COMMAND")
    class GetText {
        @Test
        @DisplayName("Case text is null then return empty")
        void getTextCaseTextIsNull() {
            Node node = getNode("like", null);
            String text = node.getText();

            assertEquals("", text);
        }

        @Test
        @DisplayName("Case text is not null then return text value")
         void getTextCaseTestNotNull() {
            Node node = getNode("", "text1");
            String text = node.getText();

            assertEquals("text1", text);
        }
    }

    private Node getNode(String path, String text) {
        return Node.builder()
                .id("e7bfca6d-6067-46f9-bdfb-0aca01fa6476")
                .name("my-node")
                .parentPath(path)
                .text(text)
                .build();
    }

}