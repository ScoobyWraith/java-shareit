package ru.practicum.shareit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.IllegalOwner;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

class RepositoryUtilTest {

    @Test
    void checkOwnerRightsForItem() {
        User owner = new User(1L, "owner", "email");
        User user = new User(2L, "user", "email");
        Item item = new Item(1L, "item", "desc", true, owner, null);

        Assertions.assertThrows(IllegalOwner.class, () -> {
            RepositoryUtil.checkOwnerRightsForItem(item, user);
        });
    }
}
