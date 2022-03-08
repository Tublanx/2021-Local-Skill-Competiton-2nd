import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyPage extends Baseframe {

	JPanel nw, ne;
	JComboBox<String> com;
	JLabel totalPrice;

	DefaultTableModel m = model("날짜,시간,카페 이름,테마명,인원수,가격,rno".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	public MyPage() {
		super("마이페이지", 700, 350);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(new BorderLayout()));
		this.add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		nw.add(lbl1("날짜 : ", 0, 13));
		nw.add(com = new JComboBox<String>());
		ne.add(btn("삭제", e -> {
			if (t.getSelectedRow() == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}

			if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0).toString()).isBefore(LocalDate.now())) {
				eMsg("지난 예약은 삭제할 수 없습니다.");
				return;
			}

			iMsg("삭제가 완료되었습니다.");
			execute("delete from reservation where r_no=" + t.getValueAt(t.getSelectedRow(), 6));
			data();
		}));

		c.add(jsc);

		s.add(totalPrice = lbl1("", 0, 13));

		com.addItem("전체");
		for (int i = 0; i < 12; i++) {
			com.addItem(i + 1 + "월");
		}

		data();

		com.addActionListener(e -> data());

		t.getColumn("시간").setPreferredWidth(50);
		t.getColumn("카페 이름").setPreferredWidth(120);
		t.getColumn("rno").setMinWidth(0);
		t.getColumn("rno").setMaxWidth(0);

		this.setVisible(true);
	}

	void data() {
		m.setRowCount(0);
		int sum = 0;

		try {
			var rs = stmt.executeQuery(
					"SELECT r_date, r_time, c_name, t_name, r_people, format(c_price*r_people, '#,##0'), r.r_no, c_price*r_people FROM 2022지방_1.reservation r, user u, cafe c, theme t where r.u_no = u.u_no and r.c_no = c.c_no and r.t_no = t.t_no and u.u_no = "
							+ uno
							+ (com.getSelectedIndex() == 0 ? "" : " and month(r_date) = " + com.getSelectedIndex()));
			while (rs.next()) {
				sum += rs.getInt(8);

				var row = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}

				m.addRow(row);
			}

			rs.last();
			if (rs.getRow() == 0) {
				eMsg("예약현황이 없습니다.");
				com.setSelectedIndex(0);
				data();
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		totalPrice.setText("총 금액 : " + df.format(sum));
	}

	public static void main(String[] args) {
		uno = "1";
		new MyPage();
	}
}
