import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GameList extends Baseframe {

	DefaultTableModel m = model("번호,날짜,지점명,장르,테마명".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	public GameList() {
		super("게임리스트", 800, 400);

		this.add(lbl2("회원명 : " + getone("select u_name from user where u_no=" + uno), 0, 30), "North");
		this.add(jsc);

		data();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1).toString()).isAfter(LocalDate.now())) {
					eMsg("미래로 예약된 게임은 실행할 수 없습니다.");
					return;
				}

				rno = t.getValueAt(t.getSelectedRow(), 0).toString();
				new RoomEscape(GameList.this).addWindowListener(new Before(GameList.this));
			}
		});

		this.setVisible(true);
	}

	void data() {
		addRow(m,
				"SELECT r_no, r_date, c_name, g_name, t_name FROM 2022지방_1.reservation r, cafe c, theme t, genre g where r.c_no = c.c_no and r.t_no = t.t_no and t.g_no = g.g_no and u_no = "
						+ uno + " and r_attend = 0;");
	}

	public static void main(String[] args) {
		uno = "1";
		new GameList();
	}
}
