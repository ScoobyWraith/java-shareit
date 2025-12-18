package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(createDefaultRestTemplate(builder, serverUrl + API_PREFIX));
    }

    ResponseEntity<Object> createRequest(long userId, ItemRequestCreateDto itemRequestCreateDto) {
        return post("", userId, itemRequestCreateDto);
    }

    ResponseEntity<Object> getRequestsForOwner(long ownerId) {
        return get("", ownerId);
    }

    ResponseEntity<Object> getAllRequests(long userId) {
        return get("/all", userId);
    }

    ResponseEntity<Object> getRequestById(long requestId) {
        return get("/" + requestId);
    }
}
