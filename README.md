# BTL-Java - Ứng dụng quản lý thư viện (Java Swing)

Ứng dụng desktop quản lý thư viện viết bằng **Java 17 + Swing**, lưu dữ liệu bằng **JSON** (Gson), không dùng database server.

## Công nghệ
- Java 17
- Java Swing
- Maven
- Gson

## Cấu trúc chính
- `model`: Book, User, LoanRecord, Role
- `repository`: đọc/ghi JSON
- `service`: nghiệp vụ, validate, phân quyền
- `ui`: màn hình đăng nhập + màn hình chính + các panel
- `util`: hash mật khẩu, validate, seed dữ liệu mẫu

## Tài khoản demo
- Admin:
  - username: `admin`
  - password: `admin123`
- User:
  - username: `user`
  - password: `user123`

> Mật khẩu được lưu dạng hash SHA-256 + salt trong `data/users.json`, không lưu plaintext.

## Dữ liệu runtime
Ứng dụng tự tạo file dữ liệu khi chạy lần đầu:
- `data/books.json` (50 sách mẫu, 10 chủ đề)
- `data/users.json`
- `data/loans.json`

## Chạy ứng dụng
```bash
mvn clean compile
mvn exec:java
```

## Chức năng
### Admin
- Xem danh sách sách theo chủ đề (JTabbedPane)
- Thêm / sửa / xóa sách
- Tìm kiếm theo tên sách, tác giả, chủ đề

### User
- Xem danh sách sách theo chủ đề (JTabbedPane)
- Mượn sách
- Trả sách
- Tìm kiếm theo tên sách, tác giả, chủ đề

## Ghi chú
- Header luôn hiển thị: `[tên người dùng] - [role]`
- Menu bên trái thay đổi theo quyền đăng nhập
- Toàn bộ thông báo UI dùng tiếng Việt
