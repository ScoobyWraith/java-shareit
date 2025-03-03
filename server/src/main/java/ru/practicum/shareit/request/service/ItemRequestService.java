package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(long userId, ItemRequestCreateDto itemRequestCreateDto);

    List<ItemRequestDto> getRequestsForOwner(long ownerId);

    List<ItemRequestDto> getAllRequests(long userId);

    ItemRequestDto getRequestById(long requestId);
}
