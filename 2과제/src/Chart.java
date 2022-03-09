import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Chart extends Baseframe {

	int points[][] = { { 286, 45 }, { 218, 80 }, { 242, 159 }, { 222, 165 }, { 161, 243 }, { 274, 294 }, { 280, 325 },
			{ 285, 229 }, { 342, 252 }, { 395, 403 }, { 303, 408 }, { 462, 447 }, { 438, 493 }, { 181, 367 },
			{ 110, 461 }, { 199, 491 }, { 274, 628 } };
	Color cols[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PINK,
			Color.MAGENTA, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.WHITE };

	ArrayList<GrayScaleImage> imgs = new ArrayList<>();
	ArrayList<GrayScaleImage> markers = new ArrayList<>();

	int ano, arc = 90;
//	int idx = -1

	public Chart() {
		super("차트", 1200, 800);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(sz(w = new JPanel(null), 600, 600), "West");
		this.add(c = sz(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				arc = 90;
				int height = 250;
				Graphics2D g2d = (Graphics2D) g;

				int sum = toInt(
						getone("select count(*) from reservation r, cafe c where c.c_no = r.c_no and a_no=" + ano));
				try {
					var rs = stmt.executeQuery(
							"select left(c.c_no, 1) as cno, count(*) as cnt, c_name from reservation r, cafe c where c.c_no = r.c_no and a_no="
									+ ano + " group by cno order by cnt desc");
					while (rs.next()) {
						var a = (int) Math.round(((double) rs.getInt(2) / (double) sum * 360) * -1);

						g2d.setColor(cols[rs.getRow() - 1]);
						g2d.fillArc(0, 250, 300, 300, arc, a);
						g2d.fillRect(350, height - 20 + 5, 20, 20);
						g2d.setColor(Color.BLACK);
						g2d.drawString(rs.getString(3).split(" ")[0], 375, height);

						arc += a;
						height += 25;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, 600, 600));

		n.add(lbl2("지역별 예약 현황", 0, 25));
		n.add(lbl2("C H A R T", 0, 20), "South");

		w.setBorder(new LineBorder(Color.black));

//		w.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent e) {
//				idx++;
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				points.add("{" + e.getX() + "," + e.getY() + "}");
//				System.out.println(String.join(",", points));
//			}
//		});

		try {
			var rs = stmt.executeQuery("select a_name, p_x, p_y from area a, ping p where a.a_no = p.a_no");
			int i = 0;
			while (rs.next()) {
				var img = new GrayScaleImage("Datafiles/지도/" + rs.getString(1) + ".png");
				var marker = new GrayScaleImage("Datafiles/마커.png", 25, 25);

				imgs.add(img);
				markers.add(marker);

				img.setName(rs.getString(1));

//				w.addMouseMotionListener(new MouseMotionListener() {
//
//					@Override
//					public void mouseMoved(MouseEvent e) {
//					}
//
//					@Override
//					public void mouseDragged(MouseEvent e) {
//						w.add(img[idx]).setBounds(e.getX(), e.getY(), img[idx].width, img[idx].height);
//					}
//				});

				marker.setName(rs.getString(1));
				marker.setToolTipText(rs.getString(1));
				marker.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var lbl = (GrayScaleImage) e.getSource();

						markers.forEach(x -> {
							x.isSelected = x.getName().equals(lbl.getName());
							repaint();
						});

						lbl.isSelected = true;

						imgs.forEach(x -> {
							x.isSelected = x.getName().equals(lbl.getName());
							if (x.isSelected) {
								ano = toInt(getone("select a_no from area where a_name='" + lbl.getName() + "'"));
							}
							
							repaint();
						});
					}
				});

				w.add(marker).setBounds(rs.getInt(2), rs.getInt(3), 25, 25);
				w.add(img).setBounds(points[rs.getRow() - 1][0], points[rs.getRow() - 1][1], img.width, img.height);

				w.setComponentZOrder(marker, 0);
			}
		} catch (Exception e) {
		}

		repaint();

		this.setVisible(true);
	}

	class GrayScaleImage extends JLabel {
		int width, height;
		boolean isSelected;

		BufferedImage master, gray;

		public GrayScaleImage(String path) {
			try {
				master = ImageIO.read(new File(path));
				gray = ImageIO.read(new File(path));

				var op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
				op.filter(gray, gray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.width = master.getWidth();
			this.height = master.getHeight();

			repaint();
		}

		public GrayScaleImage(String path, int w, int h) {
			this(path);
			this.width = w;
			this.height = h;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(isSelected ? master : gray, 0, 0, width, height, this);
		}
	}

	public static void main(String[] args) {
		new Chart();
	}
}
