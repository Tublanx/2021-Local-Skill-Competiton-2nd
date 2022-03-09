import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Board extends Baseframe {

	JPanel nw, ne, en;

	JLabel date, page;
	JComboBox<String> com;
	JTextField search, title;
	JTextArea area;
	JButton prev, next, edit;

	ArrayList<Object[]> list = new ArrayList<Object[]>();

	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = blueTable(m, getone("select u_id from user where u_no=" + uno));
	JScrollPane jsc = new JScrollPane(t);

	int cnt = 1, max = 5;

	public Board() {
		super("게시판", 700, 400);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(jsc);
		this.add(e = sz(new JPanel(new BorderLayout()), 300, 1), "East");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		e.add(en = new JPanel(new BorderLayout()), "North");
		e.add(area = new JTextArea());
		e.add(date = new JLabel("", 4), "South");

		nw.add(new JLabel("페이지 정보 : ", 2));
		nw.add(page = new JLabel(cnt + "/" + max, 2));
		nw.add(prev = btn("◀", e -> {
			cnt--;

			page.setText(cnt + "/" + max);
			next.setEnabled(true);

			changeTable();

			if (cnt == 1) {
				prev.setEnabled(false);
				return;
			}
		}));
		nw.add(next = btn("▶", e -> {
			cnt++;

			prev.setEnabled(true);
			changeTable();

			if (cnt == max) {
				next.setEnabled(false);
				return;
			}
		}));

		ne.add(new JLabel("분류", 0));
		ne.add(com = new JComboBox<String>("제목,아이디".split(",")));
		ne.add(search = new JTextField(10));
		ne.add(btn("검색", e -> search()));
		ne.add(btn("게시물 작성", e -> {
			new MakeBoard(this).addWindowListener(new Before(Board.this));
		}));

		en.add(lbl2("제목 : ", 2, 15), "West");
		en.add(title = new JTextField(15));
		en.add(edit = btn("수정", e -> {

		}), "East");

		data();

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {

				title.setText(t.getValueAt(t.getSelectedRow(), 1).toString());
				area.setText(
						getone("select n_content from notice where n_no = " + t.getValueAt(t.getSelectedRow(), 0)));
				date.setText("작성일 : " + t.getValueAt(t.getSelectedRow(), 3));

				if (Board.e.isVisible()) {
					setSize(700, 400);
				} else {
					setSize(1000, 400);
				}

				if (t.getValueAt(t.getSelectedRow(), 2).toString()
						.equals(getone("select u_id from user where u_no=" + uno))) {
					edit.setVisible(true);
					title.setEnabled(true);
					area.setEnabled(true);
				} else {
					edit.setVisible(false);
					title.setEnabled(false);
					area.setEnabled(false);

					if (!Board.e.isVisible()) {
						execute("update notice set n_viewcount = n_viewcount + 1 where n_no = "
								+ t.getValueAt(t.getSelectedRow(), 0));
						data();
					}
				}

				Board.e.setVisible(!Board.e.isVisible());

			};
		});

		t.getColumn("제목").setPreferredWidth(180);
		t.setRowHeight(30);

		area.setLineWrap(true);

		e.setVisible(false);
		prev.setEnabled(false);
		edit.setVisible(false);

		this.setVisible(true);
	}

	void data() {
		list.clear();

		try {
			String sql = "SELECT n_no, n_title, u_id, n_date, n_viewcount FROM 2022지방_1.notice n, user u where n.u_no = u.u_no and n_open = 1";
			if (com.getSelectedIndex() == 0) {
				sql += " and n_title like '%" + search.getText() + "%'";
			} else {
				sql += " and u.u_id like '%" + search.getText() + "%'";
			}
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}

				list.add(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		max = list.size() % 10 == 0 ? list.size() / 10 : list.size() / 10 + 1;

		changeTable();
	}

	void changeTable() {
		page.setText(cnt + "/" + max);
		m.setRowCount(0);

		var idx = (cnt - 1) * 10;
		for (int i = idx; i < (idx + 10 < list.size() ? idx + 10 : list.size()); i++) {
			m.addRow(list.get(i));
		}
	}

	void search() {
		data();
	}

	public static void main(String[] args) {
		uno = "1";
		new Board();
	}
}
