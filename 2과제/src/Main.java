import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Main extends Baseframe {

	ArrayList<JPanel> panels = new ArrayList<JPanel>();
	JButton btn[] = { new JButton(), new JButton(), new JButton(), new JButton(), new JButton(), new JButton() };
	JComboBox<String> com;

	Timer timer;

	String str[] = "로그인,마이페이지,검색,게시판,방탈출게임,예약현황".split(",");

	public Main() {
		super("메인", 600, 450);
		this.setDefaultCloseOperation(3);

		this.add(n = new JPanel(new FlowLayout(0)), "North");
		this.add(c = new JPanel());
		this.add(s = new JPanel(), "South");

		n.add(lbl2("예약 TOP5", 0, 15));
		n.add(com = new JComboBox<String>("지점,테마".split(",")));

		timer = new Timer(1, e -> {
			panels.forEach(x -> {
				x.setLocation(x.getLocation().x, x.getLocation().y - 5);
				if (x.getLocation().y <= -425) {
					x.setLocation(x.getLocation().x, panels.get(toInt(x.getName())).getLocation().y + 430);
				}
			});
		});

		for (int i = 0; i < str.length; i++) {
			s.add(btn[i] = btn(str[i], e -> {
				if (e.getActionCommand().equals("로그인")) {
					new Login(this).addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("로그아웃")) {
					uno = "";
					logout();
				} else if (e.getActionCommand().equals("검색")) {
					new Search().addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("게시판")) {
					new Board().addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("방탈출게임")) {
					new GameList().addWindowListener(new Before(Main.this));
				} else if (e.getActionCommand().equals("예약현황")) {
					new Chart().addWindowListener(new Before(Main.this));
				} else if(e.getActionCommand().equals("마이페이지")) {
					new MyPage().addWindowListener(new Before(Main.this));
				}
			}));
		}

		logout();
		changeImage();

		timer.start();

		this.setVisible(true);
	}

	void logout() {
		btn[0].setText("로그인");
		for (int i = 1; i < 5; i++) {
			btn[i].setEnabled(false);
		}
	}

	void login() {
		btn[0].setText("로그아웃");
		for (int i = 1; i < 5; i++) {
			btn[i].setEnabled(true);
		}
	}

	void changeImage() {
		timer.stop();
		c.removeAll();
		panels.clear();

		boolean type = com.getSelectedIndex() == 0;
		try {
			ResultSet rs = stmt.executeQuery("select count(*) as cnt, " + (type ? "c_name" : "t.t_no, t_name")
					+ " from reservation r, cafe c, theme t where " + (type ? "c.c_no = r.c_no" : "t.t_no = r.t_no")
					+ " and r_date <= '2022-04-05' group by r.c_no order by cnt desc limit 5");
			while (rs.next()) {
				var tmp = new JPanel(new BorderLayout());
				var img = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
						.getImage("Datafiles/" + (type ? "지점" : "테마") + "/"
								+ (type ? rs.getString(2).split(" ")[0] : rs.getString(2)) + ".jpg")
						.getScaledInstance(550, 400, 4)));
				var lbl = new JLabel();

				tmp.add(img, "North");
				tmp.add(lbl = lbl2(rs.getString((type ? 2 : 3)), 0, 25));
				tmp.setName((rs.getRow() == 1 ? 4 : rs.getRow() - 2) + "");

				panels.add(tmp);
				c.add(tmp);

				lbl.setBorder(new EmptyBorder(0, 0, 40, 0));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c.repaint();
		c.revalidate();

		timer.restart();
	}

	public static void main(String[] args) {
		new Main();
	}
}
