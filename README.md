# my-forum-backend
This repository is the backend part of the internet forum project. Its frontend part is available at: https://github.com/lukasz94w/my-forum-frontend.

## Available endpoints:
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
- get pageable topics by user at user/findPageableTopicByUser.

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
- delete certain topic at topic/deleteTopicById/{id},
- get list of pageable users at user/findPageableUsers/{page}. 

## Websocket:
### Stomp endpoint for authorized user (ROLE_USER):
- listening messages from admin at /listener-for-messages-from-admin-actions.
### Message broker for admin (ROLE_ADMIN):
- for push messages to users at topic/from-admin.
