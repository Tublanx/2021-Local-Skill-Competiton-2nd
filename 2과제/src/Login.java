import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends Baseframe {

	JTextField txt[] = { new JTextField(15), new JPasswordField(15) };

	String str[] = "아이디,비밀번호".split(",");

	public Login(Main main) {
		super("로그인", 280, 200);

		this.add(lbl2("방탈출", 0, 30), "North");
		this.add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		this.add(s = new JPanel(new GridLayout(1, 2, 5, 5)), "South");

		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel();

			tmp.add(sz(lbl1(str[i], 2, 13), 60, 20));
			tmp.add(txt[i]);

			c.add(tmp);
		}

		for (var k : "로그인,회원가입".split(",")) {
			JButton btn = btn(k, e -> {
				if (e.getActionCommand().equals("로그인")) {
					if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
						eMsg("빈칸이 있습니다.");
						return;
					}

					try {
						var rs = stmt.executeQuery("select * from user where u_id = '" + txt[0].getText()
								+ "' and u_pw = '" + txt[1].getText() + "'");
						if (!rs.next()) {
							eMsg("회원 정보가 일치하지 않습니다.");
							return;
						} else {
							uno = rs.getString(1);
							iMsg(rs.getString(4) + "님 환영합니다.");
							main.login();
							dispose();
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					new Sign().addWindowListener(new Before(Login.this));
				}
			});

			s.add(btn);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 10, 10, 10));

		this.setVisible(true);
	}
}
