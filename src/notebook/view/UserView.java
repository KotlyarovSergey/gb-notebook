package notebook.view;

import notebook.model.User;

import java.util.List;
import java.util.Scanner;

public class UserView {

    public UserView(){

    };


    public String prompt(String message) {
        Scanner in = new Scanner(System.in);
        System.out.print(message);
        return in.nextLine();
    }

    public void showMessage(String msg){
        System.out.println(msg);
    }




}
