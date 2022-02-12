package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.model.Ban;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.BanRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.BanRequest;

import java.time.LocalDateTime;

@Service
public class BanService {

    private final UserRepository userRepository;
    private final BanRepository banRepository;

    @Autowired
    public BanService(UserRepository userRepository,
                       BanRepository banRepository) {
        this.userRepository = userRepository;
        this.banRepository = banRepository;
    }

    public ResponseEntity<HttpStatus> banUser(BanRequest banRequest) {
        User user = userRepository.findByName(banRequest.getUserName());

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

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> unBanUser(String userName) {
        User user = userRepository.findByName(userName);

        Ban currentBan = banRepository.findByUser(user);
        user.setBan(null);
        banRepository.delete(currentBan);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean checkBanStatus(String userName) {
        User user = userRepository.findByName(userName);
        return user.isBanned();
    }
}
