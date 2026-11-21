package rideflow.app;

import java.util.Scanner;
public final class Main {
    private Main() { }
    public static void main(String[] args) {
        new ConsoleMenu(AppContext.withDemoData(), new Scanner(System.in)).start();
    }
}
