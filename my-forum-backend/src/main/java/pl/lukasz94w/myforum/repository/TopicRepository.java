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

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Topic findTopicById(final Long id);

    Page<Topic> findTopicsByCategory(Category category, Pageable pageable);

    //this query will return List<Object[]>
    @Query(value = "select count(topic.category), topic.category FROM Topic topic group by topic.category")
    List<Object[]> countTopicsByCategories();

    //if there are many topics with same max(timeOfActualization) there will be returned all of them!
    @Query(value = "select topic FROM Topic topic WHERE (topic.category, topic.timeOfActualization) IN (select topic.category, max(topic.timeOfActualization) FROM Topic topic group by topic.category)")
    List<Topic> findLatestTopicInEachCategory();

    Page<Topic> findByUser(User user, Pageable pageable);

    Page<Topic> findByTitleContainsIgnoreCase(String title, Pageable pageable);
}