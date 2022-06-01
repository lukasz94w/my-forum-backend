package pl.lukasz94w.myforum.service.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.lukasz94w.myforum.model.User;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceUtilTest {

    @Autowired
    UserServiceUtil userServiceUtil;

    User user1, user2, user3, user4, user5;
    User user6, user7, user8, user9, user10;

    @Test
    void shouldCountNumberOfEntriesByUserFromFullList() {
        List<User> users = prepareSortedUsers();
        List<Object[]> unsortedUsersWithEntries = prepareFullUnsortedListOfUsers();

        List<Long> sortedEntriesOfUsersByTestedMethod = userServiceUtil.prepareNumberOfEntriesInPageableUsers(users, unsortedUsersWithEntries);
        List<Long> expectedEntriesOfUsers = List.of(2320L, 2L, 14L, 3212L, 6L, 1323L, 321L, 319L, 13L, 3L);

        assertEquals(expectedEntriesOfUsers, sortedEntriesOfUsersByTestedMethod);
    }

    @Test
    void shouldCountNumberOfEntriesByUserFromNotFullList() {
        List<User> users = prepareSortedUsers();
        List<Object[]> unsortedNotFullOfUsersWithEntries = prepareNotFullUnsortedListOfUsers();

        List<Long> sortedEntriesOfUsersByTestedMethod = userServiceUtil.prepareNumberOfEntriesInPageableUsers(users, unsortedNotFullOfUsersWithEntries);
        List<Long> expectedEntriesOfUsers = List.of(0L, 2L, 14L, 0L, 0L, 1323L, 321L, 0L, 6L, 0L);

        assertEquals(expectedEntriesOfUsers, sortedEntriesOfUsersByTestedMethod);
    }

    List<User> prepareSortedUsers() {
        user1 = new User();
        user2 = new User();
        user3 = new User();
        user4 = new User();
        user5 = new User();
        user6 = new User();
        user7 = new User();
        user8 = new User();
        user9 = new User();
        user10 = new User();
        return List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9, user10);
    }

    List<Object[]> prepareFullUnsortedListOfUsers() {
        List<Object[]> unsortedUsersWithEntries = new LinkedList<>();
        unsortedUsersWithEntries.add(new Object[]{user6, 1324L});
        unsortedUsersWithEntries.add(new Object[]{user1, 2321L});
        unsortedUsersWithEntries.add(new Object[]{user9, 14L});
        unsortedUsersWithEntries.add(new Object[]{user3, 15L});
        unsortedUsersWithEntries.add(new Object[]{user5, 7L});
        unsortedUsersWithEntries.add(new Object[]{user8, 320L});
        unsortedUsersWithEntries.add(new Object[]{user2, 3L});
        unsortedUsersWithEntries.add(new Object[]{user10, 4L});
        unsortedUsersWithEntries.add(new Object[]{user4, 3213L});
        unsortedUsersWithEntries.add(new Object[]{user7, 322L});
        return unsortedUsersWithEntries;
    }

    List<Object[]> prepareNotFullUnsortedListOfUsers() {
        List<Object[]> unsortedUsersWithEntries = new LinkedList<>();
        unsortedUsersWithEntries.add(new Object[]{user6, 1324L});
        unsortedUsersWithEntries.add(new Object[]{user3, 15L});
        unsortedUsersWithEntries.add(new Object[]{user9, 7L});
        unsortedUsersWithEntries.add(new Object[]{user2, 3L});
        unsortedUsersWithEntries.add(new Object[]{user7, 322L});
        return unsortedUsersWithEntries;
    }
}