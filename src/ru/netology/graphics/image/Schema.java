package ru.netology.graphics.image;

public class Schema implements TextColorSchema {
    @Override
    public char convert(int color) {
        char[] symbol = {'#', '$', '@', '%', '*', '+', '-', '\''};
        return symbol[color / (256 / symbol.length)];
    }
}