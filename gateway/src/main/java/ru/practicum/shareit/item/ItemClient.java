package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(createDefaultRestTemplate(builder, serverUrl + API_PREFIX));
    }

    ResponseEntity<Object> create(Long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    ResponseEntity<Object> get(Long id) {
        return get("/" + id);
    }

    ResponseEntity<Object> update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) {
        return patch("/" + itemId, ownerId, itemUpdateDto);
    }

    ResponseEntity<Object> search(String searchText) {
        Map<String, Object> parameters = Map.of("text", searchText);
        return get("/search?text={text}", parameters);
    }

    ResponseEntity<Object> getByOwner(Long ownerId) {
        return get("", ownerId);
    }

    ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }
}
