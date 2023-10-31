package ru.netology.graphics.image;

public class ColorSchema2 implements TextColorSchema {
    char[] arr = {'`', '-', '+', '*', '%', '@', '$', '#'};

    @Override
    public char convert(int color) { //  МЕТОД -КОНВЕРТАЦИЯ ЦВЕТА В СИМВОЛ
        char c = arr[(int) Math.floor(color / 32)];
        return c;
    }
}





