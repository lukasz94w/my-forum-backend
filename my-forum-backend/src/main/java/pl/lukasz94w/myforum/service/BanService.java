package pl.lukasz94w.myforum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.exception.exception.ForumItemNotFoundException;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;
import pl.lukasz94w.myforum.model.Ban;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.BanRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.BanRequest;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BanService {

    private final UserRepository userRepository;
    private final BanRepository banRepository;

    public void banUser(BanRequest banRequest) {
        User user = checkIfUserExist(banRequest.getUserName());

        // checking if there was any ban before if so clean it from repository
        Ban possibleOldBan = banRepository.findByUser(user);
        if (possibleOldBan != null) {
            user.setBan(null);
            banRepository.delete(possibleOldBan);
        }

        // user is banned until end of given date
        LocalDateTime dateAndTimeOfBan = banRequest.getDateOfBan().atTime(23, 59, 59);
        Ban userBan = new Ban(user, banRequest.getReasonOfBan(), dateAndTimeOfBan);
        user.setBan(userBan);
        banRepository.save(userBan);
    }

    public void unBanUser(String userName) {
        User user = checkIfUserExist(userName);

        Ban currentBan = banRepository.findByUser(user);
        user.setBan(null);
        banRepository.delete(currentBan);
    }

    public boolean checkBanStatus(String userName) {
        return checkIfUserExist(userName).isBanned();
    }

    private User checkIfUserExist(String userName) {
        return userRepository
                .findUserByName(userName)
                .orElseThrow(() -> new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST));
    }
}
