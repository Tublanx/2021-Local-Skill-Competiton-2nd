import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Reserve extends Baseframe {

	JLabel time, date, price;
	JTextField txt;

	String str[] = "날짜,지점,테마,시간,가격,인원수,총금액".split(",");
	int total;

	public Reserve() {
		super("예약", 800, 500);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(new BorderLayout()));
		
		

		this.setVisible(true);
	}

	public static void main(String[] args) {
		new Reserve();
	}
}
