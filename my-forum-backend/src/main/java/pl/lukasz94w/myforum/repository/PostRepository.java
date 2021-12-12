package pl.lukasz94w.myforum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTopicId(Long id, Pageable pageable);

    Integer countPostByTopicCategory(Category category);

//    this query will return a List<Map<String, Object>> which can be accessed by name
//    @Query(value = "select new map (count(post.topic.category) as numberOfPosts, post.topic.category as postsCategory) FROM Post post group by post.topic.category")
//    List<Object> countByCategoryList();

//    this query will return a List<CategoriesDto> (automatically mapping), attributes: long count, Category categoryName
//    @Query(value = "select new pl.lukasz94w.myforum.model.CategoriesDto(count(post.topic.category), post.topic.category) FROM Post post group by post.topic.category")
//    List<CategoriesDto> countByCategoryList();

    //this query will return List<Object[]>
    @Query(value =
            "SELECT COUNT (post.topic.category), post.topic.category " +
                    "FROM Post post " +
                    "GROUP BY post.topic.category")
    List<Object[]> countPostsByCategories();

    @Query(value =
            "SELECT post FROM Post post " +
                    "WHERE (post.topic, post.dateTime) " +
                    "IN (SELECT post.topic, MAX(post.dateTime) FROM Post post " +
                    "GROUP BY post.topic " +
                    "HAVING post.topic.category = :category " +
                    "AND post.topic.id IN (:topicIds))")
    List<Post> findLatestPostsInPageableTopics(@Param("topicIds") List<Long> topicIds, @Param("category") Category category);

    @Query(value =
            "SELECT post FROM Post post " +
                    "WHERE (post.topic, post.dateTime) " +
                    "IN (SELECT post.topic, MAX(post.dateTime) FROM Post post " +
                    "GROUP BY post.topic " +
                    "HAVING post.topic.id IN (:topicIds))")
    List<Post> findLatestPostsInEachOfLatestTopics(@Param("topicIds") List<Long> topicIds);

    @Query(value =
            "SELECT post.topic, COUNT(post.topic) " +
                    "FROM Post post " +
                    "WHERE post.topic.id IN :topicIds " +
                    "GROUP BY post.topic")
    List<Object[]> countPostsInPageableTopics(@Param("topicIds") List<Long> topicIds);

    Page<Post> findByUser(User user, Pageable pageable);
}
