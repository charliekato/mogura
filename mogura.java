import java.applet.*;
import java.awt.event.*;
import java.awt.*;
/*
  <applet code="mogura" width=400 height=400>
  </applet>
*/

// メッセージボックスクラスを定義する
class MyMessageBox extends Frame {
    FontMetrics fm;
    Font font;
    int ghit, gmiss, gescape, gscore;

    // デフォルトコンストラクタの定義
    public MyMessageBox() {
        // ウィンドウのタイトルを設定する
        super("Game Over - Your Score");

        // フォントを設定する（ここでは、「Dialog」を使う）
        font = new Font("Dialog", Font.PLAIN, 12);
        fm = getFontMetrics(font);

        // 背景カラーを設定する
        setBackground(Color.gray.brighter());
        // 表示位置を設定する
        setLocation(200, 300);
        // リサイズ機能を無効にする
        setResizable(false);
        // 最初は非表示とする
        setVisible(false);

        // キーリスナを登録する
        addKeyListener(
            new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == e.VK_ENTER) {
                        // 非表示とする
                        setVisible(false);
                    }
                }
            }
        );

        // ウィンドウリスナを登録、実装する
        addWindowListener(
            new WindowAdapter() {
                // 非表示とするだけ
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            }
        );
    }

    // メッセージ文字列を設定するメソッド
    public void setText(int hit, int miss, int escape, int score) {
        ghit = hit;   gmiss = miss; gescape = escape; gscore = score;
        // メッセージボックスのサイズを調整する
        setSize(250,  180);
        // アイコン化されていれば、もとに戻す
        setState(Frame.NORMAL);
        // メッセージボックスを表示する
        setVisible(true);
        // 描画内容を更新する
        repaint();
        // ビープ音を鳴らす
        //Toolkit.getDefaultToolkit().beep();
    }

    // メッセージを描画する
    public void paint(Graphics g) {
        // フォントを設定する
        g.setFont(font);
        // 内部境界矩形を求める
        Insets is = getInsets();
        // x軸とy軸の中央揃えを行う
        String  str="Game Over";
        int x = (getWidth() - is.left - is.right - fm.stringWidth(str))/2
            + is.left;
        int y = (getHeight() - is.top - is.bottom - fm.getHeight())/2
            + fm.getAscent() + is.top;
        // 文字列を描画する
        g.drawString(str    ,           x,  y-30 );
        g.drawString("Hit   "   ,35, y-15);
        g.drawString("Miss  "   ,35, y);
        g.drawString("Escape  " ,35, y+15);
        g.drawString("Score   " ,35, y+30);
        g.drawString(": "+ ghit    ,140, y-15);
        g.drawString(": "+ gmiss   ,140, y);
        g.drawString(": "+ gescape ,140, y+15);
        g.drawString(": "+ gscore  ,140, y+30);
    }
}

                                      // java.applet.
