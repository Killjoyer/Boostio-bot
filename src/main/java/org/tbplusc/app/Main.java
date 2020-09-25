package org.tbplusc.app;

public class Main {
    public String mergeStrings(String[] args) {
        var output = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            output.append(args[i]);
        }
        return output.toString();
    }

    public static void main(String[] args) {
        System.out.println("Hello, world!");
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }
}
