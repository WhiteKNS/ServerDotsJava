package com.company.ServerDots;

/**
 * Created by Natalya on 0/0/2015.
 */


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;




public class Main{



    final public static int LINE = 10;
    final public static int COLUMN = 10;
    public static int Field[][]= new int[LINE][COLUMN];
    public static int AddField[][] = new int[LINE][COLUMN];
    public static int PointPlayer1 =0;
    public static int PointPlayer2=0;
    Player player;
    Player player2;

    static{
        for (int i=0; i<LINE; i++){
            for (int j=0; j<COLUMN; j++){
                Field[i][j] = 0;
            }
        }

        AddField=Field;
    }

    Main(){

    }

    public void PrintField(){ //рисуем поле
        System.out.println("Field");
        for (int i=0; i<LINE; i++, System.out.println()){
            for (int j=0; j<COLUMN; j++){
                System.out.print(Field[i][j]);
            }
        }}

    void ReturnNormalFieldView(int play){ //возвращаем видимость поля без 4 и 5
        for (int i=0; i<LINE; i++) {
            for (int j = 0; j < COLUMN; j++) {
                if (Field[i][j]==4||Field[i][j]==5) Field[i][j]=play;
            }
        }
    }

    void EqualFields(){
        for (int i=0; i<LINE; i++) {
            for (int j = 0; j < COLUMN; j++) {
                AddField[i][j]= Field[i][j];
            }
        }
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Main m = new Main();
        m.PrintField(); //нарисовали поле
        ServerSocket welcomeSocket = new ServerSocket(6727); //создали сокет
        StartGame(welcomeSocket); //игра
    }

    public static void StartGame(ServerSocket welcomeSocket) throws IOException{
        Main m = new Main();
        m.player = new Player(); //создаем объект для игрока 1
        m.player2 = new Player(); //создаем объект для игрока 2
        System.out.println("Waiting for connection from player1");

        Socket connectionSocket1 = welcomeSocket.accept(); //ждем соединения от игрока 1
        DataOutputStream outToClient1 = new DataOutputStream(connectionSocket1.getOutputStream()); //отсылать данные клиенту1
        outToClient1.writeInt(1);

        System.out.println("Waiting for connection from player2");
        Socket connectionSocket2 = welcomeSocket.accept();//ждем соединения от игрока 2
        DataOutputStream outToClient2 = new DataOutputStream(connectionSocket2.getOutputStream());//отсылать данные клиенту2
        outToClient2.writeInt(2);

        while(true){


            System.out.println("First player move!");
            m.player.Flag = true; //дает сигнал ,что играет игрок 1

            System.out.println("GO");

            m.getPoints(connectionSocket1, m.player);

            m.player.StartX = m.player.ChoiseLines; //запоминаем начальную точку
            m.player.StartY = m.player.ChoiseColumns;
            m.EqualFields(); //создаем доп поле для работы
            if(m.player.SearchNeighbour(AddField, m.player.ChoiseLines, m.player.ChoiseColumns, 1)&&m.player.Point>1) {PointPlayer1 = m.player.PointsForPlayer1; //если нашли замкнутое пространство, то считаем кол-во очков
                System.out.println("Points  for first player " + PointPlayer1);
            } else System.out.println("Points  for first player " + PointPlayer1);

            m.ReturnNormalFieldView(1); //возвращаем нормальное видение поля


            m.SendToClient(outToClient2);
            outToClient2.flush();


            System.out.println("Second player move!");
            m.player2.Flag = false; //дает сигнал ,что играет игрок 2

            m.getPoints(connectionSocket2, m.player2);
            m.player2.StartX = m.player2.ChoiseLines;
            m.player2.StartY = m.player2.ChoiseColumns;
            m.EqualFields();
            if(m.player2.SearchNeighbour(AddField, m.player2.ChoiseLines, m.player2.ChoiseColumns, 2)&&m.player2.Point>1) {PointPlayer2 = m.player2.PointsForPlayer2;
                System.out.println("Points  for second player " + PointPlayer2);
            } else System.out.println("Points  for second player " +PointPlayer2);
            m.ReturnNormalFieldView(2);

            m.SendToClient(outToClient1);
            outToClient1.flush();
        }

    }

    private void getPoints(Socket socket, Player PPlayer){  //здесь получаем координаты точки, по которой будем бить
        try{
            DataInputStream inToClient1 = new DataInputStream(socket.getInputStream());
            PPlayer.ChoiseLines = inToClient1.readInt(); // принимаем от клиента линию
            System.out.println("Lines  " + PPlayer.ChoiseLines);
            PPlayer.ChoiseColumns =  inToClient1.readInt(); // принимаем от клиента колонку
            System.out.println("Columns  " + PPlayer.ChoiseColumns);

        }
        catch(IOException e){}

    }


    void SendToClient(DataOutputStream outToClient) throws IOException{   //отправить нужно очки каждого игрока + поле

        PrintField();

        try{
            outToClient.writeInt(PointPlayer1); // отправляем клиенту очки
            outToClient.writeInt(PointPlayer2);
            for (int i=0; i<Main.LINE; i++)
            {
                for (int j=0; j<Main.COLUMN; j++)
                {
                    outToClient.writeInt(Field[i][j]);
                }
            }}
        catch(Exception e){}
        finally {
            outToClient.flush();
        }

    }

}