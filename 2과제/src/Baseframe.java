import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Baseframe extends JFrame {

	static Connection con = DB.con;
	static Statement stmt = DB.stmt;

	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();

	static {
		try {
			stmt.execute("use 2022지방_1");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", 0);
	}

	void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", 1);
	}

	String getone(String sql) {
		try {
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}
		} catch (SQLException e) {
			return null;
		}
	}

	int toInt(String s) {
		return Integer.parseInt(s);
	}

	JLabel lbl1(String t, int a, int s) {
		var l = new JLabel(t, a);
		l.setFont(new Font("", Font.TYPE1_FONT, s));
		return l;
	}

	JLabel lbl2(String t, int a, int s) {
		var l = new JLabel(t, a);
		l.setFont(new Font("HY헤드라인M", Font.TYPE1_FONT, s));
		return l;
	}

	JButton btn(String t, ActionListener a) {
		var b = new JButton(t);
		b.addActionListener(a);
		return b;
	}

	<T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	DefaultTableModel model(String[] col) {
		var m = new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		return m;
	}

	JTable table(DefaultTableModel m) {
		var t = new JTable(m);

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);

		t.setSelectionMode(0);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		return t;
	}

	public Baseframe(String t, int w, int h) {
		super(t);
		this.setSize(w, h);
		this.setDefaultCloseOperation(2);
		this.setLocationRelativeTo(null);
	}

	class Before extends WindowAdapter {
		Baseframe b;

		public Before(Baseframe b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}

}
