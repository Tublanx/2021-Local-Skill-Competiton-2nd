import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Reserve extends Baseframe {

	JPanel ce, ec, es;
	JLabel time, date, price;
	JLabel lbl[] = new JLabel[3];
	JTextField txt;

	Cal cal;

	String str[] = "날짜,지점,테마,시간,가격,인원수,총금액".split(",");
	int total;

	public Reserve() {
		super("예약", 800, 450);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(new BorderLayout()));

		n.add(lbl2("방탈출 예약", 0, 30));
		n.add(lbl2("Room Escape Reservation", 0, 15), "South");

		c.add(sz(cal = new Cal(), 300, 300), "West");
		c.add(sz(new Time(), 10, 600));
		c.add(ce = sz(new JPanel(new BorderLayout()), 350, 400), "East");

		ce.add(ec = new JPanel(new GridLayout(0, 1)));
		ce.add(es = new JPanel(), "South");

		System.out.println(tno);
		
		for (int i = 0; i < str.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));
			var lbl = sz(new JLabel(str[i], 2), 100, 20);
			var info = new JLabel();

			if (i == 0) {
				info = date = new JLabel(cal.today + "", 2);
			} else if (i == 1) {
				info = new JLabel(getone("select c_name from cafe where c_no='" + cno + "'"), 2);
			} else if (i == 2) {
				info = new JLabel(getone("select t_name from theme where t_no=" + tno), 2);
			} else if (i == 3) {
				info = time = new JLabel(LocalTime.of(LocalTime.now().getHour() + 1, 0) + "", 2);
			} else if (i == 4) {
				info = new JLabel(getone("select format(c_price, '#,##0') from cafe where c_no='" + cno + "'"), 2);
			} else if (i == 6) {
				info = price = new JLabel("0", 2);
			}

			tmp.add(lbl);
			tmp.add(i == 5 ? txt = new JTextField(20) : info);

			ec.add(tmp);

			lbl.setForeground(Color.WHITE);
			info.setForeground(Color.WHITE);
			tmp.setBackground(Color.BLACK);
		}

		txt.addKeyListener(new KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent e) {
				var input = toInt(txt.getText());
				var price = toInt(getone("select c_price from cafe where c_no='" + cno + "'"));

				if (input < 1) {
					eMsg("인원수를 확인하세요.");
					txt.setText("");
					Reserve.this.price.setText("");
					txt.requestFocus();
					return;
				}

				if (input > toInt(getone("select t_personnel from theme where t_no=" + tno))) {
					eMsg("인원을 초과하였습니다.");
					txt.setText("");
					Reserve.this.price.setText("");
					txt.requestFocus();
					return;
				}

				Reserve.this.price.setText(df.format(input * price));
			};
		});

		es.add(btn("예약", e -> {
			if (txt.getText().isEmpty()) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			iMsg("예약이 완료되었습니다.");
			execute("insert reservation values(0, '" + uno + "','" + cno + "','" + tno + "','" + date.getText() + "','"
					+ time.getText() + "','" + txt.getText() + "','" + 0 + "')");
			new Main().addWindowListener(new Before(Reserve.this));
		}));

		es.add(btn("취소", e -> dispose()));

		es.setBackground(Color.BLACK);
		this.setVisible(true);
	}

	class Cal extends JPanel {

		JPanel n, c, cn, cc;
		JLabel prev, date, next;
		JLabel days[] = new JLabel[42];

		LocalDate today = LocalDate.now();

		public Cal() {
			super(new BorderLayout());

			this.add(n = sz(new JPanel(new BorderLayout()), 1, 30), "North");
			this.add(c = new JPanel(new BorderLayout(50, 50)));

			n.add(prev = new JLabel("◁", 0), "West");
			n.add(date = new JLabel("Apr, 5, 2022", 0));
			n.add(next = new JLabel("▷", 0), "East");

			c.add(cn = new JPanel(new FlowLayout(1, 10, 10)), "North");
			c.add(cc = new JPanel(new GridLayout(0, 7)));

			for (int i = 0; i < DayOfWeek.values().length; i++) {
				cn.add(lbl2(DayOfWeek.values()[i == 0 ? 6 : i - 1].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
						.toUpperCase(), 2, 15));
			}

			for (int i = 0; i < days.length; i++) {
				cc.add(days[i] = new JLabel("", 0));

				days[i].addMouseListener(new MouseAdapter() {
					public void mousePressed(java.awt.event.MouseEvent e) {
						JLabel lbl = (JLabel) e.getSource();

						if (!lbl.isEnabled())
							return;

						Arrays.stream(days).forEach(x -> x.setBackground(null));

						var tmp = LocalDate.of(today.getYear(), today.getMonthValue(), toInt(lbl.getText()));

						today = tmp;
						date.setText(tmp.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", "
								+ tmp.getDayOfMonth() + ", " + tmp.getYear());
						Reserve.this.date.setText(today + "");
						time.setText("");

						lbl.setOpaque(true);
						lbl.setBackground(Color.ORANGE);
					};
				});
			}

			prev.addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					today = today.minusMonths(1);
					drawCal();
				};
			});

			next.addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					today = today.plusMonths(1);
					drawCal();
				};
			});

			drawCal();

			n.setBackground(Color.BLACK);
			prev.setForeground(Color.WHITE);
			date.setForeground(Color.WHITE);
			next.setForeground(Color.WHITE);

			this.setBorder(new EmptyBorder(0, 0, 0, 10));
		}

		void drawCal() {
			int year = today.getYear();
			int month = today.getMonthValue();

			LocalDate startDay = LocalDate.of(year, month, 1);
			int start = startDay.getDayOfWeek().getValue();

			for (int i = 0; i < days.length; i++) {
				LocalDate ld = startDay.plusDays(i - start);

				days[i].setOpaque(false);
				days[i].setForeground(null);
				days[i].setVisible(false);
				days[i].setEnabled(false);

				if (ld.getMonthValue() == today.getMonthValue()) {
					days[i].setText(ld.getDayOfMonth() + "");
					days[i].setVisible(true);

					LocalDate l = LocalDate.of(year, month, toInt(days[i].getText()));

					if (LocalDate.now().toEpochDay() == l.toEpochDay()) {
						days[i].setOpaque(true);
						days[i].setBackground(Color.ORANGE);
					}

					days[i].setEnabled(!LocalDate.now().isAfter(l));
				}
			}
		}
	}

	class Time extends JPanel {

		JPanel p1, p2;
		JLabel title, up, down; // title은 '시간'으로 되어있는 레이블
		ArrayList<JLabel> times = new ArrayList<JLabel>();

		LocalTime time = LocalTime.of(LocalTime.now().getHour() + 1, 0);

		public Time() {
			super(new GridLayout(1, 0, 5, 5));

			this.add(p1 = new JPanel(new GridLayout(16, 1)));
			this.add(p2 = new JPanel(new BorderLayout()));

			p2.add(up = new JLabel("▲", 0), "North");
			p2.add(down = new JLabel("▼", 0), "South");

			up.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (time.minusHours(7).getHour() - 1 == LocalTime.now().getHour()) {
						return;
					}

					time = time.minusHours(15);
					setTime();
				}
			});

			down.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					setTime();
				}
			});

			setTime();

			p2.setBorder(new LineBorder(Color.BLACK));
		}

		void setTime() {
			p1.removeAll();
			p1.add(title = new JLabel("시간", 0));

			while (p1.getComponentCount() < 16 && time.isAfter(LocalTime.of(0, 0))) {
				JLabel lbl = new JLabel(time + "", 0);

				p1.add(lbl);
				times.add(lbl);

				lbl.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						JLabel lbl = (JLabel) e.getSource();

						times.forEach(x -> x.setBackground(null));
						lbl.setBackground(Color.ORANGE);
						Reserve.this.time.setText(lbl.getText());
					};
				});

				time = time.plusMinutes(30);
				lbl.setOpaque(true);
				lbl.setBackground(
						time.getHour() == LocalTime.now().getHour() + 1 && time.getMinute() == 0 ? Color.ORANGE : null);
			}

			title.setOpaque(true);
			title.setForeground(Color.WHITE);
			title.setBackground(Color.BLACK);

			p1.repaint();
			p1.revalidate();
		}
	}
}
