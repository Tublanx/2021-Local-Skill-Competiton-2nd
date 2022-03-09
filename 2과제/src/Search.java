import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends Baseframe {

	DefaultTableModel m = model("지역".split(","));
	JTable t = table(m);

	JPanel nn, ns;
	JPanel cc;
	JComboBox<String> com;
	JTextField txt;

	public Search() {
		super("검색", 800, 500);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(new BorderLayout()));

		n.add(nn = new JPanel(new FlowLayout(0)), "North");
		n.add(ns = new JPanel(new FlowLayout(2)), "South");

		nn.add(lbl2("방탈출 카페 검색", 0, 30));

		ns.add(new JLabel("장르"));
		ns.add(com = new JComboBox<String>());
		ns.add(new JLabel("테마"));
		ns.add(txt = new JTextField(15));
		ns.add(btn("검색", e -> search()));

		c.add(sz(new JScrollPane(t), 80, 350), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 3, 5, 5))));

		com.addItem("전체");

		try {
			var rs = stmt.executeQuery("select g_name from genre");
			while (rs.next()) {
				com.addItem(rs.getString(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		addRow(m, "select a_name from area");
		m.insertRow(0, new Object[] { "전국" });

		search();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setVisible(true);
	}

	void search() {
		cc.removeAll();

		try {
			String sql = "SELECT \r\n" + "    c.c_no, c.c_name, t.t_no\r\n" + "FROM\r\n" + "    2022지방_1.cafe c,\r\n"
					+ "    area a,\r\n" + "    genre g,\r\n" + "    theme t\r\n" + "WHERE\r\n"
					+ "    c.a_no = a.a_no AND t.g_no = g.g_no\r\n"
					+ "        AND FIND_IN_SET(t.t_no, c.t_no) and a.a_name like '%"
					+ (t.getSelectedRow() < 1 ? "" : t.getValueAt(t.getSelectedRow(), 0)) + "%' and g.g_name like '%"
					+ (com.getSelectedIndex() == 0 ? "" : com.getSelectedItem()) + "%' and t.t_name like '%"
					+ txt.getText() + "%' group by c.c_no order by c.c_no";

			var pst = con.prepareStatement(sql);
			var rs = pst.executeQuery();
			while (rs.next()) {
				var item = new Item(rs.getString(1), rs.getInt(3));
				cc.add(item);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		revalidate();
		repaint();
	}

	class Item extends JPanel {
		String cno;

		public Item(String cno, int tno) {
			super(new BorderLayout());
			this.cno = cno;

			var name = getone("select c_name from cafe where c_no= '" + cno + "'");

			this.add(new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage("Datafiles/지점/" + name.split(" ")[0] + ".jpg").getScaledInstance(200, 100, 4))));
			this.add(new JLabel(name, 0), "South");

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						Baseframe.cno = cno;
						Baseframe.tno = tno;
						new Introduce().addWindowListener(new Before(Search.this));
					}
				}
			});

			this.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		}
	}

	public static void main(String[] args) {
		new Search();
	}
}
