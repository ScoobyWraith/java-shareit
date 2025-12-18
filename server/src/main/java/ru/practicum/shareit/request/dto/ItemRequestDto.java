package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
    private List<ItemResponseDto> items;
}
