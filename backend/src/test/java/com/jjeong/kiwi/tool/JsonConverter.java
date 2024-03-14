package com.jjeong.kiwi.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    //HATEOAS _links json 구조를 실제 반환값처럼 변환하는 메서드.
    public static String convertLinks(String jsonInput) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonInput);

        // 'links' 배열을 가져옵니다.
        JsonNode linksArray = rootNode.path("links");
        if (linksArray.isArray()) {
            ObjectNode linksObject = objectMapper.createObjectNode();

            // 배열 내의 각 항목을 '_links' 객체에 추가합니다.
            for (JsonNode link : linksArray) {
                String rel = link.get("rel").asText();
                JsonNode href = link.get("href");
                // 'rel' 값을 키로 하고 'href'를 값으로 하는 객체를 생성합니다.
                ObjectNode linkNode = objectMapper.createObjectNode();
                linkNode.set("href", href);
                linksObject.set(rel, linkNode);
            }

            // 원래 JSON 객체에서 'links' 배열을 제거하고, '_links' 객체를 추가합니다.
            ((ObjectNode) rootNode).remove("links");
            ((ObjectNode) rootNode).set("_links", linksObject);
        }

        // 수정된 JSON 객체를 문자열로 변환하여 반환합니다.
        return objectMapper.writeValueAsString(rootNode);
    }
}
