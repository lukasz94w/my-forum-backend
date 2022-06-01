package pl.lukasz94w.myforum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTopicId(Long id, Pageable pageable);

    @Query(value =
            "SELECT COUNT (post.topic.category), MIN(post.topic.category) " +
                    "FROM Post post " +
                    "GROUP BY post.topic.category")
    List<Object[]> countPostsByCategories();

    @Query(value =
            "SELECT post FROM Post post " +
                    "WHERE (post.topic, post.dateTime) " +
                    "IN (SELECT post.topic, MAX(post.dateTime) FROM Post post " +
                    "GROUP BY post.topic " +
                    "HAVING post.topic.id IN (:topicIds))")
    List<Post> findLatestPostsInEachOfLatestTopics(@Param("topicIds") List<Long> topicIds);

    @Query(value =
            "SELECT MIN(post.topic), COUNT(post.topic) " +
                    "FROM Post post " +
                    "WHERE post.topic.id IN :topicIds " +
                    "GROUP BY post.topic")
    List<Object[]> countPostsInPageableTopics(@Param("topicIds") List<Long> topicIds);

    Page<Post> findByUser(User user, Pageable pageable);

    Integer countPostByTopic(Topic topic);

    Page<Post> findByContentContainsIgnoreCase(String content, Pageable pageable);
}
