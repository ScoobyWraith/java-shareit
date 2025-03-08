package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    User itemOwner;

    Item item1;
    Item item2;

    @BeforeEach
    public void beforeEach() {
        itemOwner = userRepository.save(new User(null, "tester", "test@test.com"));
        item1 = itemRepository.save(new Item(null, "n1", "d", true, itemOwner, null));
        item2 = itemRepository.save(new Item(null, "n2", "d", true, itemOwner, null));
    }

    @AfterEach
    public void afterEach() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByItemInTest() {
        Comment comment1 = commentRepository.save(new Comment(null, "c1", LocalDateTime.now(), itemOwner, item1));
        Comment comment2 = commentRepository.save(new Comment(null, "c2", LocalDateTime.now(), itemOwner, item2));

        List<Comment> comments = commentRepository.findAllByItemIn(List.of(item1, item2));

        Assertions.assertEquals(2, comments.size());
        Assertions.assertTrue(comments.stream().anyMatch(comment -> comment.getId().equals(comment1.getId())));
        Assertions.assertTrue(comments.stream().anyMatch(comment -> comment.getId().equals(comment2.getId())));
    }

    @Test
    void findAllByItemIsTest() {
        commentRepository.save(new Comment(null, "c1", LocalDateTime.now(), itemOwner, item1));
        commentRepository.save(new Comment(null, "c2", LocalDateTime.now(), itemOwner, item1));

        List<Comment> comments = commentRepository.findAllByItemIs(item2);

        Assertions.assertTrue(comments.isEmpty());
    }
}