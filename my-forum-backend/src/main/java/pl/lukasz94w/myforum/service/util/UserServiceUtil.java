package pl.lukasz94w.myforum.service.util;

import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.model.User;

import java.util.LinkedList;
import java.util.List;

@Component
public class UserServiceUtil {

    public List<Long> prepareNumberOfEntriesInPageableUsers(List<User> listOfLatest10PageableUsers, List<Object[]> foundedNumberOfEntriesInPageableUsers) {
        List<Long> numberOfEntries = new LinkedList<>();

        for (User latestUser : listOfLatest10PageableUsers) {
            numberOfEntries.add(countNumberOfEntriesByUser(latestUser, foundedNumberOfEntriesInPageableUsers));
        }

        return numberOfEntries;
    }

    private Long countNumberOfEntriesByUser(User latestUser, List<Object[]> foundedNumberOfEntriesInPageableUsers) {

        for (Object[] object : foundedNumberOfEntriesInPageableUsers) {
            User user = (User) object[0];
            long numberOfEntriesByUser = (long) object[1];

            if (user.equals(latestUser))
                return numberOfEntriesByUser - 1;
        }

        return 0L;
    }
}
