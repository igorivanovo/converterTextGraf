package ru.netology.graphics.image;

public class ColorSchema implements TextColorSchema {
    char[] arr = {'#', '$', '@', '%', '*', '+', '-', '`'};

    @Override
    public char convert(int color) { //  МЕТОД -КОНВЕРТАЦИЯ ЦВЕТА В СИМВОЛ
        // char[] arr = { '#', '$', '@', '%', '*', '+', '-','`'};
        char c = arr[(int) Math.floor(color / 32)];

        return c;
    }
}
