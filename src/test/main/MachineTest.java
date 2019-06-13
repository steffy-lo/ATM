package main;

import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static junit.framework.TestCase.assertEquals;

public class MachineTest {
    private Machine atm;

    @Before
    public void setUp() throws IOException {
        atm = new Machine();
        new PrintWriter(new FileWriter(atm.getAlerts())).write(""); // clears the file
    }

    @Test
    public void testWithdrawOneBillType() throws IOException {
        atm.withdraw(100);
        assert(atm.getBills()[3] == 98);
        assert(atm.getBills()[2] == 100);
        assert(atm.getBills()[1] == 100);
        assert(atm.getBills()[0] == 100);
    }

    @Test
    public void testWithdrawMoreThanOneBillType() throws IOException {
        atm.withdraw(185.0);
        assert(atm.getBills()[3] == 97);
        assert(atm.getBills()[2] == 99);
        assert(atm.getBills()[1] == 99);
        assert(atm.getBills()[0] == 99);
    }

    @Test
    public void testWithdrawZeroBalanceOnBillType0() throws IOException {
        atm.setBills(0, 0);
        atm.withdraw(1015); // it will withdraw $1010
        assert(atm.getBills()[3] == 80);
        assert(atm.getBills()[2] == 100);
        assert(atm.getBills()[1] == 99);
    }

    @Test
    public void testWithdrawZeroBalanceOnBillType1() throws IOException {
        atm.setBills(1, 0);
        atm.withdraw(1015);
        assert(atm.getBills()[3] == 80);
        assert(atm.getBills()[2] == 100);
        assert(atm.getBills()[0] == 97);
    }

    @Test
    public void testWithdrawZeroBalanceOnBillType2() throws IOException {
        atm.setBills(2, 0);
        atm.withdraw(1025);
        assert(atm.getBills()[3] == 80);
        assert(atm.getBills()[1] == 98);
        assert(atm.getBills()[0] == 99);
    }

    @Test
    public void testWithdrawZeroBalanceOnBillType3() throws IOException {
        atm.setBills(3, 0);
        atm.withdraw(1015);
        assert(atm.getBills()[2] == 50);
        assert(atm.getBills()[1] == 99);
        assert(atm.getBills()[0] == 99);
    }

    @Test
    public void testDepositOneBillType() throws IOException {
        atm.deposit(1000);
        assert(atm.getBills()[3] == 120);
        assert(atm.getBills()[2] == 100);
        assert(atm.getBills()[1] == 100);
        assert(atm.getBills()[0] == 100);
    }

    @Test
    public void testDepositMoreThanOneBillType() throws IOException {
        atm.deposit(185);
        assert(atm.getBills()[3] == 103);
        assert(atm.getBills()[2] == 101);
        assert(atm.getBills()[1] == 101);
        assert(atm.getBills()[0] == 101);
    }

    @Test
    public void testAlertOneTypes() throws IOException {
        atm.setBills(3, 20);
        atm.setBills(2, 20);
        atm.setBills(1, 20);
        atm.setBills(0, 20);
        atm.withdraw(5);
        BufferedReader br = new BufferedReader(new FileReader(atm.getAlerts()));

        assert(br.readLine().equals("Need to restock $5 bills."));
    }

    @Test
    public void testAlertAllTypes() throws IOException {
        atm.setBills(3, 19);
        atm.setBills(2, 19);
        atm.setBills(1, 19);
        atm.setBills(0, 19);
        atm.withdraw(10.0);
        BufferedReader br = new BufferedReader(new FileReader(atm.getAlerts()));

        assertEquals(br.readLine(),"Need to restock $5 bills.");
        assertEquals(br.readLine(),"Need to restock $10 bills.");
        assertEquals(br.readLine(), "Need to restock $20 bills.");
        assertEquals(br.readLine(), "Need to restock $50 bills.");
    }

    @Test
    public void restock() {
        atm.restock(0, 100);
        atm.restock(1, 100);
        atm.restock(2, 100);
        atm.restock(3, 100);

        assert(atm.getBills()[0] == 200);
        assert(atm.getBills()[1] == 200);
        assert(atm.getBills()[2] == 200);
        assert(atm.getBills()[3] == 200);
    }
}
