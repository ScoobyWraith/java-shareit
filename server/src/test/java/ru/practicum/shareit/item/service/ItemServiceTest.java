package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.service.ServiceTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@Nested
@SpringJUnitConfig({ItemServiceImpl.class})
class ItemServiceTest extends ServiceTest {
    @Autowired
    private ItemService itemService;

    @Test
    void createTest() {
        Item item = itemsOfOwner1.getFirst();
        when(itemRepository.save(ArgumentMatchers.any()))
                .thenReturn(item);

        ItemDto itemDto = itemService.create(
                ownerOfItems1.getId(),
                new ItemDto(null, "n", "d", true, null)
        );

        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void getTest() {
        Item item = itemsOfOwner1.getFirst();

        ItemWithBookingAndCommentsDto itemDto = itemService.get(item.getId());

        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertNotNull(itemDto.getLastBooking());
        Assertions.assertNull(itemDto.getNextBooking());
    }

    @Test
    void updateTest() {
        Item item = itemsOfOwner1.getFirst();
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("new name", "new desc", true);

        ItemDto itemDto = itemService.update(
                ownerOfItems1.getId(),
                item.getId(),
                itemUpdateDto
        );

        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(itemUpdateDto.getName(), itemDto.getName());
        Assertions.assertEquals(itemUpdateDto.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(itemUpdateDto.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void deleteTest() {
        Item item = itemsOfOwner1.getFirst();

        ItemDto itemDto = itemService.delete(
                ownerOfItems1.getId(),
                item.getId()
        );

        Assertions.assertNotNull(itemDto);
    }

    @Test
    void searchTest() {
        when(itemRepository.search("word"))
                .thenReturn(itemsOfOwner1);

        List<ItemDto> items = itemService.search("word");

        Assertions.assertEquals(itemsOfOwner1.size(), items.size());
    }

    @Test
    void getByOwnerTest() {
        List<ItemWithBookingAndCommentsDto> items = itemService.getByOwner(ownerOfItems1.getId());

        Assertions.assertEquals(itemsOfOwner1.size(), items.size());
        Assertions.assertNotNull(items.getLast().getNextBooking());
        Assertions.assertNull(items.getLast().getLastBooking());
    }

    @Test
    void addCommentTest() {
        when(bookingRepository.findFirstByBookerAndItemAndStatusAndStartBefore(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any())
        ).thenReturn(Optional.of(bookingsForItemsOfOwner1.getFirst()));

        CommentDto commentDto = itemService.addComment(
                ownerOfItems1.getId(),
                itemsOfOwner1.getFirst().getId(),
                new CommentCreateDto("new comment"));

        Assertions.assertNotNull(commentDto);
        Assertions.assertEquals("new comment", commentDto.getText());
        Assertions.assertEquals(ownerOfItems1.getName(), commentDto.getAuthorName());
    }
}