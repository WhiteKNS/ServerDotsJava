package com.company.ServerDots;

/**
 * Created by Natalya on 0/0/2015.
 */

import java.util.EmptyStackException;

import java.util.Scanner;
import java.util.Stack;


public class Player {
    public int ChoiseLines, ChoiseColumns;
    Scanner scanner;
    Main m;
    boolean Flag;


    Player(){
        m = new Main();
        scanner = new Scanner(System.in);
        Field2 = new int[Main.LINE][Main.COLUMN];
        for (int i=0; i<Main.LINE; i++){
            for(int j=0; j<Main.COLUMN; j++)
            {
                Field2[i][j] =0;

            }

            stackField= new Stack();
            stackX = new Stack();
            stackY = new Stack();
        }

    }


    private int FCurrentField[][] = new int[Main.LINE][Main.COLUMN];

    public void Draw(){
        for (int i=0; i<Main.LINE; i++, System.out.println()){
            for(int j=0; j<Main.COLUMN; j++)
            {
                System.out.print(FCurrentField[i][j]);
            }
        }

    }


    int StartX, StartY;//запоминаем точки, с которых начали, для сравнения! Запомнить их!
    int[][] Field2; //поле для возврата на шаг назад
    int Point=0;
    boolean FlagEnd = false;
    Stack stackField ; //стек с сохраненными предыдущими ходами
    Stack stackX;
    Stack stackY;


    int counter =0;
    int PointsForPlayer1=0, PointsForPlayer2=0;

    void GetPoints(){
        PointsForPlayer1=0;
        PointsForPlayer2=0;
        for (int i=0; i<Main.LINE; i++){
            for(int j=0; j<Main.COLUMN; j++)
            {
                if (Main.Field[i][j]==6) {PointsForPlayer1++;System.out.println(PointsForPlayer1);}
                if (Main.Field[i][j]==7) {PointsForPlayer2++;System.out.println(PointsForPlayer2);}

            }
        }
    }


    void Function(int i, int FirstColumn, int SecondColumn){
        for (int k = FirstColumn+1; k<=SecondColumn-1; k++){
            if((SecondColumn - FirstColumn)<=1) { break;}
            if (Main.Field[i][k]!=4) {
                if (Flag == true) {
                    Main.Field[i][k] = 6;
                }
                if (Flag == false) {
                    Main.Field[i][k] = 7;
                }
            }
        }
        GetPoints();

    }


    private void Search(int[][] currentField, int player){ //поиск захвата

        int l =0;
        int FirstColumn, SecondColumn;
        int Col1=0;

        for (int i=0; i<Main.LINE; i++){
            for (int j=0; j<Main.COLUMN; j++){

                if ((j+1)==Main.COLUMN&&(l==1)){
                    l=0;
                }
                if (currentField[i][j]==4){
                    l++;
                    if (l==1){
                        Col1=j;
                    }
                    if (l==2){
                        SecondColumn = j;   l = 0;
                        FirstColumn=Col1;
                        if ((SecondColumn - FirstColumn) <=1) {FirstColumn = SecondColumn;l=1;  }
                        else {Function(i, FirstColumn, SecondColumn);
                            FirstColumn = SecondColumn; l=1;
                        }
                    }
                }

            }
        }

    }



    //алгоритм поиска соседей.
    boolean SearchNeighbour(int[][]currentField, int x, int y, int player) {// поиск соседней точки , player номер игрока(1 для 1го, 2 для 2го)

        Point=0;
        if (currentField[x][y] != 5) currentField[x][y] = 4; //помечаем точку,в которой находимся, цифрой 4
        else currentField[x][y] = 5; //если точка уже 5, то она уже не может перейти в состояние "текущая"
        FlagEnd = false;


        for (int i = x - 1; i <= x + 1; i++) {
            if (i >= Main.LINE || i < 0) continue;
            for (int j = y - 1; j <= y + 1; j++) {
                if (j >= Main.COLUMN || j < 0) continue;

                if (currentField[i][j] == player) {     //если точка принадлежит игроку

                    stackField.push(currentField); //помещаем текущее поле в стек
                    stackX.push(x); //помещаем текущие координаты в стек
                    stackY.push(y);
                    FCurrentField=currentField;
                    SearchNeighbour(currentField, i, j, player); //рекурсивно вызываем опять тот же поиск
                }

            }
        }

        counter = 0;

        if (FlagEnd==true) return true;

        if (!SearchNeighboar1(currentField, x, y, player) && counter >= 2 && FlagPlayer == true) {
            Point=0;
            for (int k1 = 0; k1 < Main.LINE; k1++) { //если вокруг текущей точки есть начальная, то можем подсчитать количество 4 - это будет кол-вом очков
                for (int l1 = 0; l1 < Main.COLUMN; l1++) {
                    FlagPlayer = false;
                    if (currentField[k1][l1] == 4) {
                        ++Point;
                        FlagEnd = true;
                        System.out.println("Point++   " + Point);
                    }

                }
            }
            Search(currentField, player);
            return true;


        }
        else
        if (!SearchNeighboar1(currentField, x, y, player) && stackField.empty()) {

            counter=0;
            FlagEnd= false;
            return false;
        }
        else
        if (!SearchNeighboar1(currentField, x, y, player)) {
            counter = 0;
            currentField[x][y] = 5;
            Field2 = PopField();
            Field2[x][y] = 5;
            x = PopX();
            y = PopY();
            SearchNeighboar1(Field2, x, y, player);
            FlagEnd = false;
            return false;
        }

        return true;
    }

    boolean FlagPlayer=false;

    public boolean SearchNeighboar1(int[][]currentField, int x, int y, int player){

        //если все false, то
        counter=0;
        FlagPlayer=false;
        if (!CheckField(currentField, x-1, y-1, player)&&!CheckField(currentField, x-1, y, player)&&!CheckField(currentField, x-1, y+1, player)&&
                !CheckField(currentField, x, y-1, player)&&!CheckField(currentField, x, y + 1, player)&&
                !CheckField(currentField, x+1, y-1, player)&&!CheckField(currentField, x+1, y, player)&&!CheckField(currentField, x+1, y+1, player)){System.out.println("is searchmeth"); return false;}
        return true;


    }


    boolean CheckField(int[][]currentField, int x, int y, int player){
        if(x<0||x>=Main.LINE||y<0||y>=Main.COLUMN) return false;
        if (currentField[x][y] ==player) {return true;}
        if (currentField[x][y]==4) {counter++;
            if(x==StartX&&y==StartY) {FlagPlayer=true;}
            return false;}
        return false;
    }



    private int[][] PopField(){ //вернуть поле из стека

        Object something = stackField.pop();
        int [][]Field = (int[][]) something;
        return Field;
    }

    private int PopX(){ //вернуть координату из стека.

        Object something = stackX.pop();
        Integer Field = (Integer) something;
        return Field;
    }

    private int PopY(){ //вернуть координату у из стека

        Object something = stackY.pop();
        Integer Field = (Integer) something;
        return Field;
    }

}
