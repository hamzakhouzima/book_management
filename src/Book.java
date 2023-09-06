import javax.print.DocFlavor;
import java.sql.*;
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





    //operations methods
   /* public void add(int numberOfInstance) throws SQLException{
//"add a book" logic
        String book_query = "INSERT INTO book (isbn,Title,author_id,status,quantity) VALUES (?,?,?,?,?)";
        String author_query = "INSERT INTO authors (name) VALUES (?)";
        //String author_id = "SELECT * FROM authors WHERE name = ? ";
        String instances_query = "INSERT INTO bookinstance (isbn,status) VALUES (?,?)";
//this method is to add a book in book table
// author in author table
// and also the books instances or copies in the bookinstance table
        try (Connection connection = DataBase.dbSetup()){

            PreparedStatement preparedStatement = connection.prepareStatement(book_query); //this is to add the book informations
           // PreparedStatement catch_author_id = connection.prepareStatement(author_id);
            PreparedStatement preparedStatement2 = connection.prepareStatement(author_query);
            PreparedStatement preparedStatement3 = connection.prepareStatement(instances_query);

            preparedStatement.setString(1, this.isbn);
            preparedStatement.setString(2, this.title);
            preparedStatement.setInt(3, this.author_id);
            preparedStatement.setString(4, this.status);
            preparedStatement.setInt(5, this.quantity);

            //catch_author_id.setInt(1, this.quantity);




            preparedStatement2.setString(1 , this.author_name.getName());

            preparedStatement2.executeUpdate();

            preparedStatement.executeUpdate();

            for(int i=0 ;i<=numberOfInstance ;i++){
                preparedStatement3.setString(1, this.isbn);
                //this is the status of book instances (the books copies available , not available , lost)
                preparedStatement3.setString(2, this.status);
                preparedStatement3.executeUpdate();
            }

            }catch(SQLException e){


                System.err.println("Error adding the book: " + e.getMessage());


        }

    }*/
    public void add(int numberOfInstance) throws SQLException {
        String book_query = "INSERT INTO book (isbn, title, author_id, status, quantity) VALUES (?, ?, ?, ?, ?)";
        String author_query = "INSERT INTO authors (name) VALUES (?)";
        String instances_query = "INSERT INTO bookinstance (isbn, status) VALUES (?, ?)";

        try (Connection connection = DataBase.dbSetup()) {
            int authorId = getAuthorIdByName( this.author_name.getName());

            if (authorId == -1) {
                // If the author doesn't exist, insert them into the authors table
                PreparedStatement authorStatement = connection.prepareStatement(author_query, Statement.RETURN_GENERATED_KEYS);
                authorStatement.setString(1, this.author_name.getName());
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




    public static void displayBookList(){
        //display the books
    }
    public static void searchBook(){
        //search books
    }
    public static void deleteBook(){
        //delete books
    }

    public static void updateBook(){
        //update books
    }
    public static void showBorrowed(){
        //show borrowed books
    }




}
