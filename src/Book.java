import javax.print.DocFlavor;
import java.sql.*;
import DB.DataBase;

public class Book {
    //private Author author;
    //private BookInstance BookInstance;
    private Author author_name;
    private String isbn;
    private String title;
    private String status;
    private int quantity;

    public Book(String isbn, String title, String status, int quantity, Author author) {
        this.isbn = isbn;
        this.title = title;
        this.status = status;
        this.quantity = quantity;
        this.author_name = author;
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
    public void add(int numberOfInstance) throws SQLException{
//"add a book" logic
        String book_query = "INSERT INTO books (isbn,title,status,quantity) VALUES (?,?,?,?)";
        String author_query = "INSERT INTO author (name) VALUES (?)";
        String instances_query = "INSERT INTO bookinstance (isbn,status) VALUES (?,?)";
//this method is to add a book in book table
// author in author table
// and also the books instances or copies in the bookinstance table
        try (Connection connection = DataBase.dbSetup()){

            PreparedStatement preparedStatement = connection.prepareStatement(book_query);
            PreparedStatement preparedStatement2 = connection.prepareStatement(author_query);
            PreparedStatement preparedStatement3 = connection.prepareStatement(instances_query);

            preparedStatement.setString(1, this.isbn);
            preparedStatement.setString(2, this.title);
            preparedStatement.setString(3, this.status);
            preparedStatement.setInt(4, this.quantity);

            preparedStatement.executeUpdate();

            preparedStatement2.setString(1 , this.author_name.getName());
            preparedStatement2.executeUpdate();

            for(int i=0 ;i<=numberOfInstance ;i++){
                preparedStatement3.setString(1, this.isbn);
                //this is the status of book instances (the books copies available , not available , lost)
                preparedStatement3.setString(2, this.status);
                preparedStatement3.executeUpdate();
            }

            }catch(SQLException e){
            System.out.println(e);

        }

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
