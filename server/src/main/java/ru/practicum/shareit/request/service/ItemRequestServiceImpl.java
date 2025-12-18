package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.RepositoryUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto createRequest(long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User requestor = RepositoryUtil.getUserWithCheck(userRepository, userId);
        ItemRequest itemRequest = itemRequestMapper.fromItemRequestCreateDto(itemRequestCreateDto, requestor);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public List<ItemRequestDto> getRequestsForOwner(long ownerId) {
        User requestor = RepositoryUtil.getUserWithCheck(userRepository, ownerId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIsOrderByCreatedDesc(requestor);

        if (requests.isEmpty()) {
            return List.of();
        }

        return createItemRequestDtoList(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        User requestor = RepositoryUtil.getUserWithCheck(userRepository, userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorNotOrderByCreatedDesc(requestor);

        if (requests.isEmpty()) {
            return List.of();
        }

        return createItemRequestDtoList(requests);
    }

    @Override
    public ItemRequestDto getRequestById(long requestId) {
        ItemRequest itemRequest = RepositoryUtil.getItemRequestWithCheck(itemRequestRepository, requestId);
        return createItemRequestDtoList(List.of(itemRequest))
                .getFirst();
    }

    private List<ItemRequestDto> createItemRequestDtoList(List<ItemRequest> requests) {
        Map<Long, List<ItemResponseDto>> requestToResponsesMap = itemRepository
                .findAllByRequestIn(requests)
                .stream()
                .collect(
                        Collectors.groupingBy(
                                item -> item.getRequest().getId(),
                                Collectors.mapping(itemMapper::toItemResponseDto, Collectors.toList())
                        )
                );

        return requests.stream()
                .map(request -> {
                    List<ItemResponseDto> itemResponsesDto = requestToResponsesMap.get(request.getId());

                    return itemRequestMapper.toItemRequestDto(request, itemResponsesDto);
                })
                .toList();
    }
}
