package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupModel {

    private final DatabaseConnectionModel dbConnection;

    // Constructor khởi tạo với DatabaseConnectionModel
    public GroupModel() {
        this.dbConnection = new DatabaseConnectionModel();
        dbConnection.connect();
    }

    // Phương thức để tạo một nhóm mới
    public boolean createGroup(String groupName, String ownerEmail) {
        if (groupExists(groupName)) {
            System.out.println("Nhóm đã tồn tại.");
            return false;
        }

        String query = "INSERT INTO groups (group_name, owner_email) VALUES (?, ?)";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, groupName);
            stmt.setString(2, ownerEmail);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Tạo nhóm thành công: " + groupName);
                return true;
            } else {
                System.out.println("Tạo nhóm thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo nhóm.");
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức để kiểm tra nhóm đã tồn tại chưa
    public boolean groupExists(String groupName) {
        String query = "SELECT * FROM groups WHERE group_name = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Trả về true nếu nhóm đã tồn tại
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra sự tồn tại của nhóm.");
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức thêm thành viên vào nhóm
    public boolean addMemberToGroup(String groupName, String memberEmail) {
        String query = "INSERT INTO group_members (group_name, member_email) VALUES (?, ?)";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, groupName);
            stmt.setString(2, memberEmail);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Thêm thành viên thành công vào nhóm: " + groupName);
                return true;
            } else {
                System.out.println("Thêm thành viên vào nhóm thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thành viên vào nhóm.");
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức để lấy danh sách thành viên trong nhóm
    public List<String> getGroupMembers(String groupName) {
        List<String> members = new ArrayList<>();
        String query = "SELECT member_email FROM group_members WHERE group_name = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                members.add(rs.getString("member_email"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thành viên của nhóm.");
            e.printStackTrace();
        }
        return members;
    }

    // Ngắt kết nối cơ sở dữ liệu khi không cần thiết
    public void close() {
        dbConnection.disconnect();
    }
}
