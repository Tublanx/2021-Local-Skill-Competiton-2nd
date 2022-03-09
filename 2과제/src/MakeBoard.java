import java.awt.Color;
import java.awt.FlowLayout;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class MakeBoard extends Baseframe {

	JTextField txt[] = new JTextField[3];
	JTextArea area;
	JRadioButton jrd[] = { new JRadioButton("비공개"), new JRadioButton("공개") };

	String str[] = "아이디,등록일,제목,내용,공개여부".split(",");

	public MakeBoard(Board board) {
		super("등록", 300, 400);

		this.add(c = new JPanel(new FlowLayout(0)));
		this.add(s = new JPanel(new FlowLayout(2)), "South");

		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0, 0, 10));

			tmp.add(sz(new JLabel(str[i], 2), 50, 20));

			if (i == 4) {
				var tmp2 = new JPanel();
				for (int j = 0; j < jrd.length; j++) {
					tmp2.add(jrd[j]);
				}

				tmp.add(tmp2);
			} else if (i == 3) {
				tmp.add(area = sz(new JTextArea(), 220, 100));
			} else {
				tmp.add(txt[i] = new JTextField(20));
			}

			c.add(tmp);
		}

		s.add(btn("등록", e -> {
			if (txt[1].getText().isEmpty() || txt[2].getText().isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				return;
			}

			iMsg("게시물 등록이 완료되었습니다.");
			execute("insert notice values(0, '" + uno + "','" + txt[1].getText() + "','" + txt[2].getText() + "','"
					+ area.getText() + "','" + 0 + "','" + (jrd[0].isSelected() ? 0 : 1) + "')");
			board.data();
			dispose();
		}));

		s.add(btn("취소", e -> dispose()));

		jrd[1].setSelected(true);
		
		area.setLineWrap(true);
		area.setBorder(new LineBorder(Color.BLACK));

		txt[0].setText(getone("select u_id from user where u_no=" + uno));
		txt[1].setText(LocalDate.now() + "");

		txt[0].setEnabled(false);
		txt[1].setEnabled(false);

		this.setVisible(true);
	}
}
