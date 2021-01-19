# Spring-RedditClone
- Dự án là bản sao không hoàn chỉnh của https://www.reddit.com/

- Công nghệ sử dụng :
  * Backend :
    + SpingBoot, Spring Data JPA, Spring Security
    + Json Web Token
  * Frontend :
    + Angular : https://github.com/thanhbinhgtv/Angular-RedditClone

- Chức năng :
  + Đăng ký : Xác thực tài khoản qua Mail
  + Đăng nhập : Bảo mật tài khoản bằng JWT
  + Tạo Subreddit
  + Tạo các Post cho Subreddit
  + User comment trong các bài Post
  + User Vote Like hoặc Dislike cho các bài Post
  + Thông tin User (Thống kê các bài Post, Comment của user này)
  + Thống kê các Subreddit
  + Chi tiết bài Post (ai đăng, đăng cách đây bao lâu, số lượt vote, các comment ...)
  
- Lưu ý :
  + Database sẽ được tạo tự động khi run Backend.
  + Cần xác thực tài khoản sau khi đăng ký mới có thể đăng nhập.
  + Mã JWT sẽ được refresh mỗi 15p để tăng hiệu quả bảo mật.
