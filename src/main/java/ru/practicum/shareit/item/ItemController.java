package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Request from user {} to create item: {}", userId, itemDto);
        Item item = itemMapper.fromItemDto(itemDto);
        return itemMapper.toItemDto(itemService.create(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemUpdateDto itemDto,
                           @PathVariable Long itemId) {
        log.info("Request from user {} to update item: {}", userId, itemDto);
        Item item = itemMapper.fromItemUpdateDto(itemDto);
        return itemMapper.toItemDto(itemService.update(userId, itemId, item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Request to get item {}", itemId);
        return itemMapper.toItemDto(itemService.get(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request to get all items for user: {}", userId);
        return itemService.getByOwner(userId).stream().map(itemMapper::toItemDto).toList();
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Request to search items with text: {}", text);
        return itemService.search(text).stream().map(itemMapper::toItemDto).toList();
    }
}
