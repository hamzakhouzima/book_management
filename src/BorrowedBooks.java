import DB.DataBase;

import java.util.Date;
import java.sql.*;


public class BorrowedBooks {
    private BookInstance book_instance;
    private Borrowers borrower;
    private Date borrow_date;
    private Date limit_date;

    public void setBorrow_date(Date borrow_date){
        this.borrow_date = borrow_date ;
    }
    public void setLimit_date(Date limit_date){
        this.limit_date = limit_date;
    }
    public void setBook_instance(BookInstance book_instance){
        this.book_instance = book_instance;
    }
    public void setBorrower(Borrowers borrower){
        this.borrower = borrower;

    }

    public Date getBorrow_date(){
        return this.borrow_date;
    }
    public Date getLimit_date(){
        return this.limit_date;
    }
    public BookInstance getBook_instance(){
        return this.book_instance;
    }

    public Borrowers getBorrower() {
        return borrower;
    }

    public void availableBooks(){

    }


    public void borrowBook(){

    }






}
