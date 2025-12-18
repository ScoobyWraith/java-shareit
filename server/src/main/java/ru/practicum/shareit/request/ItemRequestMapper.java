package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequest fromItemRequestCreateDto(ItemRequestCreateDto itemRequestCreateDto, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .requestor(requestor)
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemResponseDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
