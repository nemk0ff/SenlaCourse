package controllers;

import java.util.Scanner;

public interface Controller {
    Scanner scanner = new Scanner(System.in);

    Action run();

    Action checkInput();
}
