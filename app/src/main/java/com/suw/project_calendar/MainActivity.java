package com.suw.project_calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btntoday,btnviewtype;//메인 엑티비티 위젯들
    TextView textMonth;
    LinearLayout monthView,weekView;

    private MonthGridAdapter monthgridAdapter;//monthView 위젯들
    private ArrayList<String> dayList;
    private GridView monthgridView;
    private Calendar mCal;
    private int monthcnt,dayNum,maxnum;
    Button btn_b_month,btn_n_month;
    Integer todayMonth;
    View addworkView;
    String fileName;
    boolean typemonth = true;

    private  WeekGridAdapter weekgridAdapter;//weekView 위젯들
    private ArrayList<String> weekList;
    private GridView weekgridView;
    private int weekcnt,thismonth;
    private Calendar wCal;
    Button btn_b_week,btn_n_week;



    TextView week[] = new TextView[7];
    Integer weekid[] = {R.id.week_1,R.id.week_2,R.id.week_3,R.id.week_4,R.id.week_5,R.id.week_6,R.id.week_7};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btntoday = (Button) findViewById(R.id.btntoday);
        btnviewtype = (Button) findViewById(R.id.btnviewtype);

        textMonth = (TextView) findViewById(R.id.textMonth);

        monthView = (LinearLayout) findViewById(R.id.month_layout);
        weekView = (LinearLayout) findViewById(R.id.week_layout);

        setMonth();
        //setWeek();

        registerForContextMenu(btnviewtype);
        btnviewtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"꾹 눌러주세요!",Toast.LENGTH_SHORT).show();
            }
        });

        btntoday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCal = Calendar.getInstance();
                wCal = Calendar.getInstance();
                if(typemonth)
                    callMonthView();
                else
                    callWeekView();
            }
        });
    }
    private void setMonth(){
        Toast.makeText(getApplicationContext(),"월간일정 방식으로 변경됨.",Toast.LENGTH_SHORT).show();
        monthView.setVisibility(View.VISIBLE);
        weekView.setVisibility(View.INVISIBLE);
        typemonth = true;
        callMonthView();
    }
    private void setWeek(){
        Toast.makeText(getApplicationContext(),"주간일정 방식으로 변경됨.",Toast.LENGTH_SHORT).show();
        monthView.setVisibility(View.INVISIBLE);
        weekView.setVisibility(View.VISIBLE);
        typemonth = false;
        wCal = Calendar.getInstance();
        callWeekView();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        if(v==btnviewtype){
            menuInflater.inflate(R.menu.menu1, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.typeMonth:
                mCal = Calendar.getInstance();
                setMonth();
                return true;
            case R.id.typeWeekly:
                wCal = Calendar.getInstance();
                setWeek();
                return true;
        }
        return false;
    }

    //월간일정 함수들
    private void callMonthView(){
        monthgridView=(GridView)findViewById(R.id.monthgridview);

        btn_b_month=(Button)findViewById(R.id.btn_b_month);
        btn_n_month=(Button)findViewById(R.id.btn_n_month);

        mCal = Calendar.getInstance();
        todayMonth=mCal.get(Calendar.MONTH);
        monthcnt=0;
        textMonth.setTextSize(40);
        textMonth.setText(mCal.get(Calendar.MONTH)+monthcnt+1+"월");
        makeMonthView();
        btn_b_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCal.get(Calendar.MONTH)+monthcnt>0) {
                    monthcnt--;
                    textMonth.setText(mCal.get(Calendar.MONTH)+monthcnt+1+"월");
                    makeMonthView();
                }
                else
                    Toast.makeText(getApplicationContext(),"달력이 처음 장입니다.",Toast.LENGTH_SHORT).show();
            }
        });
        btn_n_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCal.get(Calendar.MONTH)+monthcnt<11){
                    monthcnt++;
                    textMonth.setText(mCal.get(Calendar.MONTH)+monthcnt+1+"월");
                    makeMonthView();
                }
                else
                    Toast.makeText(getApplicationContext(),"달력이 마지막 장입니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void makeMonthView() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        date.setMonth(date.getMonth()+monthcnt);

        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);

        dayList = new ArrayList<String>();

        mCal = Calendar.getInstance();
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date))-1,1);

        dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        int month = mCal.get(Calendar.MONTH);       //이전달 날짜 입력하기
        Calendar bCal = Calendar.getInstance();
        bCal.set(2020,month-1,1);
        int bmaxnum = bCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for(int i=1;i<dayNum;i++)
            dayList.add(""+(bmaxnum-dayNum+i+1));

        maxnum = mCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        mCal.set(Calendar.MONTH,(mCal.get(Calendar.MONTH)+1)+3);

        for(int j=0;j<maxnum;j++)   //이번달 날짜 입력하기
            dayList.add(""+(j+1));

        for(int i=1;i<44-dayNum-maxnum;i++) // 월별 칸수 다 일정하게하기
            dayList.add(""+i);

        monthgridAdapter = new MonthGridAdapter(getApplicationContext(),dayList);
        monthgridView.setAdapter(monthgridAdapter);

        monthgridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                if(monthgridAdapter.getItemId(position)<dayNum-1 || monthgridAdapter.getItemId(position)>maxnum+dayNum-2) { // 이전, 다음달 날짜에 일정 추가 못하게하기
                    Toast.makeText(getApplicationContext(),"이 칸은 이번달이 아니기에 일정을 추가할 수 없습니다.",Toast.LENGTH_SHORT).show();
                    return false;
                }
                final int month = mCal.get(Calendar.MONTH)+1+monthcnt, day = (int)monthgridAdapter.getItemId(position)+2-dayNum;
                fileName = month + "_" + day + ".txt";

                addworkView = (View) View.inflate(MainActivity.this,R.layout.addwork_contextbox,null);

                final EditText addtitile = (EditText) addworkView.findViewById(R.id.addtitle);
                final EditText s_h = (EditText) addworkView.findViewById(R.id.starttime_h);
                final EditText s_m = (EditText) addworkView.findViewById(R.id.starttime_m);
                final EditText f_h = (EditText) addworkView.findViewById(R.id.finishtime_h);
                final EditText f_m = (EditText) addworkView.findViewById(R.id.finishtime_m);
                final EditText addplace = (EditText) addworkView.findViewById(R.id.addplace);
                final EditText addmemo = (EditText) addworkView.findViewById(R.id.addmemo);

                File fFile1 = new File("/data/data/com.suw.project_calendar/files/"+fileName);
                if(fFile1.exists()) { // 파일 존재하면 정보 읽어와서 일정추가 화면 채우기
                    try {
                        FileInputStream inFs = openFileInput(fileName);
                        byte[] txt = new byte[500];
                        inFs.read(txt);
                        String[] fdata = (new String(txt)).trim().split("@");

                        addtitile.setText(fdata[0]);
                        s_h.setText(fdata[1]);
                        s_m.setText(fdata[2]);
                        f_h.setText(fdata[3]);
                        f_m.setText(fdata[4]);
                        addplace.setText(fdata[5]);
                        addmemo.setText(fdata[6]);

                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle(month+"월 "+day+"일의 일정 추가하기");
                dlg.setView(addworkView);
                dlg.setPositiveButton("추가", new DialogInterface.OnClickListener() {//일정 추가하기
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(addtitile.getText().toString().isEmpty() || s_h.getText().toString().isEmpty() || s_m.getText().toString().isEmpty() || f_h.getText().toString().isEmpty() || f_m.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "일정의 제목과 시간은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        int     num1 = Integer.parseInt(s_h.getText().toString()), num2 = Integer.parseInt(s_m.getText().toString()),
                                num3 = Integer.parseInt(f_h.getText().toString()), num4 = Integer.parseInt(f_m.getText().toString());

                        if(num1 < 0 || num1 > 23 || num2 < 0 || num2 > 59 || num3 < 0 || num3 > 23 || num4 < 0 || num4 > 59) {
                            Toast.makeText(getApplicationContext(), "시간을 정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        if(num1*60+num2 >=num3*60+num4) {
                            Toast.makeText(getApplicationContext(), "일정의 시작시간이 끝나는 시간보다 빠르다니요...", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(addplace.getText().toString().isEmpty()) //장소와 메모는 여백시 띄어쓰기로 대체하여 @로 나누기
                            addplace.setText("장소 없음");
                        if(addmemo.getText().toString().isEmpty())
                            addmemo.setText("메모 없음");
                        try {
                            fileName = month + "_" + day + ".txt";
                            FileOutputStream outFs = openFileOutput(fileName, Context.MODE_PRIVATE);
                            String str = addtitile.getText().toString()+"@" + s_h.getText().toString()+"@"+s_m.getText().toString()+"@"+f_h.getText().toString()+"@"+f_m.getText().toString()+"@"+addplace.getText().toString()+"@"+addmemo.getText().toString();
                            outFs.write(str.getBytes());
                            outFs.close();
                            Toast.makeText(getApplicationContext(),month+"월 "+day+"일에 일정이 생성됨",Toast.LENGTH_SHORT).show();
                            makeMonthView();
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {//일정 삭제하기
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File fFile2 = new File("/data/data/com.suw.project_calendar/files/"+month+"_"+day+".txt");
                        if(fFile2.exists()) {
                            fFile2.delete();
                            Toast.makeText(getApplicationContext(),"일정이 존재하여 삭제하였습니다.",Toast.LENGTH_SHORT).show();
                            makeMonthView();
                        } else
                            Toast.makeText(getApplicationContext(),"일정은 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
                return false;
            }
        });
    }

    public class MonthGridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public MonthGridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MonthViewHolder holder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.month_item_calendar_gridview, parent, false);
                holder = new MonthViewHolder();
                holder.monthItemGridView = (TextView) convertView.findViewById(R.id.month_item_gridview);
                holder.month_text_workname = (TextView) convertView.findViewById(R.id.month_text_workname);
                convertView.setTag(holder);
            } else
                holder = (MonthViewHolder) convertView.getTag();

            holder.monthItemGridView.setText("" + getItem(position));
            holder.monthItemGridView.setTextColor(Color.BLACK);

            if (getItemId(position) % 7 == 0) //일요일 텍스트 컬러 변경
                holder.monthItemGridView.setTextColor(Color.RED);
            else if (getItemId(position) % 7 == 6) //토요일 텍스트 컬러 변경
                holder.monthItemGridView.setTextColor(Color.BLUE);

            mCal = Calendar.getInstance();

            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdfNow = new SimpleDateFormat("MM");
            String Month = sdfNow.format(date);

            if (sToday.equals(getItem(position)) && textMonth.getText().toString().equals(Integer.parseInt(Month) + "월"))  //오늘의 텍스트 컬러 변경
                holder.monthItemGridView.setTextColor(Color.GREEN);

            if(getItemId(position)<dayNum-1 || getItemId(position)>maxnum+dayNum-2)//이전, 다음달 텍스트 컬러 변경
                holder.monthItemGridView.setTextColor(Color.GRAY);

            int month = mCal.get(Calendar.MONTH)+1+monthcnt;

            File fFile = new File("/data/data/com.suw.project_calendar/files/" + month + "_" + getItem(position) + ".txt"); //일정이 있으면 일정의 이름 날짜밑에
               if (fFile.exists()) {
                   try {
                       fileName = month+ "_" + getItem(position) + ".txt";
                       FileInputStream inFs = openFileInput(fileName);
                       byte[] txt = new byte[500];
                       inFs.read(txt);
                       String[] fdata = (new String(txt)).trim().split("@");
                       holder.month_text_workname.setText(fdata[0]);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            return convertView;
        }
    }
    private class MonthViewHolder{
        TextView monthItemGridView,month_text_workname;
    }


    //주간일정 함수들
    private void callWeekView(){
        weekgridView=(GridView)findViewById(R.id.weekgridview);

        btn_b_week=(Button)findViewById(R.id.btn_b_week);
        btn_n_week=(Button)findViewById(R.id.btn_n_week);

        todayMonth=wCal.get(Calendar.MONTH);
        monthcnt=0;

        dayNum = wCal.get(Calendar.DAY_OF_WEEK);

        textMonth.setTextSize(30);
        makeweekView(wCal.get(Calendar.DAY_OF_MONTH));
        thismonth = wCal.get(Calendar.MONTH)+1;
        int num1 = Integer.parseInt(week[0].getText().toString()),num2 = Integer.parseInt(week[6].getText().toString());
        if(num1>num2 && wCal.get(Calendar.WEEK_OF_MONTH)==1) {
            textMonth.setText((thismonth - 1) + "/" + num1 + " ~ " + thismonth + "/" + num2);
            if(thismonth==1) {
                textMonth.setTextSize(25);
                textMonth.setText(19 + "/" + 12 + "/" + num1 + " ~ " + thismonth + "/" + num2);
            }
        }
        else if(num2<7) {
            textMonth.setText(thismonth + "/" + num1 + " ~ " + (thismonth + 1) + "/" + num2);
            if(thismonth==12) {
                textMonth.setTextSize(25);
                textMonth.setText(thismonth + "/" + num1 + " ~ "+21+"/" + 1 + "/" + num2);
            }
        }
        else
            textMonth.setText(thismonth+"/"+num1+" ~ "+thismonth+"/"+num2);

        weekcnt = wCal.get(Calendar.WEEK_OF_YEAR);
        btn_b_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//2020년의 12월 31일은 53주인데 53주는 불러올 수 없음.
                if(wCal.get(Calendar.WEEK_OF_MONTH)==1 && wCal.get(Calendar.MONTH)==0)
                    Toast.makeText(getApplicationContext(),"2020년의 첫 주입니다.",Toast.LENGTH_SHORT).show();
                else {
                    if(wCal.get(Calendar.WEEK_OF_MONTH)==1){
                        Calendar bCal = Calendar.getInstance();
                        bCal.set(2020,thismonth,1);
                        int startidx = bCal.get(Calendar.DAY_OF_WEEK);
                        bCal.set(2020,thismonth-1,1);
                        int lastday = bCal.getActualMaximum(Calendar.DAY_OF_MONTH);

                        wCal.set(Calendar.MONTH,wCal.get(Calendar.MONTH)-1);
                        wCal.set(Calendar.DAY_OF_MONTH,lastday-7+startidx);
                    }
                    else
                        wCal.set(Calendar.DAY_OF_MONTH,wCal.get(Calendar.DAY_OF_MONTH)-7);
                    callWeekView();
                }
            }
        });
        btn_n_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wCal.get(Calendar.WEEK_OF_MONTH)==5 && wCal.get(Calendar.MONTH)==11)
                    Toast.makeText(getApplicationContext(),"2020년의 마지막 주입니다.",Toast.LENGTH_SHORT).show();
                else{
                    wCal.set(Calendar.DAY_OF_MONTH,wCal.get(Calendar.DAY_OF_MONTH)+7);
                    callWeekView();
                }
            }
        });
    }
    public void makeweekView(int today) {

        weekList = new ArrayList<String>();

        for(int i=0;i<7;i++){
            dayNum = wCal.get(Calendar.DAY_OF_WEEK);
            week[i]=(TextView) findViewById(weekid[i]);

            int num = today-dayNum+i+1;                      // 이번주에 다음달이나 이전달이 있으면 어떻게할것인가?
            int month = wCal.get(Calendar.MONTH);
            Calendar bCal = Calendar.getInstance();
            bCal.set(2020,month-1,1);
            int bmaxnum = bCal.getActualMaximum(Calendar.DAY_OF_MONTH);

            Calendar cCal = Calendar.getInstance();
            cCal.set(2020,month,1);

            if(num<1) { //지난달 날짜 입력
                dayNum = cCal.get(Calendar.DAY_OF_WEEK);
                num = bmaxnum - dayNum + i + 2;
                //Toast.makeText(getApplicationContext(), wCal.get(Calendar.MONTH)+"/"+bCal.get(Calendar.MONTH)+"/"+cCal.get(Calendar.MONTH)+"/"+bmaxnum +"  "+dayNum+"  "+i,Toast.LENGTH_SHORT).show();
            } else if(num>wCal.getActualMaximum(Calendar.DAY_OF_MONTH)){
                num-=wCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }

            week[i].setText(num+"");
        }

        for(int i=0;i<24*7;i++)
            weekList.add("@");

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM");
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("d");
        String Month = sdfNow.format(date),Day = sdfNow2.format(date);
        for(int i=0;i<7;i++) {
            if (Day.equals(week[i].getText().toString()) && Integer.parseInt(Month) == (wCal.get(Calendar.MONTH)+1))  //오늘의 텍스트 컬러 변경
                week[i].setTextColor(Color.GREEN);
            else if(i==0)
                week[i].setTextColor(Color.RED);
            else if(i==6)
                week[i].setTextColor(Color.BLUE);
            else
                week[i].setTextColor(Color.BLACK);
        }

        weekgridAdapter = new WeekGridAdapter(getApplicationContext(),weekList);
        weekgridView.setAdapter(weekgridAdapter);

        weekgridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                final int month = wCal.get(Calendar.MONTH)+1+monthcnt, day = Integer.parseInt(week[position%7].getText().toString());

                if(wCal.get(Calendar.MONTH)==0 && wCal.get(Calendar.WEEK_OF_MONTH)==1 && day>7) {
                    Toast.makeText(getApplicationContext(),"2020년이전의 일정관리는 지원하지 않습니다.",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if(wCal.get(Calendar.MONTH)==11 && wCal.get(Calendar.WEEK_OF_MONTH)>=4 && day<7) {
                    Toast.makeText(getApplicationContext(),"2020년이후의 일정관리는 지원하지 않습니다.",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if(Integer.parseInt(week[0].getText().toString())>day)
                    fileName = (month+1) + "_" + day + ".txt";
                else
                    fileName = month + "_" + day + ".txt";

                addworkView = (View) View.inflate(MainActivity.this,R.layout.addwork_contextbox,null);

                final EditText addtitile = (EditText) addworkView.findViewById(R.id.addtitle);
                final EditText s_h = (EditText) addworkView.findViewById(R.id.starttime_h);
                final EditText s_m = (EditText) addworkView.findViewById(R.id.starttime_m);
                final EditText f_h = (EditText) addworkView.findViewById(R.id.finishtime_h);
                final EditText f_m = (EditText) addworkView.findViewById(R.id.finishtime_m);
                final EditText addplace = (EditText) addworkView.findViewById(R.id.addplace);
                final EditText addmemo = (EditText) addworkView.findViewById(R.id.addmemo);

                File fFile;
                if(Integer.parseInt(week[0].getText().toString())>day)
                    fFile = new File("/data/data/com.suw.project_calendar/files/"+(month+1)+"_"+day+".txt");
                else
                    fFile = new File("/data/data/com.suw.project_calendar/files/"+month+"_"+day+".txt");
                if(fFile.exists()) { // 파일 존재하면 정보 읽어와서 일정추가 화면 채우기
                    try {
                        FileInputStream inFs = openFileInput(fileName);
                        byte[] txt = new byte[500];
                        inFs.read(txt);
                        String[] fdata = (new String(txt)).trim().split("@");

                        addtitile.setText(fdata[0]);
                        s_h.setText(fdata[1]);
                        s_m.setText(fdata[2]);
                        f_h.setText(fdata[3]);
                        f_m.setText(fdata[4]);
                        addplace.setText(fdata[5]);
                        addmemo.setText(fdata[6]);

                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    s_h.setText(position/7+"");

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                if(Integer.parseInt(week[0].getText().toString())>day)
                    dlg.setTitle((month+1)+"월 "+day+"일의 일정 추가하기");
                else
                    dlg.setTitle(month+"월 "+day+"일의 일정 추가하기");
                dlg.setView(addworkView);
                dlg.setPositiveButton("추가", new DialogInterface.OnClickListener() {//일정 추가하기
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int     num1 = Integer.parseInt(s_h.getText().toString()), num2 = Integer.parseInt(s_m.getText().toString()),
                                num3 = Integer.parseInt(f_h.getText().toString()), num4 = Integer.parseInt(f_m.getText().toString());

                        if(num1 < 0 || num1 > 23 || num2 < 0 || num2 > 59 || num3 < 0 || num3 > 23 || num4 < 0 || num4 > 59) {
                            Toast.makeText(getApplicationContext(), "시간을 정확히 입력해주세요!", Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        if(num1*60+num2 >=num3*60+num4) {
                            Toast.makeText(getApplicationContext(), "일정의 시작시간이 끝나는 시간보다 빠르다니요...", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(addtitile.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "일정의 제목은 공백일 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        if(addplace.getText().toString().isEmpty()) //장소와 메모는 여백시 띄어쓰기로 대체하여 @로 나누기
                            addplace.setText("장소 없음");
                        if(addmemo.getText().toString().isEmpty())
                            addmemo.setText("메모 없음");
                        try {
                            if(Integer.parseInt(week[0].getText().toString())>day)
                                fileName = (month+1) + "_" + day + ".txt";
                            else
                                fileName = month + "_" + day + ".txt";
                            FileOutputStream outFs = openFileOutput(fileName,Context.MODE_PRIVATE);
                            String str = addtitile.getText().toString()+"@" + s_h.getText().toString()+"@"+s_m.getText().toString()+"@"+f_h.getText().toString()+"@"+f_m.getText().toString()+"@"+addplace.getText().toString()+"@"+addmemo.getText().toString();
                            outFs.write(str.getBytes());
                            outFs.close();
                            if(Integer.parseInt(week[0].getText().toString())>day)
                                Toast.makeText(getApplicationContext(),(month+1)+"월 "+day+"일에 일정이 생성됨",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(),month+"월 "+day+"일에 일정이 생성됨",Toast.LENGTH_SHORT).show();
                            callWeekView();
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {//일정 삭제하기
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File fFile;
                        if(Integer.parseInt(week[0].getText().toString())>day)
                            fFile = new File("/data/data/com.suw.project_calendar/files/"+(month+1)+"_"+day+".txt");
                        else
                            fFile = new File("/data/data/com.suw.project_calendar/files/"+month+"_"+day+".txt");
                        if(fFile.exists()) {
                            fFile.delete();
                            Toast.makeText(getApplicationContext(),"일정이 존재하여 삭제하였습니다.",Toast.LENGTH_SHORT).show();
                            callWeekView();
                        } else
                            Toast.makeText(getApplicationContext(),"일정은 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
                return false;
            }
        });
    }

    public class WeekGridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public WeekGridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            WeekViewHolder holder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.week_item_calendar_gridview, parent, false);
                holder = new WeekViewHolder();
                holder.week_text_workname = (TextView) convertView.findViewById(R.id.week_text_workname);
                holder.week_text_worktime = (TextView) convertView.findViewById(R.id.week_text_worktime);
                holder.week_text_workplace = (TextView) convertView.findViewById(R.id.week_text_workplace);
                holder.week_item_background = (LinearLayout) convertView.findViewById(R.id.week_item_background);
                convertView.setTag(holder);
            } else
                holder = (WeekViewHolder) convertView.getTag();


            holder.week_text_workname.setText(" ");
            holder.week_text_workname.setTextColor(Color.BLACK);

            if(Integer.parseInt(week[6].getText().toString()) < Integer.parseInt(week[((int)getItemId(position))%7].getText().toString()))
                return convertView;
            int month = wCal.get(Calendar.MONTH)+1+monthcnt;
            File fFile;
            if(Integer.parseInt(week[0].getText().toString())>Integer.parseInt(week[((int)getItemId(position))%7].getText().toString()))
                fFile = new File("/data/data/com.suw.project_calendar/files/" + (month + 1) + "_" + week[((int) getItemId(position)) % 7].getText().toString() + ".txt"); //일정이 있으면 일정의 이름 날짜밑에
            else
                fFile = new File("/data/data/com.suw.project_calendar/files/" + month + "_" + week[((int)getItemId(position))%7].getText().toString() + ".txt"); //일정이 있으면 일정의 이름 날짜밑에
            if (fFile.exists()) {
                try {
                    if(Integer.parseInt(week[0].getText().toString())>Integer.parseInt(week[((int)getItemId(position))%7].getText().toString()))
                        fileName = (month+1)+ "_" + week[((int)getItemId(position))%7].getText().toString() + ".txt";
                    else
                        fileName = month+ "_" + week[((int)getItemId(position))%7].getText().toString() + ".txt";

                    FileInputStream inFs = openFileInput(fileName);
                    byte[] txt = new byte[500];
                    inFs.read(txt);
                    String[] fdata = (new String(txt)).trim().split("@");
                    if((((int)getItemId(position))/7)==Integer.parseInt(fdata[1]) && ((position)%7)%2 == 0) { //월,수,금,일 파란색 배경
                        holder.week_text_workname.setText(fdata[0]);
                        holder.week_text_worktime.setText(fdata[1] + ":" + fdata[2] + " ~ " + fdata[3] + ":" + fdata[4]);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.week_text_worktime.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.color.mholo_blue_light));
                            holder.week_item_background.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge0));
                            if(!(fdata[5].equals("장소 없음"))) {
                                holder.week_text_workplace.setText(fdata[5]);
                                holder.week_text_workplace.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.color.mholo_blue_light));
                            }
                        } else {
                            holder.week_text_worktime.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.color.mholo_blue_light));
                            holder.week_item_background.setBackgroundDrawable(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge0));
                        }
                    } else if((((int)getItemId(position))/7) > Integer.parseInt(fdata[1]) && (((int)getItemId(position))/7) < Integer.parseInt(fdata[3]) && ((position)%7)%2 == 0) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            holder.week_item_background.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge0));
                         else
                            holder.week_item_background.setBackgroundDrawable(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge0));
                    } else if((((int)getItemId(position))/7)==Integer.parseInt(fdata[3]) && Integer.parseInt(fdata[4]) != 0 && ((position)%7)%2 == 0) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            holder.week_item_background.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge0));
                        else
                            holder.week_item_background.setBackgroundDrawable(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge0));
                    }
                    if((((int)getItemId(position))/7)==Integer.parseInt(fdata[1]) && ((position)%7)%2 == 1) { //화,목,토 노란색 배경
                        holder.week_text_workname.setText(fdata[0]);
                        holder.week_text_worktime.setText(fdata[1] + ":" + fdata[2] + " ~ " + fdata[3] + ":" + fdata[4]);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.week_text_worktime.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.color.mholo_blue_light));
                            holder.week_item_background.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge1));
                            if(!(fdata[5].equals("장소 없음"))) {
                                holder.week_text_workplace.setText(fdata[5]);
                                holder.week_text_workplace.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.color.mholo_blue_light));
                            }
                        } else {
                            holder.week_text_worktime.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.color.mholo_blue_light));
                            holder.week_item_background.setBackgroundDrawable(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge1));
                        }
                    } else if((((int)getItemId(position))/7) > Integer.parseInt(fdata[1]) && (((int)getItemId(position))/7) < Integer.parseInt(fdata[3]) && ((position)%7)%2 == 1) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            holder.week_item_background.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge1));
                        else
                            holder.week_item_background.setBackgroundDrawable(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge1));
                    } else if((((int)getItemId(position))/7)==Integer.parseInt(fdata[3]) && Integer.parseInt(fdata[4]) != 0 && ((position)%7)%2 == 1) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            holder.week_item_background.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge1));
                        else
                            holder.week_item_background.setBackgroundDrawable(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colored_edge1));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return convertView;
        }
    }
    private class WeekViewHolder{
        TextView week_text_workname,week_text_worktime,week_text_workplace;
        LinearLayout week_item_background;
    }
}