public class mogura extends Applet implements Runnable
{
/*

    画面を９分割し、その一つ分の横幅が x_unit 縦が y_unit

    <------> x_unit
    +------+------+------+ A
    |      |      |      | | y_unit
    |      |      |      | |
    +------+------+------+ V
    |      |      |      |
    |      |      |      |
    +------+------+------+
    |      |      |      |
    |      |      |      |
    +------+------+------+


*/
    Thread          th;
    Graphics        g;
    int             game_status=0;     // 0-- not started yet, 1-- under play, -1 -- game over
    int             num_mogu =20;            //もぐらの数(出現した数)
    int             num_mogu2=20;           //もくらの数(消えた数)
    int             flag[]=new      int[9]; //もぐらの状態
                                            //  0 - 隠れている
                                            //  1 - 頭が出ている
                                            //  2 - 半分出ている
                                            //  3 - 全部出ている
                                            //  4 - たたかれた状態
    int             mog[]=new       int[9]; //もぐらが出現している時間
    int             x_unit=100,
                    y_unit=100,
                    x_offset=0,
                    y_offset=60;
    Image           mogura[]=new Image[5];      //
                                    // イメージ
    Image           base,spc15;     // 背景と同じイメージ
    int             a;
    int             position;       // もぐらの位置
    int             score=0;        //ゲームの得点
    int             hit=0,          //ヒットした数
                    miss=0,         //ミスヒットした数
                    escape=0;       //逃げられた数
    int             a_value=5;      // 次のmogを出現させるかどうかをきめる値。
    int             b_value=36;     // 100mSにa_value/b_valueの確率でもぐらが出現
    int             c_value=5;      // あるもぐらが出現してから次のもぐらが出現するまでの時間(単位:100mS)
    int             c_timer;        // この値が0より大きい場合は、新たにもぐらは出ない。
    int             base_time=6;    // mogが最低onの時間 単位は100ミリ秒
    int             sx=260,sy=40;   // scoreの先頭座標
    int             mogu_to_update_x;  // paint メソッドでupdateが必要なモグラのx座標
    int             mogu_to_update_y;  // paint メソッドでupdateが必要なモグラのy座標
    int             mogu_to_update_k;  // paint メソッドでupdateが必要なモグラのKey番号
    Image           state_of_that;   // 上記のモグラの状態。(=flag[]に対応)
    Button          start_button,stop_button;
    Checkbox        easy,normal,hard;
    MyMessageBox    mbox;

