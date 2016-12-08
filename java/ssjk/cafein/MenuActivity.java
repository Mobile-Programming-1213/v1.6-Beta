package ssjk.cafein;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import static android.graphics.Color.BLACK;

/**
 * Created by wqe13 on 2016-11-28.
 */

public class MenuActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        int id = getIntent().getIntExtra("Extra", 0);
        get_text(id);
    }

    private void get_text(int id) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.textinfo);
        TextView tview = new TextView(this);
        tview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tview.setTextColor(BLACK);
        String str = null;
        switch (id) {
            case R.id.notice:
                str = String.format("Cafe人 ver 1.0\n" +
                        "Released at Nov.28" +
                        "\n\n\n" +
                        "과제제출용으로 만든 어플리케이션이라 부족한 점이 많습니다.\n" +
                        "개선사항/의견은 이메일로 보내주세요!");
                break;
            case R.id.ask:
                str = String.format("강남호: wqe1323@hotmail.com \n\n" +
                        "신창환: chshin622@naver.com \n\n" +
                        "장소영: thdud5364@naver.com\n\n" +
                        "서지원: jiwon277r@naver.com\n\n");
                tview.setTextSize(15);
                break;
            case R.id.developers:
                str = String.format("Team 1213 \n"
                        + "\t\tof Mobile Systems Engineering\n" +
                        "Members:\n" +
                        "\t\t강남호 (Development)\n" +
                        "\t\t신창환 (Design)\n" +
                        "\t\t장소영 (Database + Development)\n" +
                        "\t\t서지원 (Database + Design)\n");
                break;
        }
        tview.setText(str);
        Linkify.addLinks(tview, Linkify.EMAIL_ADDRESSES);
        tview.setLinksClickable(true);
        layout.addView(tview);

    }
}
