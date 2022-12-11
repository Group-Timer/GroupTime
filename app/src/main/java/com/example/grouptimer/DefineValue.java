package com.example.grouptimer;

public class DefineValue
{
    public final static int     INVALID_VALUE               = -1;

    public static String        Group_ID;
    public static String        Group_Name;

    public static String        User_Name;


    public final static int     Time_Convert_Key            = 0x1;                      // 0000 0000 0000 0001

    public final static int     Day_Cnt                     = 7;                        // Time Table에서 시간을 설정 할 수 있는 요일 수
    public final static int     Times_Of_Day                = 12;                       // 요일별로 시간을 설정 할 수 있는 시간대 수

    public final static int     Max_Bit_Size                = Times_Of_Day;

    public final static int     TimeTable_Button_Cnt        = Day_Cnt * Times_Of_Day;   // Time Table에 생성되는 버튼 수

    public final static int     TimeTable_GridLayout_Cnt    = Day_Cnt + 1;              // Time Table에서 생성되는 GridLayout 수

    public final static int     Mon                         = 0;
    public final static int     Tue                         = 1;
    public final static int     Wed                         = 2;
    public final static int     Thu                         = 3;
    public final static int     Fri                         = 4;
    public final static int     Sat                         = 5;
    public final static int     Sun                         = 6;

    public final static int[][] OverlapLevel                = { {0, 0, 0, 0, 0, 0},
                                                                {0, 5, 3, 2, 1, 1},
                                                                {0, 0, 5, 3, 3, 2},
                                                                {0, 0, 0, 5, 4, 3},
                                                                {0, 0, 0, 0, 5, 4},
                                                                {0, 0, 0, 0, 0, 5} };

    public final static String[] Days                       = {"일", "월", "화", "수", "목", "금", "토"};

    public final static int     Chat_Right                  = 1;
    public final static int     Chat_Left                   = 0;
}
