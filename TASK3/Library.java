import java.util.ArrayList;

public class Library {

    private ArrayList<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book Added Successfully!");
    }

    public void viewBooks() {

        if (books.isEmpty()) {
            System.out.println("No Books Available!");
            return;
        }

        System.out.println("\n===== BOOK LIST =====");

        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void issueBook(int id) {

        for (Book book : books) {

            if (book.getBookId() == id) {

                if (!book.isIssued()) {
                    book.setIssued(true);
                    System.out.println("Book Issued Successfully!");
                } else {
                    System.out.println("Book Already Issued!");
                }
                return;
            }
        }

        System.out.println("Book Not Found!");
    }

    public void returnBook(int id) {

        for (Book book : books) {

            if (book.getBookId() == id) {

                if (book.isIssued()) {
                    book.setIssued(false);
                    System.out.println("Book Returned Successfully!");
                } else {
                    System.out.println("Book Was Not Issued!");
                }
                return;
            }
        }

        System.out.println("Book Not Found!");
    }
} 