    public void init(){
        int             x,y;
        setBackground(new Color(255,255,255)); // white ....
        mogura[0]=getImage(getDocumentBase(),"base.gif");       // background image
        mogura[1]=getImage(getDocumentBase(),"mogura1.gif");    // 出現する時のもぐら
        mogura[2]=getImage(getDocumentBase(),"mogura2.gif");    //
        mogura[3]=getImage(getDocumentBase(),"mogura3.gif");    //
        mogura[4]=getImage(getDocumentBase(),"mogura4.gif");    // たたかれた時のもぐら
        //spc15=getImage(getDocumentBase(),"SPC15.GIF");          // small blank

        start_button = new Button("start");
        stop_button  = new Button("stop");
        CheckboxGroup   cg=new CheckboxGroup();
        easy         = new Checkbox("easy",  cg,false);
        normal       = new Checkbox("normal",cg,true);
        hard         = new Checkbox("hard",  cg,false);
        start_button.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     /* define action for start_button */
                    game_status=1;
                    escape=0;
                    hit=0;
                    miss=0;
                    score=0;
                    num_mogu=20;    /*****/
                    num_mogu2=20;   /*****/
                    //repaint();
                    requestFocus(); //////////////
                }
            }
        );
        stop_button.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game_status=0;
                }
            }
        );
        easy.addItemListener(
            new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    b_value=40;
                    c_value=7;
                    base_time=7;
                }
            }
        );
        normal.addItemListener(
            new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    b_value=36;
                    c_value=5;
                    base_time=6;
                }
            }
        );
        hard.addItemListener(
            new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    b_value=25;
                    c_value=2;
                    base_time=3;
                }
            }
        );

        add(start_button);
        add(stop_button);
        add(easy);
        add(normal);
        add(hard);

        addKeyListener(
            new KeyAdapter( ) {
                public void keyTyped(KeyEvent e) {
                    int  k;
                    if (game_status!=1 ) return;
                    k=(int)e.getKeyChar();
                    //System.out.println(" "+k+ " is pushed..");
                    if ((k>48) && (k<58) )
                    hit_or_miss(k-48);
                }
            }
        );
        addMouseListener(
            new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    int             k=0;
                    //int             x,y;
                    Point           pt;
                    if (game_status!=1) return ;
                    pt = e.getPoint();
                    //x = pt.x;    y=pt.y;
                    if      ((pt.x>x_offset+20)&&(pt.x<x_offset+80)) k=1;
                    else if ((pt.x>x_offset+120)&&(pt.x<x_offset+180)) k=2;
                    else if ((pt.x>x_offset+220)&&(pt.x<x_offset+280)) k=3;
                    else miss();
                    if      ((pt.y>y_offset+20)&&(pt.y<y_offset+80)) k=k+6;
                    else if ((pt.y>y_offset+120)&&(pt.y<y_offset+180)) k=k+3;
                    else if ((pt.y>y_offset+220)&&(pt.y<y_offset+280)) hit_or_miss(k);
                    else miss();
                    hit_or_miss(k);
                }
            }
        );
        mbox = new MyMessageBox();
    }  /* of init */


    public void paint(Graphics g){
        int         i,x,y,k;
        for (k=1;k<10;k++) {
            i=key2pos(k);
            x=x_offset+(i%3)*x_unit;
            y=y_offset+(i/3)*y_unit;
            g.drawImage(mogura[flag[i]],x,y,this);
            g.drawString(" "+k+" ",x+40,y+80);
        }
        g.setColor(Color.white);
        g.fillRect( sx+35,sy-25,30,40);
        //g.drawImage(spc15,sx+35,sy-25,this);
        //g.drawImage(spc15,sx+35,sy-10,this);
        g.setColor(Color.black);
        g.drawString("score: "+score,sx,sy);
    }   /* paint */



    public void update(Graphics g) {
        paint(g);
    }
    public void start(){
        if (th==null) {
            th=new Thread(this);
            th.start();
        }
    }
    public void run(){
        int     x,y,i,k;
        int     pos;
        int     update_need=0;
        while(true){
            update_need=0;
            if (game_status==1) {
                for (k=1;k<10;k++) {
                    i=key2pos(k);
                    if (mog[i]>0) {  /* mog[i]はi番目のモグラが出現している時間*/
                                 /* つまり、mog[i]>0ならモグラが出現しているということ*/
                        if (--mog[i]==0) {
                            if (flag[i]<4)  escape++;
                            flag[i]=0;
                            num_mogu2--;

                            //MyDraw(k);
                        }
                        else {
                            if (flag[i]<3 )flag[i]++ ;
                            //MyDraw(k);
                        }
                        update_need=1;
                    }/*if mog[i]>0*/
                }
                if (c_timer>0) c_timer--;
                a=(int)(Math.random()*b_value);
                if ((a<a_value)&&(c_timer==0)) {   /*モグラを出現させる*/
                    k=(int)(Math.random()*9);
                    k++;
                    pos=key2pos(k);
                    if (mog[pos]==0) {
                        mog[pos]=base_time+(int)( Math.random()*6);
                        flag[pos]=1;
                        update_need=1;
                        //MyDraw(k);
                        num_mogu--;
                        c_timer=c_value;
                    }
                }
                if (num_mogu2==0) { // ゲーム終了
                    game_status=-1;
                    mbox.setText(hit,miss,escape,score);
                }
                if (update_need==1) repaint();
                /**/
            }
            try {
                th.sleep(100);
            }
                catch (InterruptedException e){}
        }
    }
    // MyDraw-- モグラの絵を描く
    public void MyDraw(int k){
        int  i = key2pos(k);
        int  x = x_offset+(i%3)*x_unit;
        int  y = y_offset+(i/3)*y_unit;
        //Myg.drawImage(mogura[flag[i]],x,y,this);
        //Myg.drawString(" "+k+" ",x+40,y+80);
    }


    public int      key2pos(int k){
        int             i;
        switch (k) {
        case 7 :                case 8 :                case 9 :
                i=k-7;
                break;
        case 4:         case 5:         case 6:
                i=k-1;
                break;
        default /*case 1:               case 2:         case 3:*/ :
                i=k+5;
        }
        return i;
    }

    public void     hit_or_miss(int k)
    {
        int     ii;
        int     x,y;
        if (k==0) miss();
        ii=key2pos(k);

        if ((flag[ii]==1)||(flag[ii]==2)||(flag[ii]==3)) {
            mog[ii]=4;   /* hitしたときは400ms表示する。*/
            flag[ii]=4;
            hit++;
            score+=10;
            //MyDraw(k);
            repaint();
        }
        else miss();
    }   /* hit_or_miss */


    public void miss()
    {
        miss++;
        score--;
    }
    class Mylabel extends Label {
        public boolean isFocusTraversable() {
            return true;
        }
    }


}
