import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Quiz extends Baseframe {

	Timer timer;
	JPanel ns;
	JLabel chance, quizNum, time, answer, img;
	JTextField txt;
	JButton btn;

	enum AnswerState {
		NONE, RIGHT, WRONG;
	}

	AnswerState state = AnswerState.NONE;

	LocalTime ti = LocalTime.of(0, 1, 50);
	int ch = 3;

	public Quiz(GameList gameList) {
		super("퀴즈", 500, 500);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(img = new JLabel(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("Datafiles/퀴즈/" + qno + ".jpg").getScaledInstance(500, 300, 4))) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;

				g2d.setColor(Color.RED);
				g2d.setStroke(new BasicStroke(5));

				if (AnswerState.RIGHT == state) {
					g2d.drawOval(0, 0, img.getWidth(), img.getHeight());
				} else if (AnswerState.WRONG == state) {
					g2d.drawLine(0, 0, img.getWidth(), img.getHeight());
					g2d.drawLine(img.getWidth(), 0, 0, img.getHeight());
				}
			}
		});
		this.add(s = new JPanel(), "South");

		n.add(quizNum = lbl2("퀴즈번호 : " + qno, 0, 25));
		n.add(ns = new JPanel(new BorderLayout()), "South");

		ns.add(chance = lbl2("기회 : " + ch + "번", 2, 20), "West");
		ns.add(time = lbl2("남은 시간 : " + ti.format(DateTimeFormatter.ofPattern("m:ss")), 0, 20), "East");

		s.add(answer = lbl2("답 입력 : ", 2, 12));
		s.add(txt = new JTextField(15));
		s.add(btn = btn("확인", e -> {
			if (getone("select q_answer from quiz where q_no=" + qno).equals(txt.getText().replaceAll("\\s+", ""))) {
				state = AnswerState.RIGHT;
				img.repaint();
				timer.stop();

				iMsg("Q" + qno + "번 문제를 통과하였습니다.");
				Quiz.this.setVisible(false);
				gameList.data();
				new GameList();
			} else {
				ch--;
			}

			chance.setText("기회 : " + ch + "번");

			if (ch == 0) {
				state = AnswerState.WRONG;
				img.repaint();
				timer.stop();

				eMsg("남은 기회가 없으므로 종료합니다.");
				dispose();
			}
		}));

		timer = new Timer(1000, e -> {
			ti = ti.minusSeconds(1);
			time.setText("남은 시간 : " + ti.format(DateTimeFormatter.ofPattern("m:ss")));

			if (ti.isBefore(LocalTime.of(0, 0, 1))) {
				eMsg("제한시간 초과로 종료합니다.");
				dispose();
			}
		});

		timer.start();

		((JPanel) getContentPane()).setBackground(Color.BLACK);
		btn.setBackground(Color.WHITE);

		quizNum.setForeground(Color.WHITE);
		chance.setForeground(Color.WHITE);
		time.setForeground(Color.WHITE);
		answer.setForeground(Color.WHITE);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (btn.getText().equals("선택")) {
					dispose();
				} else {
					int yn = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);

					if (yn == JOptionPane.YES_OPTION) {
						dispose();
					} else {
						setDefaultCloseOperation(0);
					}
				}
			}
		});

		n.setOpaque(false);
		ns.setOpaque(false);
		s.setOpaque(false);
		this.setVisible(true);
	}
}
