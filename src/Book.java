import javax.print.DocFlavor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DB.DataBase;

public class Book {
    //private Author author;
    //private BookInstance BookInstance;
    private Author author_name;
    private int author_id;
    private String isbn;
    private String title;
    private String status;
    private int quantity;

    public Book(String isbn, String title, String status, int quantity, Author author , int author_id) {
        this.isbn = isbn;
        this.title = title;
        this.status = status;
        this.quantity = quantity;
        this.author_name = author;
        this.author_id = author_id;
    }
    //I think this line isn't the correct way to describe this relationship , i'll consider another appr


//getters
    public  String getIsbn(){
        return  this.isbn;
    }

    public String getTitle(){
        return this.title;
    }
    public String getStatus(){
        return this.status;
    }
    public int getQuantity(){
        return this.quantity;

    }
    public Author getAuthor(){
        return this.author_name;
    }

  //setters
  public void setIsbn(String isbn){
        this.isbn = isbn;
  }
  public void setTitle(String title ){
        this.title = title ;
  }
  public void setStatus(String status){
        this.status = status;
  }

  public void setQuantity(int quantity){
        this.quantity = quantity;
  }


    public void add(int numberOfInstance) throws SQLException {
        String book_query = "INSERT INTO book (isbn, title, author_id, status, quantity) VALUES (?, ?, ?, ?, ?)";
        String author_query = "INSERT INTO authors (name) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = ?)";
        String instances_query = "INSERT INTO bookinstance (isbn, status) VALUES (?, ?)";

            try (Connection connection = DataBase.dbSetup()) {
            int authorId = getAuthorIdByName( this.author_name.getName());

            if (authorId == -1) {
                PreparedStatement authorStatement = connection.prepareStatement(author_query, Statement.RETURN_GENERATED_KEYS);
                authorStatement.setString(1, this.author_name.getName());
                authorStatement.setString(2, this.author_name.getName());
                authorStatement.executeUpdate();

                ResultSet authorKeys = authorStatement.getGeneratedKeys();
                if (authorKeys.next()) {
                    authorId = authorKeys.getInt(1);
                }
            }

            PreparedStatement preparedStatement = connection.prepareStatement(book_query);
            PreparedStatement preparedStatement3 = connection.prepareStatement(instances_query);

            preparedStatement.setString(1, this.isbn);
            preparedStatement.setString(2, this.title);
            preparedStatement.setInt(3, authorId);
            preparedStatement.setString(4, this.status);
            preparedStatement.setInt(5, this.quantity);

            preparedStatement.executeUpdate();

            for (int i = 1; i <= numberOfInstance; i++) {
                preparedStatement3.setString(1, this.isbn);
                preparedStatement3.setString(2, this.status);
                preparedStatement3.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error adding the book: " + e.getMessage());
        }
    }



    public static int getAuthorIdByName(String authorName) throws SQLException {
        int authorId = -1;
        String query = "SELECT id FROM authors WHERE name = ?";

        try (Connection connection = DataBase.dbSetup();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, authorName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                authorId = resultSet.getInt("id");
                System.out.println(authorId);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching author's ID: " + e.getMessage());
        }

        return authorId;
    }




    public static void displayBookList() throws SQLException {
        String display_books = "SELECT book.*, authors.name AS author_name FROM book INNER JOIN authors ON book.author_id = authors.id ;\n";
        try (
                Connection connection = DataBase.dbSetup();
                PreparedStatement displayStatement = connection.prepareStatement(display_books) ) {

            ResultSet result = displayStatement.executeQuery();
            while(result.next()){
                String isbn = result.getString("isbn");
                String title = result.getString("title");
                String status = result.getString("status");
                int quantity = result.getInt("quantity");
                String authorName = result.getString("author_name");

                System.out.println("ISBN: " + isbn);
                System.out.println("Title: " + title);
                System.out.println("Status: " + status);
                System.out.println("Quantity: " + quantity);
                System.out.println("Author Name: " + authorName);

                System.out.println("--------------------------");
            }


        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }





    }



    //this method is to search for a book  by the book title
    public static void searchBook(String searchQ) throws SQLException{
        //search books
        String searchBytitle = "SELECT * FROM book WHERE title = (?) ";


        try(Connection connection = DataBase.dbSetup();
            PreparedStatement preparedStatement = connection.prepareStatement(searchBytitle)) {
            preparedStatement.setString(1,searchQ);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                String isbn = result.getString("isbn");
                String title = result.getString("title");
                String status = result.getString("status");
                int quantity = result.getInt("quantity");

                System.out.println("ISBN: " + isbn);
                System.out.println("Title: " + title);
                System.out.println("Status: " + status);
                System.out.println("Quantity: " + quantity);
                System.out.println("--------------------------");
            }
        }catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    //this method is to search for a book by author name
    public static void searchBookByAuthor(String searchQ) throws SQLException {
        String searchByAuthorId = "SELECT id FROM authors WHERE name = ?";
        String searchByAuthor = "SELECT * FROM book WHERE author_id = ?";

        try (Connection connection = DataBase.dbSetup();
             PreparedStatement authorIdStatement = connection.prepareStatement(searchByAuthorId);
             PreparedStatement bookStatement = connection.prepareStatement(searchByAuthor)) {

            authorIdStatement.setString(1, searchQ);
            ResultSet authorIdResult = authorIdStatement.executeQuery();

            if (authorIdResult.next()) {
                int authorId = authorIdResult.getInt("id");

                bookStatement.setInt(1, authorId);
                ResultSet bookResult = bookStatement.executeQuery();

                while (bookResult.next()) {
                    String isbn = bookResult.getString("isbn");
                    String title = bookResult.getString("title");
                    String status = bookResult.getString("status");
                    int quantity = bookResult.getInt("quantity");

                    System.out.println("ISBN: " + isbn);
                    System.out.println("Title: " + title);
                    System.out.println("Status: " + status);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("--------------------------");
                }
            } else {
                System.out.println("Author not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    //searchByAuthor isn't complete yet
    public static void deleteBook(String isbn ) throws SQLException{
        String check = "SELECT * FROM book WHERE isbn = (?)";
        String DeleteQ = "DELETE FROM book WHERE isbn = (?)";
        try(Connection connection = DataBase.dbSetup();
            PreparedStatement preparedStatement = connection.prepareStatement(DeleteQ))
        {
         preparedStatement.setString(1,isbn);
         int rowsDeleted = preparedStatement.executeUpdate();
         if (rowsDeleted >0){
             System.out.println("Book "+isbn+"has been deleted");
        }
         else{
             System.out.println("Operation failed ");
         }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }



    }

    public static void updateBook(String isbn, String title, String authorName, String status, int quantity) {
        try (Connection connection = DataBase.dbSetup()) {
            // Start a transaction
            connection.setAutoCommit(false);

            // Update the book table
            String updateBookQuery = "UPDATE book SET title = ?, status = ?, quantity = ? WHERE isbn = ?";
            try (PreparedStatement bookStatement = connection.prepareStatement(updateBookQuery)) {
                bookStatement.setString(1, title);
                bookStatement.setString(2, status);
                bookStatement.setInt(3, quantity);
                bookStatement.setString(4, isbn);

                int rowsUpdated = bookStatement.executeUpdate();

                if (rowsUpdated <= 0) {
                    // If no rows were updated, rollback the transaction and return
                    connection.rollback();
                    System.out.println("No book with ISBN " + isbn + " found for update.");
                    return;
                }
            }

            // Update the author table
            String updateAuthorQuery = "UPDATE authors SET name = ? WHERE id = " +
                    "(SELECT author_id FROM book WHERE isbn = ?)";
            try (PreparedStatement authorStatement = connection.prepareStatement(updateAuthorQuery)) {
                authorStatement.setString(1, authorName);
                authorStatement.setString(2, isbn);

                authorStatement.executeUpdate();
            }

            connection.commit();
            System.out.println("Book with ISBN " + isbn + " and its author updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());

          /*  try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                System.err.println("Rollback error: " + rollbackException.getMessage());
            }*/
        }
    }






    }





