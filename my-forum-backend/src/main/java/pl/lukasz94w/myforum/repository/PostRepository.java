package pl.lukasz94w.myforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByTopicId(Long id);

    Integer countPostByTopicCategory(Category category);

//    this query will return a List<Map<String, Object>> which can be accessed by name
//    @Query(value = "select new map (count(post.topic.category) as numberOfPosts, post.topic.category as postsCategory) FROM Post post group by post.topic.category")
//    List<Object> countByCategoryList();

//    this query will return a List<CategoriesDto> (automatically mapping), attributes: long count, Category categoryName
//    @Query(value = "select new pl.lukasz94w.myforum.model.CategoriesDto(count(post.topic.category), post.topic.category) FROM Post post group by post.topic.category")
//    List<CategoriesDto> countByCategoryList();

    //this query will return List<Object[]>
    @Query(value = "select count(post.topic.category), post.topic.category FROM Post post group by post.topic.category")
    List<Object[]> countByCategoryList();
}
