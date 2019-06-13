package users;

import main.Machine;

/**
 * A Manager assist who can only undo the last transaction and can only help the bank manager to restock the machine when
 * the amount of any denomination is below 10.
 */
public class ManagerAssist extends Customer {

    public ManagerAssist(String name, String pw, String securityQ, String securityA){
        super(name, pw, securityQ, securityA);
    }


    public void emergentRestock(Machine machine) {
        int[] current_bills = machine.getBills();
        if (current_bills[0] < 10){
            machine.restock(current_bills[0], 20);
        }
        if (current_bills[1] < 10){
            machine.restock(current_bills[1], 20);
        }
        if (current_bills[2] < 10){
            machine.restock(current_bills[2], 20);
        }
        if (current_bills[3] < 10){
            machine.restock(current_bills[3],20);
        }
    }
}

