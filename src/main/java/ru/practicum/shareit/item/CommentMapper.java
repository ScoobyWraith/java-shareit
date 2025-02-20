package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {
    private static final DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(dateTimeFormatter.format(comment.getCreated()))
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public Comment fromCommentCreateDto(CommentCreateDto commentCreateDto, User author, Item item) {
        return Comment.builder()
                .text(commentCreateDto.getText())
                .author(author)
                .item(item)
                .build();
    }
}
