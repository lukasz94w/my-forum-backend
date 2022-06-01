# my-forum-backend
This repository is the backend part of the internet forum project. Its frontend part is available at: https://github.com/lukasz94w/my-forum-frontend.

The following technologies were used in the project:
- Spring Boot,
- Spring Security,
- Spring Data JPA,
- Spring WebSocket,
- Spring Messaging,
- Hibernate,
- Java,
- JUnit 5,
- Mockito,
- PostgreSQL,
- JWT,
- Maven,
- Lombok.
## Available endpoints
### For everyone:
- creating new account at auth/signUp,
- account login at auth/signIn,
- getting new authorization token at auth/refreshToken,
- resetting password through email at auth/resetPassword and auth/changePassword,
- activating account at auth/activateAccount and auth/resendActivationToken,
- checking ban status of certain user at ban/checkBanStatus/{userName},
- get pageable posts of certain topic at post/findPageablePostsByTopicId,
- search in posts at post/searchInPosts,
- get topic by id at topic/getTopicById/{id},
- get pageable topics by category at topic/findPageableTopicsInCategory,
- count topics and posts by category at topic/countTopicsAndPostsByCategory,
- search in topics at topic/searchInTopicTitles,
- get certain user data at user/getUserInfo/{userName},
- get pageable posts by user at user/findPageablePostsByUser,
- get pageable topics by user at user/findPageableTopicsByUser.

### Only for authorized user (ROLE_USER):
- creating new post at post/addPost,
- creating new topic at topic/addTopic,
- changing profile picture at user/changeProfilePic,
- get profile pic at user/getProfilePic,
- changing password at user/changePassword.

### Only for admin (ROLE_ADMIN):
- ban user at ban/banUser,
- unban user at ban/unBanUser,
- hide (moderate) certain post at post/changeStatus,
- open/close topic at topic/changeStatus,
- delete topic at topic/deleteTopicById/{id},
- get list of pageable users at user/findPageableUsers/{page}. 

## Websocket
### Stomp endpoint for authorized user (ROLE_USER):
- listening messages from admin (name of banned user) at /listener-for-messages-from-admin-actions.
### Message broker for admin (ROLE_ADMIN):
- for push messages to users at topic/from-admin.

## Configuration
After startup, the application is available on port 8080 (localhost:8080). Default configuration is presented below:
```
#jwt authorization token attributes (access token validity - 1 hour, refresh token validity - 24 hours)
pl.lukasz94w.jwtSecret = lukasz94wsecretkey
pl.lukasz94w.jwtAccessTokenExpirationTimeInMs = 3600000
pl.lukasz94w.jwtRefreshTokenExpirationTimeInMs = 86400000

#server address
pl.lukasz94w.serverAddress = http://localhost:4200

#number of pageable items
pl.lukasz94w.pageableItemsNumber = 10

#maximum image upload size
#in the frontend, the limit set when loading the file is 200KB, but the algorithms
#can increase the size, so here with a certain margin the limit was set to 350KB
spring.servlet.multipart.max-file-size = 350KB
spring.servlet.multipart.max-request-size = 350KB

#mail client settings
spring.mail.host = smtp.gmail.com
spring.mail.port = 587
spring.mail.username = ### EMAIL ###
spring.mail.password = ### PASSWORD ### 
spring.mail.properties.mail.smtp.auth = true
spring.mail.properties.mail.smtp.starttls.enable = true
spring.mail.properties.mail.smtp.starttls.required = true
spring.mail.properties.mail.smtp.ssl.trust = smtp.gmail.com
spring.mail.properties.mail.smtp.connectiontimeout = 5000
spring.mail.properties.mail.smtp.timeout = 5000
spring.mail.properties.mail.smtp.writetimeout = 5000

#postgresql settings
spring.datasource.url = jdbc:postgresql://localhost:5432/postgres
spring.datasource.username = postgres
spring.datasource.password = 123
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation = true
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto = create-drop
spring.sql.init.mode = always
spring.jpa.defer-datasource-initialization = true
spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor = org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor

#show hibernate commands
spring.jpa.show-sql = true
```

## Impromevents
It is possible to add new functionalities / improvements to the application, such as:
- add endpoint which allows to delete account,
- secure websocket stomp endpoint /listener-for-messages-from-admin-actions, currently it is not protected in any way and theoretically anyone can connect to it and listen to messages (use JWT for authorization?),
- instead of global stomp endpoint, use queues to be able to pass information about the ban through a private channel to a specific user,
- implement a chat function (using WebSocket) that enables sending messages between users,
- reduce the number of operations while searching for data (TopicServiceUtil and PostServiceUtil class) by adding additional fields in entity objects, e.g. the date of the last activity in the topic,
- write more tests, e.g. checking password reset via email, access and refresh token duration or validating of incoming data in requests.
