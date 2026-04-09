# Repository instructions

## Công nghệ và phạm vi
- Xây dựng ứng dụng desktop quản lý thư viện chỉ bằng Java và Java Swing.
- Không dùng C#, .NET, WinForms, WPF, JavaFX, Spring, Hibernate, framework web, ORM hoặc dependency injection.
- Từ "Winform" trong yêu cầu chỉ mô tả kiểu bố cục desktop; hãy triển khai đúng bằng Java Swing.
- Ưu tiên code tối giản, dễ đọc, dễ sửa, phù hợp bài tập lớn quy mô nhỏ.

## Kiến trúc mong muốn
- Tách rõ các phần: `model`, `repository`, `service`, `ui`, `util`.
- `repository` chỉ đọc/ghi file JSON.
- `service` xử lý nghiệp vụ, validation và phân quyền.
- `ui` chỉ xử lý hiển thị và điều hướng, không nhét toàn bộ nghiệp vụ vào form.
- Không tạo pattern phức tạp, abstract class hoặc interface nếu không thật sự cần.

## Giao diện
- Màn hình chính chia 2 phần:
  - bên trái là menu/tác vụ người dùng
  - bên phải là vùng nội dung chính và phải chiếm phần lớn diện tích
- Header luôn hiển thị: `[tên người dùng] - [role]`.
- Ưu tiên dùng `JFrame`, `JPanel`, `JTable`, `DefaultTableModel`, `JOptionPane`, `CardLayout`, `BorderLayout`.
- Dùng `CardLayout` để chuyển màn hình ở vùng nội dung bên phải.
- Toàn bộ label, nút bấm, dialog và thông báo hiển thị bằng tiếng Việt.

## Xác thực và phân quyền
- Bắt buộc có chức năng đăng nhập và đăng xuất.
- `admin` có quyền: xem danh sách sách, thêm sách, sửa sách, xóa sách, tìm kiếm sách.
- `user` có quyền: xem danh sách sách, mượn sách, trả sách.
- Mọi quyền phải được kiểm tra ở tầng `service`, không chỉ dựa vào việc ẩn hoặc hiện nút trên giao diện.
- Không cho phép leo thang đặc quyền.

## Dữ liệu
- `Book` có các trường: `id`, `tenSach`, `soLuong`, `chuDe`, `tacGia`.
- Nếu cần model mượn/trả, dùng model đơn giản như `LoanRecord`.
- Danh sách sách phải sắp xếp alphabet theo `tenSach`.
- Khi hiển thị danh sách, nhóm theo `chuDe`.
- Tạo dữ liệu mẫu khoảng 50 sách với 10 chủ đề khác nhau.
- Nếu file JSON chưa tồn tại, có thể seed dữ liệu mẫu ở lần chạy đầu tiên.

## Lưu trữ JSON
- Dùng file JSON làm nơi lưu dữ liệu chính.
- Không dùng cơ sở dữ liệu SQL hoặc NoSQL.
- Giữ cấu trúc JSON đơn giản, rõ nghĩa, dễ đọc thủ công.
- Dùng path cố định cho file dữ liệu, ví dụ `data/*.json`.
- Các file dữ liệu runtime phải được ignore khỏi git.

## Nghiệp vụ và validation
- Validate toàn bộ input trước khi xử lý và trước khi ghi file.
- Kiểm tra dữ liệu rỗng, độ dài, kiểu số, số lượng âm, ID trùng, sách không tồn tại.
- Khi mượn sách, chỉ cho phép nếu `soLuong > 0`.
- Khi trả sách, cập nhật lại số lượng chính xác.
- Không tạo lệnh, không build file path, không thực thi logic động từ input người dùng.
- Xem mọi input là không tin cậy; tránh các lỗi kiểu injection.

## Bảo mật tài khoản
- Không hardcode mật khẩu thật trong source code.
- Nếu lưu tài khoản trong JSON, dùng `passwordHash`, không lưu plaintext.
- Dữ liệu demo có thể có tài khoản mẫu `admin` và `user`, nhưng phải tách khỏi dữ liệu thật.

## Style code và comment
- Viết code Java ngắn, rõ nghĩa, ít tầng, ít phụ thuộc.
- Ưu tiên class nhỏ, method ngắn, tên biến và tên method dễ hiểu.
- Không dùng cấu trúc dữ liệu cầu kỳ hay thuật toán khó khi không cần thiết.
- Comment bằng tiếng Việt, đặc biệt chi tiết ở phần nghiệp vụ và xử lý dữ liệu.
- Mỗi method nghiệp vụ cần nói rõ: xử lý nghiệp vụ gì, input là gì, output là gì, validate gì, ảnh hưởng dữ liệu ra sao.
- Comment UI boilerplate ngắn gọn hơn comment nghiệp vụ.

## Kỳ vọng khi Copilot sinh code
- Mặc định sinh code Java Swing hoàn chỉnh, không pha công nghệ ngoài phạm vi dự án.
- Khi thêm tính năng, nêu rõ class mới và class cần sửa.
- Ưu tiên solution chạy được ngay, dễ demo, dễ chấm, dễ đọc.