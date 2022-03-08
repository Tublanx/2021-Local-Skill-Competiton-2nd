import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.Month;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Sign extends Baseframe {

	JComboBox com[] = new JComboBox[3];
	JTextField txt[] = { new JTextField(20), new JTextField(20), new JPasswordField(20), new JPasswordField(20) };

	String str1[] = "이름,아이디,비밀번호,비밀번호 확인,생년월일".split(",");
	String str2[] = "년,월,일".split(",");

	LocalDate today = LocalDate.now();
	LocalDate date = LocalDate.of(2022, 4, 9);

	public Sign() {
		super("회원가입", 500, 400);

		this.add(c = new JPanel(new GridLayout(0, 1)));
		this.add(s = new JPanel(), "South");

		for (int i = 0; i < str1.length; i++) {
			var tmp = new JPanel();

			tmp.add(sz(lbl1(str1[i], 2, 13), 100, 30));
			if (i == 4) {
				for (int j = 0; j < str2.length; j++) {
					tmp.add(com[j] = new JComboBox());
					tmp.add(lbl1(str2[j], 0, 13));
				}
			} else {
				tmp.add(txt[i]);
			}

			c.add(tmp);
		}

		s.add(btn("회원가입", e -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!(txt[1].getText().length() >= 4 && txt[1].getText().length() <= 8)
					|| txt[1].getText().equals(getone("select u_id from user where u_id='" + txt[1].getText() + "'"))) {
				eMsg("사용할 수 없는 아이디입니다.");
				return;
			}

			if (isSame(txt[1].getText(), txt[2].getText())) {
				eMsg("비밀번호는 아이디와 4글자 이상 연속으로 겹쳐질 수 없습니다.");
				return;
			}

			if (!txt[2].getText().equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			String date = com[0].getSelectedItem() + "-" + com[1].getSelectedItem() + "-" + com[2].getSelectedItem();
			iMsg(txt[0].getText() + "님 가입을 환영합니다.");
			dispose();
			execute("insert user values(0, '" + txt[1].getText() + "','" + txt[2].getText() + "','" + txt[0].getText()
					+ "','" + date + "')");
		}));

		com[0].addActionListener(e -> {
			date = LocalDate.of((int) com[0].getSelectedItem(), 1, 1);
			Stream.of(com[1], com[2]).filter(x -> x.getItemCount() != 0).forEach(JComboBox::removeAllItems);

			if (date.getYear() == today.getYear()) {
				for (int i = 0; i < today.getMonthValue(); i++) {
					com[1].addItem(String.format("%02d", i + 1));
				}

				for (int i = 0; i < today.getDayOfMonth(); i++) {
					com[2].addItem(String.format("%02d", i + 1));
				}
			} else {
				for (int i = 0; i < 12; i++) {
					com[1].addItem(String.format("%02d", i + 1));
				}

				for (int i = 0; i < date.lengthOfMonth(); i++) {
					com[2].addItem(String.format("%02d", i + 1));
				}
			}
		});

		com[1].addActionListener(e -> {
			if (com[1].getItemCount() == 0)
				return;

			com[2].removeAllItems();

			date = LocalDate.of((int) com[0].getSelectedItem(), toInt(com[1].getSelectedItem().toString()), 1);
			for (int i = 0; i < date.lengthOfMonth(); i++) {
				com[2].addItem(String.format("%02d", i + 1));
			}
		});

		for (int i = 0; i < com.length; i++) {
			com[i].removeAllItems();
		}

		for (int i = 1900; i <= today.getYear() + 1; i++) {
			com[0].addItem(i);
		}
		for (int i = 0; i < today.getMonthValue(); i++) {
			com[1].addItem(String.format("%02d", i + 1));
		}
		for (int i = 0; i < today.getDayOfMonth(); i++) {
			com[2].addItem(String.format("%02d", i + 1));
		}

		com[0].setSelectedItem(2022);
		com[1].setSelectedItem(String.format("%02d", LocalDate.now().getMonthValue()));
		com[2].setSelectedItem(String.format("%02d", LocalDate.now().getDayOfMonth()));

		this.setVisible(true);
	}

	boolean isSame(String id, String pw) {
		try {
			for (int i = 0; i < id.length() - 4; i++) {
				if (id.contains(pw.substring(i, i + 4))) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	public static void main(String[] args) {
		new Sign();
	}
}
