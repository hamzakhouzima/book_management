public class BookInstance  {
    private int instanceQuantity;
    private String instanceStatus;

    private Book book;

    public BookInstance(Book book, int instanceQuantity, String instanceStatus) {
        this.book = book;
        this.instanceQuantity = instanceQuantity;
        this.instanceStatus = instanceStatus;
    }
    public void setBook(Book book){
         this.book = book;
    }
    public Book getBook(){
        return book;
    }



    public int getInstanceQuantity(){
        return this.instanceQuantity;
    }
    public String getInstanceStatus(){
        return this.instanceStatus;
    }


    public void setInstanceQuantity(int instanceQuantity){
        this.instanceQuantity=instanceQuantity;
    }
    public void setInstanceStatus(String instanceStatus){
        this.instanceStatus=instanceStatus;

    }



}
