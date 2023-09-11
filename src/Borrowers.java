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

    public static void returnBook(Date returnDate, String isbn, int borrower_number) throws SQLException {
        String returnQ = "UPDATE borrowedbooks SET return_date = ? WHERE Borrowed_book_id = ? AND borrower_member_number = ?;";
        String getBorrowedInstances = "SELECT instance_id FROM bookinstance WHERE status = 'borrowed' AND isbn = ?;";
        String changeBorrowedStatus = "UPDATE bookinstance SET status = 'available' WHERE instance_id = ?; ";
        int borrowedBookid = 0;

        try (Connection connection = DataBase.dbSetup()) {

            // First, execute the getBorrowedInstances code
            try (PreparedStatement getBorrowedStmt = connection.prepareStatement(getBorrowedInstances)) {
                getBorrowedStmt.setString(1, isbn); // Set the ISBN
                ResultSet resultSet = getBorrowedStmt.executeQuery();

                // Process the resultSet and do what you need with it
                // Example: iterate through the results and print them
                while (resultSet.next()) {
                    // Access the retrieved data as needed
                    borrowedBookid = resultSet.getInt("instance_id");
                }
            } catch (SQLException e) {
                System.err.println("Error querying borrowed instances: " + e.getMessage());
            }

            // Update the status of the book instance to 'available'
            try (PreparedStatement changeStatus = connection.prepareStatement(changeBorrowedStatus)) {
                changeStatus.setInt(1, borrowedBookid);
                int rowsUpdated = changeStatus.executeUpdate(); // Use executeUpdate for UPDATE queries
                if (rowsUpdated > 0) {
                    System.out.println("Book status updated to 'available'.");
                } else {
                    System.out.println("No records updated. Book instance not found.");
                }
            } catch (SQLException e) {
                System.err.println("Error updating book status: " + e.getMessage());
            }

            // Now, execute the update query for return date
            try (PreparedStatement preparedStatement = connection.prepareStatement(returnQ)) {
                // Format the return date as needed (e.g., "yyyy-MM-dd")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String returnDateStr = sdf.format(returnDate);

                preparedStatement.setString(1, returnDateStr); // Set the return date
                preparedStatement.setInt(2, borrowedBookid);
                preparedStatement.setInt(3, borrower_number);// Set the Borrowed_book_id

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Return date updated successfully.");
                } else {
                    System.out.println("No records updated. Borrowed_book_id not found.");
                }
            } catch (SQLException e) {
                System.err.println("Error updating return date: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }


    public static void statistics() throws SQLException{
        String statisticsQuery = "SELECT isbn, status, COUNT(*) AS quantity FROM bookinstance GROUP BY isbn, status";

        try (Connection connection = DataBase.dbSetup();
             PreparedStatement preparedStatement = connection.prepareStatement(statisticsQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String status = resultSet.getString("status");
                int quantity = resultSet.getInt("quantity");

                System.out.println("---------------------------------------------------------------------");
                System.out.println("ISBN: " + isbn + ", Status: " + status + ", Quantity: " + quantity);
                System.out.println("---------------------------------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Error executing statistics query: " + e.getMessage());
        }
    }

    public static String getBook(String isbn) {
        String status = null; // Initialize title variable

        // SQL query with a placeholder for ISBN
        String getBookQuery = "SELECT * FROM bookinstance WHERE isbn = ? AND status = ?;";

        try (Connection connection = DataBase.dbSetup();
             PreparedStatement preparedStatement = connection.prepareStatement(getBookQuery)) {

            // Set the ISBN value in the prepared statement
            preparedStatement.setString(1, isbn);
            //preparedStatement.setString(2, statu);


            // Execute the query and retrieve the result set
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                // Retrieve values from the result set
               // String retrievedIsbn = result.getString("isbn");
                 //title = result.getString("title");
                 status = result.getString("status");
                 //int quantity = result.getInt("quantity");

                // Print retrieved values if needed
          /*      System.out.println("ISBN: " + retrievedIsbn);
                System.out.println("Title: " + title);
                System.out.println("Status: " + status);
                System.out.println("Quantity: " + quantity);
                System.out.println("--------------------------");*/
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Return the title
        return status;
    }


}
