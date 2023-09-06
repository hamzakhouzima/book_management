public class Author {
    private String name;
    private int Id ;

    public Author(String name , int id) {
        this.name = name;
        this.Id = id;
    }

    public String getName(){

        return this.name;
    }
     public void setName(String name){
        this.name = name;
     }
     public void setId(int id){
        this.Id = id;
     }
     public int getId(){
        return this.Id;
     }

}
