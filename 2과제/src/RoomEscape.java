import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class RoomEscape extends Baseframe {

	List<String> files = new ArrayList<String>();
	List<JLabel> imgs = new ArrayList<JLabel>();

	Timer timer;
	JButton btn;

	int idx = 0;

	public RoomEscape(GameList gameList) {
		super("방탈출", 400, 450);

		this.add(c = new JPanel(new GridLayout(3, 3, 10, 10)));
		this.add(btn = btn("선택", e -> {
			if (e.getActionCommand().equals("선택")) {
				timer.stop();
				btn.setText("게임시작");
			} else {
				qno = imgs.get(idx - 1).getName();
				execute("update reservation set r_attend = 1 where r_no=" + rno);
				gameList.data();
				new Quiz(gameList).addWindowListener(new Before(RoomEscape.this));
			}
		}), "South");

		int length = new File("Datafiles/퀴즈/").listFiles().length;

		while (files.size() != 9) {
			String idx = new Random().nextInt(length) + 1 + "";

			if (!files.contains(idx)) {
				files.add(idx);

				JLabel img = new JLabel(new ImageIcon(
						Toolkit.getDefaultToolkit().getImage("Datafiles/퀴즈/" + files.get(files.size() - 1) + ".jpg")
								.getScaledInstance(150, 150, 4)));
				imgs.add(img);
				img.setName(files.get(files.size() - 1) + "");
				c.add(imgs.get(imgs.size() - 1));
				imgs.get(imgs.size() - 1).setEnabled(false);
			}
		}

		Collections.shuffle(imgs);

		for (int i = 0; i < 5; i++) {
			imgs.get(i).setEnabled(true);
		}

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (btn.getText().equals("선택")) {
					dispose();
				} else {
					int yn = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);

					if (yn == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend = 1 where r_no=" + rno);
						gameList.data();
						dispose();
					} else {
						setDefaultCloseOperation(0);
					}
				}
			}
		});

		timer = new Timer(100, e -> {
			imgs.forEach(x -> x.setBorder(new LineBorder(Color.BLACK)));
			imgs.get(idx).setBorder(new LineBorder(Color.RED));

			idx = (idx == 4 ? 0 : idx + 1);

			repaint();
			revalidate();
		});

		timer.start();

		this.setVisible(true);
	}

}
