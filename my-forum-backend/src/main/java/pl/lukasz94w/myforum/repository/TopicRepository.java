package pl.lukasz94w.myforum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findById(final Long id);

    Page<Topic> findTopicsByCategory(Category category, Pageable pageable);

    @Query(value = "SELECT COUNT(topic.category), MIN(topic.category) FROM Topic topic GROUP BY topic.category")
    List<Object[]> countTopicsByCategories();

    //if there are many topics with same max(timeOfActualization) there will be returned all of them!
    @Query(value = "SELECT topic FROM Topic topic WHERE (topic.category, topic.timeOfActualization) IN (SELECT topic.category, MAX(topic.timeOfActualization) FROM Topic topic GROUP BY topic.category)")
    List<Topic> findLatestTopicInEachCategory();

    Page<Topic> findByUser(User user, Pageable pageable);

    Page<Topic> findByTitleContainsIgnoreCase(String title, Pageable pageable);
}