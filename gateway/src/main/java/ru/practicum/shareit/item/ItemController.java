package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Request from user {} to create item: {}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemUpdateDto itemDto,
                              @PathVariable Long itemId) {
        log.info("Request from user {} to update item: {}", userId, itemDto);

        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Field 'name' can't be blank");
        }

        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Field 'description' can't be blank");
        }

        return itemClient.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody CommentCreateDto commentCreateDto,
                                 @PathVariable Long itemId) {
        log.info("Request from user {} to add comment for item with id" +
                " {} with content: {}", userId, itemId, commentCreateDto);
        return itemClient.addComment(userId, itemId, commentCreateDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public  ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        log.info("Request to get item {}", itemId);
        return itemClient.get(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request to get all items for user: {}", userId);
        return itemClient.getByOwner(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Request to search items with text: {}", text);
        return itemClient.search(text);
    }
}
