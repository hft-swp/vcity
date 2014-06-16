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

	/**
	 * Returns the height of the icon in px.
	 */
	@Override
	public int getIconHeight() {
		return 60;
	}

	/**
	 * Returns the width of the icon in px.
	 */
	@Override
	public int getIconWidth() {
		return 60;
	}

	/**
	 * Method for drawing the icon/picture.
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(this.img, 0, 0, c);
//		g.drawImage(this.img, 0, 0, 60, 60, c);
	}

}
