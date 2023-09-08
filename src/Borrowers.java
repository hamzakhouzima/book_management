import DB.DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Borrowers {
    private String member_number;
    private String member_name;
    private String contact_information;

    public String getMember_number(){
        return this.member_number;
    }
    public String getMember_name(){
        return this.member_name;

    }
    public String getContact_information(){
        return this.contact_information;
    }



    public static void borrowBook(String isbn,  String name, String contact, Date borrowDate,  Date limitDate , int Member_number) throws SQLException {
        int borrowerID = 0;
        try (Connection connection = DataBase.dbSetup()) {
            // Step 1: Fetch book information by ISBN
            String fetchBookQuery = "SELECT * FROM book WHERE isbn = ?";
            try (PreparedStatement bookStatement = connection.prepareStatement(fetchBookQuery)) {
                bookStatement.setString(1, isbn);
                try (ResultSet bookResult = bookStatement.executeQuery()) {
                    if (bookResult.next()) {
                        String bookTitle = bookResult.getString("title");

                        // Step 2: Fetch available instances of the book
                        String fetchInstancesQuery = "SELECT * FROM bookinstance WHERE isbn = ? AND status = 'available'";
                        try (PreparedStatement instancesStatement = connection.prepareStatement(fetchInstancesQuery)) {
                            instancesStatement.setString(1, isbn);
                            try (ResultSet instancesResult = instancesStatement.executeQuery()) {
                                if (instancesResult.next()) {
                                    int instanceId = instancesResult.getInt("instance_id");

                                    // Step 3: Insert borrower information into the borrower table
                                    String insertBorrowerQuery = "INSERT INTO borrowers (Member_number,name, contact_info) VALUES (?, ?, ?)";
                                    try (PreparedStatement borrowerStatement = connection.prepareStatement(insertBorrowerQuery)) {
                                        borrowerStatement.setInt(1, Member_number);
                                        borrowerStatement.setString(2, name);
                                        borrowerStatement.setString(3, contact);


                                        borrowerStatement.executeUpdate();
                                    }

                                    // Step 4: Update the book instance to mark it as borrowed
                                    String updateInstanceQuery = "UPDATE bookinstance SET status = 'borrowed' WHERE instance_id = ?";
                                    try (PreparedStatement updateInstanceStatement = connection.prepareStatement(updateInstanceQuery)) {
                                        updateInstanceStatement.setInt(1, instanceId);
                                        updateInstanceStatement.executeUpdate();
                                    }
                                    //getting the borrower id to fill the borrowedBook
                                    String getBorrowerIDQuery = "SELECT member_number FROM borrowers WHERE name = ?";
                                    try (PreparedStatement getBorrowerIDStatement = connection.prepareStatement(getBorrowerIDQuery)) {
                                        getBorrowerIDStatement.setString(1, name);

                                        try (ResultSet borrowerIDResult = getBorrowerIDStatement.executeQuery()) {
                                            if (borrowerIDResult.next()) {
                                                 borrowerID = borrowerIDResult.getInt("member_number");
                                                // Now you have the borrower's ID
                                            } else {
                                                // Handle the case where no matching borrower was found
                                                System.out.println("No user existed");
                                            }
                                        }
                                    } catch (SQLException e) {
                                        System.err.println("Error: " + e.getMessage());
                                    }


                                    // Step 5: Insert borrowed book info into borrowed_books table
                                    String insertBorrowedBookQuery = "INSERT INTO borrowedbooks (Borrowed_book_id, borrower_member_number, borrow_date, limit_date) VALUES (?, ?, ?, ?)";
                                    try (PreparedStatement insertBorrowedBookStatement = connection.prepareStatement(insertBorrowedBookQuery)) {
                                        insertBorrowedBookStatement.setInt(1, instanceId);
                                        insertBorrowedBookStatement.setInt(2, borrowerID);
                                        insertBorrowedBookStatement.setDate(3, new java.sql.Date(borrowDate.getTime()));
                                        insertBorrowedBookStatement.setDate(4, new java.sql.Date(limitDate.getTime()));
                                       // insertBorrowedBookStatement.setDate(5, new java.sql.Date(returnDate.getTime()));
                                        insertBorrowedBookStatement.executeUpdate();
                                    }

                                    System.out.println("Book '" + bookTitle + "' with ISBN '" + isbn + "' has been borrowed successfully.");
                                } else {
                                    System.out.println("No available instances of the book with ISBN '" + isbn + "' are found.");
                                }
                            }
                        }
                    } else {
                        System.out.println("Book with ISBN '" + isbn + "' not found.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void returnBook(int memberNumber, Date returnDate) {
        String returnQ = "UPDATE borrowers SET return_date = ? WHERE Member_number = ?";
        String returned_book_id = "SELECT Borrowed_book_id FROM borrowers WHERE borrower_member_number = (?) ";

        try (Connection connection = DataBase.dbSetup();
             PreparedStatement preparedStatement = connection.prepareStatement(returnQ)) {

            // Format the return date as needed (e.g., "yyyy-MM-dd")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String returnDateStr = sdf.format(returnDate);

            preparedStatement.setString(1, returnDateStr); // Set the return date
            preparedStatement.setInt(2, memberNumber); // Set the member number

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Return date updated successfully.");
            } else {
                System.out.println("No records updated. Member number not found.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating return date: " + e.getMessage());
        }
    }
}
