import java.util.Scanner;
import java.sql.*;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Connection;


public class Main {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Scaler@123";


    public static void main(String[] args) throws ClassNotFoundException , SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
//            System.out.println("Driver load successfully");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        // Establish connection
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
//            System.out.println("Connection Establish successfully");
            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room ");
                System.out.println("2. View reservation");
                System.out.println("3. Get room number");
                System.out.println("4. Update reservation");
                System.out.println("5. Delete reservation");
                System.out.println("0. Exit");
                System.out.println();
                System.out.print("CHOOSE ANY OPTION : ");
                Scanner sc = new Scanner(System.in);
                int choose = sc.nextInt();
                System.out.println();

                switch (choose){
                    case 1 : reserveRoom(connection, sc);
                        break;
                    case 2 : ViewRoom(connection);
                        break;
                    case 3 : getRoomNumber(connection, sc);
                        break;
                    case 4 : updateReservation(connection, sc);
                        break;
                    case 5 : deleteReservation(connection, sc);
                        break;
                    case 0 : exit();
                        sc.close();
                        return;
                    default:System.out.println("INVALID INPUT !!! PLEASE ENTER CORRECT INPUT OPERATION - ");
                }
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }


    private static void reserveRoom(Connection connection, Scanner sc) {
        try{
            System.out.println("Please Enter guest details for reservation ");
            System.out.println();
            System.out.print("Enter Guest name : ");
            String guestName  = sc.next();
            sc.nextLine();
            System.out.print("Enter room number : ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter contact number : ");
            String contactNumber  = sc.next();

            String sql = "INSERT INTO reservation( guest_name,room_number,contact_number)"+
                            "VALUES( '" + guestName + "'," + roomNumber +",'" + contactNumber+"')";
            try(Statement statement = connection.createStatement()){
                int affectRow = statement.executeUpdate(sql);

                System.out.println();
                if(affectRow>0) System.out.println("Reservation successfully");
                else System.out.println("Reservation failed");

            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void ViewRoom(Connection connection) throws SQLException{

        String sql = "SELECT * FROM reservation;";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");


            while(resultSet.next()){
                int reservationID = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationID,guestName,roomNumber,contactNumber,reservationDate);

            }
        }
    }

    private static void getRoomNumber(Connection connection, Scanner sc) {
        System.out.print("Enter reservation ID : ");
        int reservationId = sc.nextInt();
        System.out.print("Enter Name : ");
        String guestName = sc.next();

        String sql = "SELECT room_number FROM reservation WHERE reservation_id =" +  reservationId + " && guest_name='" + guestName +  "';";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            if(resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println();
                System.out.println("Reservation ID : " + reservationId);
                System.out.println("Guest name : "+ guestName);
                System.out.print( "Room number : "+ roomNumber);
                System.out.println();
            }
            else{
                System.out.println("Reservation not found for the given ID and guest name.");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }
    private static void updateReservation(Connection connection, Scanner sc){

        try{
            System.out.print("Enter reservation ID to Update : ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if(!reservationExist(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }


            System.out.print(" Enter new guest name :");
            String guestName = sc.nextLine();
            System.out.print(" Enter new room number :");
            int roomNumber = sc.nextInt();
            System.out.print(" Enter new contact number :");
            String contactNumber = sc.next();

            String sql = "UPDATE reservation SET guest_name = '" + guestName +"'," +
                    "room_number = " + roomNumber +"," +
                    "contact_number = '" + contactNumber + "'" +
                    "WHERE reservation_id =  " + reservationId + ";";

            try( Statement statement = connection.createStatement()){
                int affected = statement.executeUpdate(sql);

                if(affected>0) System.out.println(" Successfully Updated");
                else System.out.println("Updation failed ");
            }


        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc){

        try {
            System.out.print("Enter reservation id , You want to delete : ");
            int deleteId = sc.nextInt();

            if(!reservationExist(connection,deleteId)){
                System.out.print("Reservation is not Found for your given id : ");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " + deleteId;

            try(Statement statement = connection.createStatement()){

                int deleteRow = statement.executeUpdate(sql);

                if(deleteRow>0) System.out.print("Deleting reservation successfully.");
                else System.out.print("Deleting reservation failed.");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }

    private static boolean reservationExist( Connection connection, int reservationId){
        try{
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id ="+ reservationId+";";

            try( Statement statement = connection.createStatement();
                  ResultSet resultSet = statement.executeQuery(sql)){
                return resultSet.next();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }

}
