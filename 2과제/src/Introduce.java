import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Introduce extends Baseframe {

	JScrollPane jsc;
	JPanel nw = new JPanel(), ne, cc;
	JLabel cname, tname = new JLabel(), img = new JLabel(), explan = new JLabel();
	JLabel lbl[] = { new JLabel(), new JLabel(), new JLabel(), new JLabel() };

	ArrayList<Button> btns = new ArrayList<Introduce.Button>();

	String str[] = "장르 :,최대 인원 :,시간 :,가격 :".split(",");

	public Introduce() {
		super("지점소개", 700, 600);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(null));

		try {
			var rs = stmt.executeQuery("select * from cafe where c_no='" + cno + "'");
			if (rs.next()) {
				lbl[3].setText(df.format(rs.getInt("c_price")));
				for (var tno : rs.getString(3).split(",")) {
					var btn = new Button(toInt(tno));
					btns.add(btn);
					nw.add(sz(btn, 45, 45));
					img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("Datafiles/테마/" + tno + ".jpg")
							.getScaledInstance(this.getWidth() / 2, this.getHeight() - 150, 4)));
					tname.setText(getone("select t_name from theme where t_no = " + tno));
					explan.setText("<html>" + getone("select t_explan from theme where t_no = " + tno));

					var gno = getone("select g_no from theme where t_no=" + tno);
					lbl[0].setText(getone("select g_name from genre where g_no=" + gno));
					lbl[1].setText(getone("select t_personnel from theme where t_no=" + tno) + "명");
					lbl[2].setText(getone("select t_time from theme where t_no=" + tno) + "분");
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (nw.getComponentCount() <= 3) {
			n.add(nw, "West");
		} else {
			n.add(sz(new JScrollPane(nw), 150, 75), "West");
		}

		n.add(ne = new JPanel(), "East");

		ne.add(btn("예약하기", e -> {
			btns.forEach(btn -> {
				if (btn.isSelected) {
					tno = btn.no;
				}
			});

			new Reserve().addWindowListener(new Before(Introduce.this));
		}));

		c.add(cname = lbl2(getone("select c_name from cafe where c_no='" + cno + "'"), 2, 25), "North");
		c.add(img);
		c.add(tname);
		c.add(explan);

		cname.setBounds(0, 0, 300, 30);
		img.setBounds(0, 40, this.getWidth() / 2, this.getHeight() - 150);
		tname.setBounds(360, 40, 300, 30);
		explan.setBounds(360, 150, 300, 200);

		for (int i = 0; i < str.length; i++) {
			JLabel lbl = new JLabel(str[i]);

			c.add(lbl);
			c.add(this.lbl[i]);
			lbl.setBounds(360, 400 + i * 20, 200, 25);

			if (i == 1) {
				this.lbl[i].setBounds(440, 400 + i * 20, 200, 25);
			} else {
				this.lbl[i].setBounds(410, 400 + i * 20, 200, 25);
			}

			this.lbl[i].setFont(new Font("HY헤드라인M", Font.TYPE1_FONT, 15));
			this.lbl[i].setForeground(Color.WHITE);
			lbl.setFont(new Font("HY헤드라인M", Font.TYPE1_FONT, 15));
			lbl.setForeground(Color.WHITE);
		}

		tname.setForeground(Color.WHITE);
		explan.setForeground(Color.WHITE);
		cname.setForeground(Color.ORANGE);
		c.setBackground(Color.BLACK);

		tname.setFont(new Font("HY헤드라인M", Font.TYPE1_FONT, 20));
		explan.setFont(new Font("HY헤드라인M", Font.TYPE1_FONT, 20));

		this.setVisible(true);
	}

	class Button extends JButton {
		int no;
		boolean isSelected;

		BufferedImage master;
		BufferedImage grayScale;

		public Button(int no) {
			this.no = no;

			try {
				master = ImageIO.read(new File("Datafiles/테마/" + no + ".jpg"));
				grayScale = ImageIO.read(new File("Datafiles/테마/" + no + ".jpg"));
				ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
				op.filter(grayScale, grayScale);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.addActionListener(e -> {
				btns.forEach(x -> {
					if (x.isSelected) {
						x.isSelected = false;
						x.repaint();
						x.revalidate();
					}
				});

				img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("Datafiles/테마/" + no + ".jpg")
						.getScaledInstance(350, 450, 4)));
				tname.setText(getone("select t_name from theme where t_no = " + no));
				explan.setText("<html>" + getone("select t_explan from theme where t_no = " + no));
				var gno = getone("select g_no from theme where t_no=" + no);
				lbl[0].setText(getone("select g_name from genre where g_no=" + gno));
				lbl[1].setText(getone("select t_personnel from theme where t_no=" + no) + "명");
				lbl[2].setText(getone("select t_time from theme where t_no=" + no) + "분");

				isSelected = true;
			});

			this.setToolTipText(getone("select t_name from theme where t_no=" + no));
			this.setBorder(BorderFactory.createEmptyBorder());

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(isSelected ? master : grayScale, 0, 0, 45, 45, this);
		}
	}
}
