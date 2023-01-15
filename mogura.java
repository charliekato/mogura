import java.applet.*;
import java.awt.event.*;
import java.awt.*;
/*
  <applet code="mogura" width=400 height=400>
  </applet>
*/

// ���b�Z�[�W�{�b�N�X�N���X���`����
class MyMessageBox extends Frame {
    FontMetrics fm;
    Font font;
    int ghit, gmiss, gescape, gscore;

    // �f�t�H���g�R���X�g���N�^�̒�`
    public MyMessageBox() {
        // �E�B���h�E�̃^�C�g����ݒ肷��
        super("Game Over - Your Score");

        // �t�H���g��ݒ肷��i�����ł́A�uDialog�v���g���j
        font = new Font("Dialog", Font.PLAIN, 12);
        fm = getFontMetrics(font);

        // �w�i�J���[��ݒ肷��
        setBackground(Color.gray.brighter());
        // �\���ʒu��ݒ肷��
        setLocation(200, 300);
        // ���T�C�Y�@�\�𖳌��ɂ���
        setResizable(false);
        // �ŏ��͔�\���Ƃ���
        setVisible(false);

        // �L�[���X�i��o�^����
        addKeyListener(
            new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == e.VK_ENTER) {
                        // ��\���Ƃ���
                        setVisible(false);
                    }
                }
            }
        );

        // �E�B���h�E���X�i��o�^�A��������
        addWindowListener(
            new WindowAdapter() {
                // ��\���Ƃ��邾��
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            }
        );
    }

    // ���b�Z�[�W�������ݒ肷�郁�\�b�h
    public void setText(int hit, int miss, int escape, int score) {
        ghit = hit;   gmiss = miss; gescape = escape; gscore = score;
        // ���b�Z�[�W�{�b�N�X�̃T�C�Y�𒲐�����
        setSize(250,  180);
        // �A�C�R��������Ă���΁A���Ƃɖ߂�
        setState(Frame.NORMAL);
        // ���b�Z�[�W�{�b�N�X��\������
        setVisible(true);
        // �`����e���X�V����
        repaint();
        // �r�[�v����炷
        //Toolkit.getDefaultToolkit().beep();
    }

    // ���b�Z�[�W��`�悷��
    public void paint(Graphics g) {
        // �t�H���g��ݒ肷��
        g.setFont(font);
        // �������E��`�����߂�
        Insets is = getInsets();
        // x����y���̒����������s��
        String  str="Game Over";
        int x = (getWidth() - is.left - is.right - fm.stringWidth(str))/2
            + is.left;
        int y = (getHeight() - is.top - is.bottom - fm.getHeight())/2
            + fm.getAscent() + is.top;
        // �������`�悷��
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

    ��ʂ��X�������A���̈���̉����� x_unit �c�� y_unit

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
    int             num_mogu =20;            //������̐�(�o��������)
    int             num_mogu2=20;           //������̐�(��������)
    int             flag[]=new      int[9]; //������̏��
                                            //  0 - �B��Ă���
                                            //  1 - �����o�Ă���
                                            //  2 - �����o�Ă���
                                            //  3 - �S���o�Ă���
                                            //  4 - �������ꂽ���
    int             mog[]=new       int[9]; //�����炪�o�����Ă��鎞��
    int             x_unit=100,
                    y_unit=100,
                    x_offset=0,
                    y_offset=60;
    Image           mogura[]=new Image[5];      //
                                    // �C���[�W
    Image           base,spc15;     // �w�i�Ɠ����C���[�W
    int             a;
    int             position;       // ������̈ʒu
    int             score=0;        //�Q�[���̓��_
    int             hit=0,          //�q�b�g������
                    miss=0,         //�~�X�q�b�g������
                    escape=0;       //������ꂽ��
    int             a_value=5;      // ����mog���o�������邩�ǂ��������߂�l�B
    int             b_value=36;     // 100mS��a_value/b_value�̊m���ł����炪�o��
    int             c_value=5;      // ��������炪�o�����Ă��玟�̂����炪�o������܂ł̎���(�P��:100mS)
    int             c_timer;        // ���̒l��0���傫���ꍇ�́A�V���ɂ�����͏o�Ȃ��B
    int             base_time=6;    // mog���Œ�on�̎��� �P�ʂ�100�~���b
    int             sx=260,sy=40;   // score�̐擪���W
    int             mogu_to_update_x;  // paint ���\�b�h��update���K�v�ȃ��O����x���W
    int             mogu_to_update_y;  // paint ���\�b�h��update���K�v�ȃ��O����y���W
    int             mogu_to_update_k;  // paint ���\�b�h��update���K�v�ȃ��O����Key�ԍ�
    Image           state_of_that;   // ��L�̃��O���̏�ԁB(=flag[]�ɑΉ�)
    Button          start_button,stop_button;
    Checkbox        easy,normal,hard;
    MyMessageBox    mbox;

    public void init(){
        int             x,y;
        setBackground(new Color(255,255,255)); // white ....
        mogura[0]=getImage(getDocumentBase(),"base.gif");       // background image
        mogura[1]=getImage(getDocumentBase(),"mogura1.gif");    // �o�����鎞�̂�����
        mogura[2]=getImage(getDocumentBase(),"mogura2.gif");    //
        mogura[3]=getImage(getDocumentBase(),"mogura3.gif");    //
        mogura[4]=getImage(getDocumentBase(),"mogura4.gif");    // �������ꂽ���̂�����
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
                    if (mog[i]>0) {  /* mog[i]��i�Ԗڂ̃��O�����o�����Ă��鎞��*/
                                 /* �܂�Amog[i]>0�Ȃ烂�O�����o�����Ă���Ƃ�������*/
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
                if ((a<a_value)&&(c_timer==0)) {   /*���O�����o��������*/
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
                if (num_mogu2==0) { // �Q�[���I��
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
    // MyDraw-- ���O���̊G��`��
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
            mog[ii]=4;   /* hit�����Ƃ���400ms�\������B*/
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
