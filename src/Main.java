import java.util.Scanner;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Menu:");
            System.out.println("1. Add a Book");
            System.out.println("2. Delete book by ISBN");
            System.out.println("3. Update book by ISBN");
            System.out.println("4. Search book ");
            System.out.println("5. Show Books");

            System.out.println("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    try {
                        addBook(scanner); // Call a method to handle adding a book
                        System.out.println("Book added successfully.");
                    } catch (SQLException e) {
                        System.err.println("Error adding the book: " + e.getMessage());
                    }
                    break;
                case 2:
                    deleteBookl(scanner);
                    System.out.println("Book has been deleted ");
                    break;
                case 3:
                    System.out.println("Book has been updated");
                    updateBook(scanner);
                    break;

                case 4:
                    ShowBooks();

                    break;
                case 5:
                    searchBook(scanner);
                    break;
                case 6:
                    System.out.println("Exiting the program.");

                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 2);

        scanner.close();
    }

    public static void addBook(Scanner scanner) throws SQLException {
        // Prompt the user for book information (ISBN, title, author, etc.)
        // Create a Book object and set its attributes based on user input
        // Call the add method of the Book object to add it to the database

        System.out.println("enter isbn :");
        String isbn = scanner.nextLine();

        System.out.println("enter title :");
        String title = scanner.nextLine();

        System.out.println("enter author name :");
        String author_name = scanner.nextLine();

        System.out.println("enter status");
        String status = scanner.nextLine();

        System.out.println("enter quantity");
        int quantity = scanner.nextInt();

        int author_id = Book.getAuthorIdByName(author_name);

        Author author = new Author(author_name,author_id);
        Book book = new Book(isbn,title,status,quantity,author, author_id);
        //BookInstance copy = new BookInstance();

        book.add(quantity);

    }
public static void deleteBookl(Scanner scanner) throws SQLException {
    System.out.println("enter isbn :");
    String isbn = scanner.nextLine();

    Book.deleteBook(isbn);
}

public static void updateBook(Scanner scanner)  throws SQLException{
    System.out.println("enter the book's isbn you wanna update :");
    String isbn = scanner.nextLine();



/*    System.out.println("enter isbn :");
    String isbn = scanner.nextLine();*/

    System.out.println("enter title :");
    String title = scanner.nextLine();

    System.out.println("enter author name :");
    String author_name = scanner.nextLine();

    System.out.println("enter status");
    String status = scanner.nextLine();

    System.out.println("enter quantity");
    int quantity = scanner.nextInt();

    int author_id = Book.getAuthorIdByName(author_name);

     Author author = new Author(author_name,author_id);
    Book book = new Book(isbn,title,status,quantity,author, author_id);
    //BookInstance copy = new BookInstance();

    book.updateBook(isbn , title , author_name , status  ,quantity);


}

public static void ShowBooks() throws SQLException{
        System.out.println("------------------------------------------------------------");
            Book.displayBookList();
        System.out.println("------------------------------------------------------------");

}

public static void searchBook(Scanner scanner) throws SQLException{

    System.out.println("Search here (title || author) :");
    String title = scanner.nextLine();
    Book.searchBook(title);




}


}