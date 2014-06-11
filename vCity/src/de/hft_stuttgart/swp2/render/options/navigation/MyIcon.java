package de.hft_stuttgart.swp2.render.options.navigation;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;

public class MyIcon implements Icon {
	private Image img;

	public MyIcon(String path) {
		try {
			this.img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getIconHeight() {
		return 75;
	}

	@Override
	public int getIconWidth() {
		return 75;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(this.img, 0, 0, c);
	}

}
